//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Jan 2013  Mike Jarmy  Creation
//
package haystack.util;

import java.io.*;

/**
  * Base64 handles various methods of encoding and decoding
  * base 64 format.
  */
public class Base64
{
  /**
    * Return a Base64 codec that uses standard Base64 format.
    */
  public static Base64 STANDARD = new Base64(
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray(),
    '=');

  /**
    * Return a Base64 codec that uses a custom, Uri-friendly Base64 format.
    * <p>
    * This codec <i>mostly</i> follows the RFC 3548 standard for Base64.
    * It uses '-' and '_' instead of '+' and '/' (as per RFC 3548),
    * but uses use '~' as padding instead of '=' (this is the non-standard part).
    * <p>
    * This approach allows us to encode and decode HRef instances.
    * HRef has five special chars available for us to use: ':', '.', '-', '_', '~'.
    * We are using three of them here, leaving two still available: ':' and '.'
    */
  public static Base64 URI = new Base64(
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray(),
    '~');

////////////////////////////////////////////////////////////////
// constructor
////////////////////////////////////////////////////////////////

  private Base64(char[] alphabet, char padding)
  {
    this.alphabet = alphabet;
    for(int i = 0; i < alphabet.length; i++)
      this.charIdx[alphabet[i]] = i;

    this.padding = padding;
    this.pad1 = new String(new char[] { padding });
    this.pad2 = new String(new char[] { padding, padding });
  }

////////////////////////////////////////////////////////////////
// API
////////////////////////////////////////////////////////////////

  /**
    * Encode the string to base 64, using the platform's default charset.
    */
  public String encode(String str)
  {
    return encodeBytes(str.getBytes());
  }

  /**
    * Encode the string to base 64, using the UTF8 charset.
    */
  public String encodeUTF8(String str)
  {
    try
    {
      return encodeBytes(str.getBytes("UTF8"));
    }
    catch (UnsupportedEncodingException e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
    * Decode the string from base 64, using the platform's default charset.
    */
  public String decode(String str)
  {
    return new String(decodeBytes(str));
  }

  /**
    * Decode the string from base 64, using the UTF8 charset.
    */
  public String decodeUTF8(String str)
  {
    try
    {
      return new String(decodeBytes(str), "UTF8");
    }
    catch (UnsupportedEncodingException e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
    * Encode the byte array to base 64.
    */
  public String encodeBytes(byte[] bytes)
  {
    int size = bytes.length;
    char[] arr = new char[((size + 2) / 3) * 4];

    int a = 0;
    int i = 0;
    while (i < size)
    {
      byte b0 = bytes[i++];
      byte b1 = (i < size) ? bytes[i++] : 0;
      byte b2 = (i < size) ? bytes[i++] : 0;

      int mask = 0x3F;
      arr[a++] = alphabet[(b0 >> 2) & mask];
      arr[a++] = alphabet[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
      arr[a++] = alphabet[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
      arr[a++] = alphabet[b2 & mask];
    }

    switch(size % 3)
    {
      case 1: arr[--a] = padding;
      case 2: arr[--a] = padding;
    }

    return new String(arr);
  }

  /**
    * Decode the byte array from base 64.
    */
  public byte[] decodeBytes(String str)
  {
    int delta = str.endsWith(pad2) ? 2 : str.endsWith(pad1) ? 1 : 0;
    byte[] bytes = new byte[str.length()*3/4 - delta];

    int mask = 0xFF;
    int index = 0;
    for (int i = 0; i < str.length(); i += 4)
    {
      int c0 = charIdx[str.charAt(i)];
      int c1 = charIdx[str.charAt(i + 1)];
      bytes[index++] = (byte)(((c0 << 2) | (c1 >> 4)) & mask);
      if(index >= bytes.length)
        return bytes;

      int c2 = charIdx[str.charAt(i + 2)];
      bytes[index++] = (byte)(((c1 << 4) | (c2 >> 2)) & mask);
      if(index >= bytes.length)
        return bytes;

      int c3 = charIdx[str.charAt(i + 3)];
      bytes[index++] = (byte)(((c2 << 6) | c3) & mask);
    }

    return bytes;
  }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

  private final char[] alphabet;
  private final int[] charIdx = new int[128];

  private final char padding;
  private final String pad1;
  private final String pad2;
}