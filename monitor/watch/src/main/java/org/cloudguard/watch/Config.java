package org.cloudguard.watch;

import java.util.Objects;

public class Config {
    private String publicKey;
    private String privateKey;
    private String in;
    private String encrypted;
    private String decrypted;

    public Config(String publicKey, String privateKey, String in, String encrypted, String decrypted) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.in = in;
        this.encrypted = encrypted;
        this.decrypted = decrypted;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }

    public String getDecrypted() {
        return decrypted;
    }

    public void setDecrypted(String decrypted) {
        this.decrypted = decrypted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Config)) return false;
        Config config = (Config) o;
        return getPublicKey().equals(config.getPublicKey()) &&
                getPrivateKey().equals(config.getPrivateKey()) &&
                getIn().equals(config.getIn()) &&
                getEncrypted().equals(config.getEncrypted()) &&
                getDecrypted().equals(config.getDecrypted());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPublicKey(), getPrivateKey(), getIn(), getEncrypted(), getDecrypted());
    }

    @Override
    public String toString() {
        return "Config{" +
                "publicKey='" + publicKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", in='" + in + '\'' +
                ", encrypted='" + encrypted + '\'' +
                ", decrypted='" + decrypted + '\'' +
                '}';
    }
}
