//
// Copyright (c) 2014, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   22 Apr 2014  Brian Frank  Creation
//

package org.projecthaystack.util;

import java.security.*;

/**
  * Crypto utilities
  */
public class CryptoUtil
{
  /**
   * Implementation of HMAC algorthm since Java's implementation
   * doesn't allow empty passwords
   */
  public static byte[] hmac(String algorithm, byte[] data, byte[] key)
    throws NoSuchAlgorithmException
  {
    // get digest algorthim
    MessageDigest md = MessageDigest.getInstance(algorithm);
    int blockSize = 64;

    // key is greater than block size we hash it first
    int keySize = key.length;
    if (keySize > blockSize)
    {
      md.update(key, 0, keySize);
      key = md.digest();
      keySize = key.length;
      md.reset();
    }

    // RFC 2104:
    //   ipad = the byte 0x36 repeated B times
    //   opad = the byte 0x5C repeated B times
    //   H(K XOR opad, H(K XOR ipad, text))

    // inner digest: H(K XOR ipad, text)
    for (int i=0; i<blockSize; ++i)
    {
      if (i < keySize)
        md.update((byte)(key[i] ^ 0x36));
      else
        md.update((byte)0x36);
    }
    md.update(data, 0, data.length);
    byte[] innerDigest = md.digest();

    // outer digest: H(K XOR opad, innerDigest)
    md.reset();
    for (int i=0; i<blockSize; ++i)
    {
      if (i < keySize)
        md.update((byte)(key[i] ^ 0x5C));
      else
        md.update((byte)0x5C);
    }
    md.update(innerDigest);

    // return result
    return md.digest();
  }

}