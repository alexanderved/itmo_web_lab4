package web.utils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public final class SaltGenerator {
    private static final Random RANDOM = new SecureRandom();

    public static String generateSalt() {
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);

        return Base64.getEncoder().encodeToString(saltBytes);
    }
}
