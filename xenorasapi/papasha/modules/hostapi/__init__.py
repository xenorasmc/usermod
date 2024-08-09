from pyngrok import ngrok
from modules.hostapi.tcpapi import SocketAPI
import os

class NgrokApi():
    tunnel = None

    def __init__(self, token: str, host: str, port: int) -> None:
        try:
            os.remove("C:\\Users\\sanya\\AppData\\Local/ngrok/ngrok.yml")
            os.remove("C:\\Users\\sanya\\.ngrok2\\ngrok.yml")
        except:
            pass
        ngrok.set_auth_token(token)
        self.host = host
        self.port = port

    def start(self) -> None:
        self.tunnel = ngrok.connect(self.host+":"+str(self.port), "tcp")
        self.tunnel2 = ngrok.connect(self.host+":"+str(self.port+1), "tcp")

    def update(self) -> None:
        ngrok.disconnect(self.tunnel.public_url)
        ngrok.disconnect(self.tunnel2.public_url)
        self.tunnel = ngrok.connect(self.host+":"+str(self.port), "tcp")
        self.tunnel2 = ngrok.connect(self.host+":"+str(self.port+1), "tcp")

    def gethost(self) -> str:
        return self.tunnel.public_url.replace("tcp://", "")

    def gethostDiscs(self) -> str:
        return self.tunnel2.public_url.replace("tcp://", "")