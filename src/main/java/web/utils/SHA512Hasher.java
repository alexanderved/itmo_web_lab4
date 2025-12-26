package web.utils;

import jakarta.ejb.Singleton;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Singleton(name = "hasher")
public class SHA512Hasher implements Hasher {
    private final MessageDigest md;

    public SHA512Hasher() throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance("SHA-512");
    }

    @Override
    public String hash(String pass, String salt) {
        String s = pass + salt;

        byte[] bytes = md.digest(s.getBytes());
        BigInteger num = new BigInteger(1, bytes);
        StringBuilder hashStringBuilder = new StringBuilder(num.toString(16));

        while (hashStringBuilder.length() < 128) {
            hashStringBuilder.insert(0, "0");
        }

        return hashStringBuilder.toString();
    }
}
