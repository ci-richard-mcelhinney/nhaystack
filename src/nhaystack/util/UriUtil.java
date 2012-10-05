//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   02 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.util;

/**
  * UriUtil has methods which allow for encoding a String
  * into an opaque, URI-safe format.  The encoding
  * is reversible so that you can get the original String
  * back again later.
  */
public abstract class UriUtil
{
    // WARNING: Non-standard Base64 encoding!  
    //
    // Hacked-up version of code found on StackOverflow at
    // http://stackoverflow.com/a/4265472
    // 
    // This encoding uses '-' and '_' instead of '+' and '/',
    // as per RFC 3548, and uses use '.' as padding instead 
    // of '=' (this is the non-standard part).
    //
    // This approach plays well with HRef, because HRef has four 
    // special chars for us to use: ":", ".", "-", "_".  
    // We are using ":" in NHId, and using up the other three here.
    //
    // It would be simpler to just use BigInteger and encode
    // to base-36, but sadly J2ME doesn't have BigInteger.

    /**
      * Encode a String into an opaque, URI-safe format.
      */
    public static String encodeToUri(String str)
    {
        try
        {
            return encodeBase64(str.getBytes("UTF8"));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
      * Decode a String that was encoded via encodeToUri()
      */
    public static String decodeFromUri(String str)
    {
        try
        {
            return new String(decodeBase64(str), "UTF8");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Translates the specified byte array into Base64 string.
     *
     * @param buf the byte array (not null)
     * @return the translated Base64 string (not null)
     */
    private static String encodeBase64(byte[] buf){
        int size = buf.length;
        char[] ar = new char[((size + 2) / 3) * 4];

        int a = 0;
        int i=0;
        while(i < size){
            byte b0 = buf[i++];
            byte b1 = (i < size) ? buf[i++] : 0;
            byte b2 = (i < size) ? buf[i++] : 0;

            int mask = 0x3F;
            ar[a++] = ALPHABET[(b0 >> 2) & mask];
            ar[a++] = ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
            ar[a++] = ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
            ar[a++] = ALPHABET[b2 & mask];
        }
        switch(size % 3){
            case 1: ar[--a]  = '.';
            case 2: ar[--a]  = '.';
        }
        return new String(ar);
    }

    /**
     * Translates the specified Base64 string into a byte array.
     *
     * @param s the Base64 string (not null)
     * @return the byte array (not null)
     */
    private static byte[] decodeBase64(String s){
        int delta = s.endsWith( ".." ) ? 2 : s.endsWith( "." ) ? 1 : 0;
        byte[] buffer = new byte[s.length()*3/4 - delta];

        int mask = 0xFF;
        int index = 0;
        for(int i=0; i< s.length(); i+=4){
            int c0 = CHARS[s.charAt( i )];
            int c1 = CHARS[s.charAt( i + 1)];
            buffer[index++]= (byte)(((c0 << 2) | (c1 >> 4)) & mask);
            if(index >= buffer.length){
                return buffer;
            }
            int c2 = CHARS[s.charAt( i + 2)];
            buffer[index++]= (byte)(((c1 << 4) | (c2 >> 2)) & mask);
            if(index >= buffer.length){
                return buffer;
            }
            int c3 = CHARS[s.charAt( i + 3 )];
            buffer[index++]= (byte)(((c2 << 6) | c3) & mask);
        }
        return buffer;
    } 

    private final static char[] ALPHABET = 
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();

    private static int[] CHARS = new int[128];

    static {
        for(int i=0; i< ALPHABET.length; i++){
            CHARS[ALPHABET[i]]= i;
        }
    }

////////////////////////////////////////////////////////////////
// Test
////////////////////////////////////////////////////////////////

//    private static String randomString(java.util.Random rnd)
//    {
//        char[] chars = new char[rnd.nextInt(100) + 1];
//        for (int i = 0; i < chars.length; i++)
//            chars[i] = (char) (rnd.nextInt(127 - 32) + 32);
//        return new String(chars);
//    }
//
//    public static void main(String[] args)
//    {
//        long seed = System.currentTimeMillis();
//        System.out.println("SEED: " + seed);
//        java.util.Random rnd = new java.util.Random(seed);
//
//        for (int i = 0; i < 1000; i++)
//        {
//            String s1 = randomString(rnd);
//            String enc = encodeToUri(s1);
//            String s2 = decodeFromUri(enc);
//
//            System.out.println(s1 + ", " + enc + ", " + s2);
//
//            if (!s1.equals(s2)) throw new IllegalStateException();
//        }
//    }
}
