package me.nathena.infra.crypto;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public final class Base64Coder {

    public Base64Coder() {
    }

    public static byte[] decode(byte[] data) 
    {
    	return Base64.decodeBase64(data);
    }
    
    public static byte[] decode(String data)
    {
    	return Base64.decodeBase64(data.replaceAll("\\s", "+"));
    }

    public static String encode(byte[] data) 
    {
    	return Base64.encodeBase64String(data);
    }
    
    public static String encode(String data) throws UnsupportedEncodingException
    {
    	return Base64.encodeBase64String(data.getBytes("UTF-8"));
    }

}
