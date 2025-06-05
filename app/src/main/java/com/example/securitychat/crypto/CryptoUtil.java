package com.example.securitychat.crypto;

import android.util.Base64;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoUtil {
    private CryptoUtil(){}
    private static final String ALGO = "AES";
    private static final String TR   = "AES/GCM/NoPadding";
    private static final int IV  = 12, TAG = 128;

    public static String generateKey(){
        byte[] k = new byte[32];
        new SecureRandom().nextBytes(k);
        return Base64.encodeToString(k, Base64.NO_WRAP);
    }
    public static String encrypt(String plain, String b64Key) throws Exception{
        byte[] iv=new byte[IV]; new SecureRandom().nextBytes(iv);
        SecretKey key = new SecretKeySpec(Base64.decode(b64Key,Base64.NO_WRAP), ALGO);
        Cipher c = Cipher.getInstance(TR);
        c.init(Cipher.ENCRYPT_MODE,key,new GCMParameterSpec(TAG,iv));
        byte[] ct = c.doFinal(plain.getBytes("UTF-8"));
        byte[] out=new byte[iv.length+ct.length];
        System.arraycopy(iv,0,out,0,iv.length);
        System.arraycopy(ct,0,out,iv.length,ct.length);
        return Base64.encodeToString(out,Base64.NO_WRAP);
    }
    public static String decrypt(String b64Cipher,String b64Key)throws Exception{
        byte[] all=Base64.decode(b64Cipher,Base64.NO_WRAP);
        byte[] iv=new byte[IV];
        byte[] ct=new byte[all.length-IV];
        System.arraycopy(all,0,iv,0,IV);
        System.arraycopy(all,IV,ct,0,ct.length);
        SecretKey key=new SecretKeySpec(Base64.decode(b64Key,Base64.NO_WRAP),ALGO);
        Cipher c=Cipher.getInstance(TR);
        c.init(Cipher.DECRYPT_MODE,key,new GCMParameterSpec(TAG,iv));
        return new String(c.doFinal(ct),"UTF-8");
    }
}
