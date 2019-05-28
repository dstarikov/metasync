import os
import sys
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend

BUFFER_SIZE = 1024

def AESEncrypt(key, pfile):
    backend = default_backend()
    iv = os.urandom(16)
    cipher = Cipher(algorithms.AES(key), modes.CTR(iv), backend=backend)
    encryptor = cipher.encryptor()

    buf = pfile.read(BUFFER_SIZE)
    ct = bytes()
    while buf != b'':
        ct = ct + encryptor.update(buf)
        buf = pfile.read(BUFFER_SIZE)

    return iv + ct + encryptor.finalize()

def AESDecrypt(key, cfile):
    backend = default_backend()
    iv = cfile.read(16)
    cipher = Cipher(algorithms.AES(key), modes.CTR(iv), backend=backend)
    decryptor = cipher.decryptor()

    buf = cfile.read(BUFFER_SIZE)
    pt = bytes()
    while buf != b'':
        pt = pt + decryptor.update(buf)
        buf = cfile.read(BUFFER_SIZE)

    return pt + decryptor.finalize()

def main():
    pfile = open(sys.argv[1], "rb")
    cfile = open(sys.argv[2], "wb")
    kfile = open(sys.argv[3], "wb")

    key = os.urandom(32)
    kfile.write(key)
    kfile.close()

    ct = AESEncrypt(key, pfile)
    cfile.write(ct)

    cfile.close()
    cfile = open(sys.argv[2], "rb")
    pt = AESDecrypt(key, cfile)

if __name__ == '__main__':
    main()
