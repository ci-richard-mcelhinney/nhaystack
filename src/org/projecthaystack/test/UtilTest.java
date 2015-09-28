//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Jan 2013  Mike Jarmy  Creation
//
package org.projecthaystack.test;

import java.util.*;
import org.projecthaystack.util.*;
import org.projecthaystack.util.Base64;

/**
 * UtilTest tests the Base64 encoder
 */
public class UtilTest extends Test
{
  private static String randomString()
  {
    char[] chars = new char[RND.nextInt(100) + 1];
    for (int i = 0; i < chars.length; i++)
      chars[i] = (char) (RND.nextInt(127 - 32) + 32);
    return new String(chars);
  }

  public void testBase64() throws Exception
  {
    for (int i = 0; i < 1000; i++)
    {
      String s1 = randomString();

      String enc = Base64.STANDARD.encodeUTF8(s1);
      String s2 = Base64.STANDARD.decodeUTF8(enc);
      verifyEq(s1, s2);

      enc = Base64.STANDARD.encode(s1);
      s2 = Base64.STANDARD.decode(enc);
      verifyEq(s1, s2);

      enc = Base64.URI.encodeUTF8(s1);
      s2 = Base64.URI.decodeUTF8(enc);
      verifyEq(s1, s2);

      enc = Base64.URI.encode(s1);
      s2 = Base64.URI.decode(enc);
      verifyEq(s1, s2);
    }
  }

  private static Random RND;

  static
  {
    long seed = System.currentTimeMillis();
    // System.out.println("TestUtil SEED: " + seed);
    RND = new Random(seed);
  }
}