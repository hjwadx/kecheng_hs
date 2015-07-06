package fm.jihua.kecheng.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtil
{
	public static String encode(String s, byte[] key) {
        return parseByte2HexStr(xorWithKey(s.getBytes(), key));
    }

//    public String decode(String s, String key) {
//        return new String(xorWithKey(base64Decode(s), key.getBytes()));
//    }

    public static byte[] xorWithKey(byte[] a, byte[] key) {
    	byte[] big = a.length > key.length ? a : key;
    	byte[] tiny = big == a ? key : a;
        byte[] out = new byte[big.length];
        int diff = big.length - tiny.length;
        for (int i = big.length - 1; i >=0; i--) {
        	byte b = i - diff >= 0 ? tiny[i-diff] : 0x00; 
            out[i] = (byte) (big[i] ^ b);
        }
        return out;
    }
    

  public static String parseByte2HexStr(byte[] hash)
  {
	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) {
	        if ((b & 0xFF) < 0x10) hex.append("0");
	        hex.append(Integer.toHexString(b & 0xFF));
	    }
	    return hex.toString();
  }
  
  public static byte[] md5(String string) {
	    byte[] hash = {};
	    try {
	        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Huh, MD5 should be supported?", e);
	    } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException("Huh, UTF-8 should be supported?", e);
	    }
	    return hash;

//	    StringBuilder hex = new StringBuilder(hash.length * 2);
//	    for (byte b : hash) {
//	        if ((b & 0xFF) < 0x10) hex.append("0");
//	        hex.append(Integer.toHexString(b & 0xFF));
//	    }
//	    return hex.toString();
	}
}