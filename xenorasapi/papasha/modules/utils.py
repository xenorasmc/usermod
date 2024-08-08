from config import *
import os, json

def getcount(v):
    _ = json.loads(open(ADVFOLDER+v, 'r').read())
    l = 0
    for i in _.keys():
        if 'blazeandcave' in i:
            if not 'blazeandcave:bacap' in i and not 'blazeandcave:technical' in i:
                l += 1
    return l+1

def gettop():
    tmp = [i for i in os.listdir(ADVFOLDER)]
    _ = {k: getcount(v) for k, v in zip((i.split('.')[0] for i in tmp), tmp)}
    return list(reversed(sorted(_.items(), key=lambda x:x[1])))[:30]

emnum = {
    1: '1.',
    2: '2.',
    3: '3.',
    4: '4.',
    5: '5.',
    6: '6.',
    7: '7.',
    8: '8.',
    9: '9.',
    10: '10.',
    11: '11.',
    12: '12.',
    13: '13.',
    14: '14.',
    15: '15.'
}