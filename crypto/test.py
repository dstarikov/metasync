import os
import sys
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.asymmetric import padding
from cryptography.hazmat.primitives import hashes

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

def RSAEncrypt(public_key, plaintext):
    ciphertext = public_key.encrypt(
        plaintext,
        padding.OAEP(
            mgf=padding.MGF1(algorithm=hashes.SHA256()),
            algorithm=hashes.SHA256(),
            label=None
        )
    )

    return ciphertext

def RSADecrypt(private_key, ciphertext):
    plaintext = private_key.decrypt(
        ciphertext,
        padding.OAEP(
            mgf=padding.MGF1(algorithm=hashes.SHA256()),
            algorithm=hashes.SHA256(),
            label=None
        )
    )

    return plaintext


def main():
    pfile = open(sys.argv[1], "rb")
    cfile = open(sys.argv[2], "wb")
    kfile = open(sys.argv[3], "wb")


    # Encrypt
    AES_key = os.urandom(32)
    with open(sys.argv[4], "rb") as key_file:
        private_key = serialization.load_pem_private_key(
            key_file.read(),
            password=None,
            backend=default_backend()
        )

        public_key = private_key.public_key()
        ciphertext = RSAEncrypt(public_key, AES_key)
        kfile.write(ciphertext)
        kfile.close()
    ct = AESEncrypt(AES_key, pfile)
    cfile.write(ct)
    cfile.close()

    # Decrypt
    AES_key = b''
    with open(sys.argv[4], "rb") as key_file:
        private_key = serialization.load_pem_private_key(
            key_file.read(),
            password=None,
            backend=default_backend()
        )

        kfile = open(sys.argv[3], "rb")
        ciphertext = kfile.read()
        kfile.close()
        AES_key = RSADecrypt(private_key, ciphertext)

    cfile = open(sys.argv[2], "rb")
    pt = AESDecrypt(AES_key, cfile)

    print(pt)


if __name__ == '__main__':
    main()
