//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   02 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.util;

import java.io.*;
import java.math.*;

import javax.baja.sys.*;

/**
  * UriUtil has methods which allow for encoding a String
  * into an opaque, Uri-friendly format.  The encoding
  * is reversible so that you can get the original String
  * back again later.
  */
public abstract class UriUtil
{
    /**
      * Encode a String into an opaque, Uri-friendly format.
      */
    public static String encodeToUri(String str)
    {
        try
        {
            // base 36
            BigInteger big = new BigInteger(str.getBytes("UTF8"));
            return big.toString(36);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new BajaRuntimeException(e);
        }
    }

    /**
      * Decode a String that was encoded via encodeToUri()
      */
    public static String decodeFromUri(String str)
    {
        try
        {
            // base 36
            BigInteger big = new BigInteger(str, 36);
            return new String(big.toByteArray(), "UTF8");
        }
        catch (Exception e)
        {
            throw new BajaRuntimeException(e);
        }
    }
}
