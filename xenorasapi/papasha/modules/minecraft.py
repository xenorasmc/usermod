from modules.mcapi import Minecraft

class MinecraftApi():
    def __init__(self, address, port, salt) -> None:
        self.mc = Minecraft.create(address = address, port = port, salt = salt)

    def check_player_online(self, name) -> bool:
        try:
            self.mc.getPlayerEntityId(name)
            return True
        except:
            return False

    def post(self, msg):
        try:
            self.mc.postToChat(msg)
            return True
        except:
            return False
        
    def call(self, name):
        if self.check_player_online(name):
            self.mc.xsCall(name)

    def namefromid(self, uuid):
        return self.mc.namefromid(uuid)
    
    def addwhitelist(self, name):
        self.mc.addwhitelist(name)