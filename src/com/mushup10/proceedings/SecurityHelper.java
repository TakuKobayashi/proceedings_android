package com.mushup10.proceedings;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

public class SecurityHelper {
  public static class RSA{
    public static String encrypt(String plain){
      String result = null;
      try {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(Config.PUBLIC_KEY, Base64.DEFAULT));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(spec);
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plain.getBytes("UTF-8"));
        result = Base64.encodeToString(encrypted, Base64.DEFAULT);
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      } catch (NoSuchPaddingException e) {
        e.printStackTrace();
      } catch (InvalidKeyException e) {
        e.printStackTrace();
      } catch (IllegalBlockSizeException e) {
        e.printStackTrace();
      } catch (BadPaddingException e) {
        e.printStackTrace();
      } catch (InvalidKeySpecException e) {
        e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      return result;
    }

    public static String decrypt(String encripted){
      String result = null;
      try {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(Config.PUBLIC_KEY, Base64.DEFAULT));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(spec);
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(encripted.getBytes("UTF-8"));
        result = new String(decrypted, "UTF8");
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      } catch (NoSuchPaddingException e) {
        e.printStackTrace();
      } catch (InvalidKeyException e) {
        e.printStackTrace();
      } catch (IllegalBlockSizeException e) {
        e.printStackTrace();
      } catch (BadPaddingException e) {
        e.printStackTrace();
      } catch (InvalidKeySpecException e) {
        e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      return result;
    }
  }

  public static class AES{
    public static String encrypt(String plain){
      String result = null;
      try {
        SecretKeySpec key = new SecretKeySpec(Config.ENCODE_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        IvParameterSpec iv = new IvParameterSpec(Config.IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encrypted = cipher.doFinal(plain.getBytes());
        result = Base64.encodeToString(encrypted, Base64.DEFAULT);
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      } catch (NoSuchPaddingException e) {
        e.printStackTrace();
      } catch (InvalidKeyException e) {
        e.printStackTrace();
      } catch (IllegalBlockSizeException e) {
        e.printStackTrace();
      } catch (BadPaddingException e) {
        e.printStackTrace();
      } catch (InvalidAlgorithmParameterException e) {
        e.printStackTrace();
      }
      return result;
    }

    public static String decrypt(String encripted){
      String result = null;
      try {
        SecretKeySpec keySpec = new SecretKeySpec(Config.DECODE_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(asByteArray(Config.IV));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
        byte[] decrypted = cipher.doFinal(encripted.getBytes());
        result = new String(decrypted);
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      } catch (NoSuchPaddingException e) {
        e.printStackTrace();
      } catch (InvalidKeyException e) {
        e.printStackTrace();
      } catch (IllegalBlockSizeException e) {
        e.printStackTrace();
      } catch (BadPaddingException e) {
        e.printStackTrace();
      } catch (InvalidAlgorithmParameterException e) {
        e.printStackTrace();
      }
      return result;
    }
  }

  // byte配列を16進で返す関数。
  private static String asHex(byte bytes[]) {
    StringBuffer strbuf = new StringBuffer(bytes.length * 2);
    for (int index = 0; index < bytes.length; index++) {
      int bt = bytes[index] & 0xff;
      if (bt < 0x10) {
        strbuf.append("0");
      }
      strbuf.append(Integer.toHexString(bt));
    }
    return strbuf.toString();
  }

  //16進数の文字列をバイト配列に変換する。
  private static byte[] asByteArray(String hex) {
    // 文字列長の1/2の長さのバイト配列を生成。
    byte[] bytes = new byte[hex.length() / 2];
    // バイト配列の要素数分、処理を繰り返す。
    for (int index = 0; index < bytes.length; index++) {
      // 16進数文字列をバイトに変換して配列に格納。
      bytes[index] = (byte) Integer.parseInt(hex.substring(index * 2, (index + 1) * 2), 16);
    }
    // バイト配列を返す。
    return bytes;
  }

}