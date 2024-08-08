from pyngrok import ngrok
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

    def update(self) -> None:
        ngrok.disconnect(self.tunnel.public_url)
        self.tunnel = ngrok.connect(self.host+":"+str(self.port), "tcp")

    def gethost(self) -> str:
        return self.tunnel.public_url.replace("tcp://", "")