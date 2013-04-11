//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Jun 2011  Brian Frank  My birthday!
//
package haystack;

/**
 * ParseException is thrown when there is an exception read
 * from HReader.
 */
public class ParseException extends RuntimeException
{

  /** Constructor with message and null cause */
  public ParseException(String msg)
  {
    super(msg);
  }

  /** Constructor with message and null cause */
  public ParseException(String msg, Throwable cause)
  {
    super(msg, cause);
  }

}