// app/src/main/java/com/example/securitychat/crypto/CryptoUtil.java
package com.example.securitychat.crypto;

import android.util.Base64;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoUtil {
    private CryptoUtil() {}
    private static final String ALGO = "AES";
    private static final String TRANSFORM = "AES/GCM/NoPadding";
    private static final int IV_LEN = 12;          // 96-битный IV
    private static final int TAG_LEN = 128;        // 128-битный тег аутентичности

    /** Создаёт случайный 256-битный ключ и отдаёт Base64-строкой */
    public static String generateKey() {
        byte[] k = new byte[32];
        new SecureRandom().nextBytes(k);
        return Base64.encodeToString(k, Base64.NO_WRAP);
    }

    /** Шифрует plaintext → base64(cipher | iv) */
    public static String encrypt(String plaintext, String b64Key) throws Exception {
        byte[] keyBytes = Base64.decode(b64Key, Base64.NO_WRAP);
        SecretKey key = new SecretKeySpec(keyBytes, ALGO);

        byte[] iv = new byte[IV_LEN];
        new SecureRandom().nextBytes(iv);

        Cipher c = Cipher.getInstance(TRANSFORM);
        c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LEN, iv));
        byte[] ct = c.doFinal(plaintext.getBytes("UTF-8"));

        byte[] out = new byte[iv.length + ct.length];
        System.arraycopy(iv, 0, out, 0, iv.length);
        System.arraycopy(ct, 0, out, iv.length, ct.length);
        return Base64.encodeToString(out, Base64.NO_WRAP);
    }

    /** Расшифровывает base64(cipher|iv) → plaintext */
    public static String decrypt(String b64Cipher, String b64Key) throws Exception {
        byte[] all = Base64.decode(b64Cipher, Base64.NO_WRAP);
        byte[] iv = new byte[IV_LEN];
        byte[] ct = new byte[all.length - IV_LEN];
        System.arraycopy(all, 0, iv, 0, IV_LEN);
        System.arraycopy(all, IV_LEN, ct, 0, ct.length);

        byte[] keyBytes = Base64.decode(b64Key, Base64.NO_WRAP);
        SecretKey key = new SecretKeySpec(keyBytes, ALGO);

        Cipher c = Cipher.getInstance(TRANSFORM);
        c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LEN, iv));
        byte[] pt = c.doFinal(ct);
        return new String(pt, "UTF-8");
    }
}
