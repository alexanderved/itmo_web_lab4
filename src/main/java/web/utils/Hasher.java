package web.utils;

public interface Hasher {
    String hash(String pass, String salt);
}