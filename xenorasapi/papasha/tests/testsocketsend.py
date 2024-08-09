import os
import socket, json

import tqdm

HOST = "localhost"  # The server's hostname or IP address
PORT = 25566  # The port used by the server
FILE = "test.exe"
BUFFER_SIZE = 4096

s = socket.socket()
s.connect((HOST, PORT))
s.send(json.dumps({"from": "app", "version": "v", "username": "un", "request": {"type": "disc", "link": "file", "filesize": os.path.getsize(FILE)}}).encode())
name = json.loads(s.recv(1024).decode())["response"]

progress = tqdm.tqdm(range(os.path.getsize(FILE)), f"Sending {FILE}", unit="B", unit_scale=True, unit_divisor=1024)
with open(FILE, "rb") as f:
    while True:
        # read the bytes from the file
        bytes_read = f.read(BUFFER_SIZE)
        if not bytes_read:
            # file transmitting is done
            break
        # we use sendall to assure transimission in
        # busy networks
        s.sendall(bytes_read)
        # update the progress bar
        progress.update(len(bytes_read))

s.close()

s = socket.socket()
s.connect((HOST, PORT))
s.send(json.dumps({"from": "app", "version": "v", "username": "un", "request": {"type": "getdisc", "name": name}}).encode())
print(s.recv(1024).decode())