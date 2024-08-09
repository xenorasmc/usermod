import os
import random
import socket
import threading
import json
import time

from pytubefix import YouTube
from pytubefix.cli import on_progress


class SocketAPI(threading.Thread):
    BUFFER_SIZE = 4096
    DISCS_LIST = "../../tests/"
    in_process = {}

    def __init__(self, db, host, port, botcfg, ytwork):
        super().__init__()
        self.db = db
        self.host = host
        self.port = port
        self.botcfg = botcfg
        self.ytwork = ytwork

    def download(self, video_url):
        print(f"[debug] downloading {video_url}")
        yt = YouTube(video_url, on_progress_callback = on_progress)
        ys = yt.streams.get_audio_only()
        name = str(random.randint(1, 100000000000))
        link_ = self.botcfg['link'] + name + '.mp3'
        ys.download(mp3=True, filename="../tests/"+name)
        self.db.add(int(name), link_, yt.title)
        return link_

    def download_from_file(self, c, request, name, link_):
        filesize = int(request["filesize"])
        readed = 0
        print("[debug] working with file "+name)
        with open(self.DISCS_LIST+name+".mp3", "wb") as f:
            while True:
                if readed >= filesize:
                    break
                bytes_read = c.recv(self.BUFFER_SIZE)
                readed += self.BUFFER_SIZE
                if not bytes_read:
                    break
                f.write(bytes_read)
        self.db.add(int(name), link_, "not stated")

    def client_thread(self, c):
        #json structure: {"from": "app", "version": "v", "username": "un", "request": "r"}
        #resp structure: {"status": "s", "error": "e", "response": "r"}
        try:
            while True:
                json_string = json.loads(c.recv(1024).decode())
                print(json_string)

                from_ = json_string["from"]
                version_ = json_string["version"]
                username_ = json_string["username"]
                request_ = json_string["request"]

                #print(f"[debug] work with request type {request_["type"]} from {username_}")

                if request_["type"] == "disc":
                    if request_["link"] == "file":
                        #c.send(json.dumps({"status": "error", "error": "chlen", "response":  None}).encode())
                        name = str(random.randint(1, 100000000000))
                        self.in_process[username_] = json.dumps({"status": "ok", "error": None, "response": name}).encode()
                        #c.send(json.dumps({"status": "ok", "error": None, "response": name}).encode())
                        link_ = self.botcfg['link'] + name + '.mp3'
                        time.sleep(5)
                        self.download_from_file(c, request_, name, link_)
                        c.close()
                        break
                    elif request_["link"] == "mylast":
                        if username_ in self.in_process.keys():
                            c.send(self.in_process[username_])
                            c.close()
                            break
                        else:
                            c.send(json.dumps({"status": "error", "error": "cant find your request", "response": None}).encode())
                            c.close()
                            break
                    else:
                        if self.ytwork:
                            resp = self.download(request_["link"])
                            c.send(json.dumps({"status": "ok", "error": None, "response": resp}).encode())
                            c.close()
                            break
                        else:
                            c.send(json.dumps({"status": "error", "error": "youtube not work in Russia", "response": None}).encode())
                            c.close()
                            break
                elif request_["type"] == "getdisc":
                    name = str(request_["name"])
                    print(name)
                    if os.path.exists(self.DISCS_LIST+name+".mp3"):
                        link_ = self.botcfg['link'] + name + '.mp3'
                        c.send(json.dumps({"status": "ok", "error": None, "response": link_}).encode())
                        c.close()
                        break
                    else:
                        c.send(json.dumps({"status": "error", "error": "invalid request", "response": None}).encode())
                        c.close()
                        break
                else:
                    c.send(json.dumps({"status": "error", "error": "invalid request", "response": None}).encode())
                    c.close()
                    break
        except:
            #c.send(json.dumps({"status": "error", "error": "invalid request", "response": None}).encode())
            c.close()
            print("socket closed")

    def run(self):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.bind((self.host, self.port))
        s.listen(5)

        while True:
            c, addr = s.accept()
            print('api connection from:', addr[0] + ':' + str(addr[1]))
            a = threading.Thread(target=self.client_thread, args=(c,))
            a.start()