//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Jun 2011  Brian Frank  My birthday!
//
package haystack;

/**
 * UnknownWatchException is thrown when attempting to perform
 * a checked lookup by of a watch by its identifier
 */
public class UnknownWatchException extends RuntimeException
{

  /** Constructor with message */
  public UnknownWatchException(String msg)
  {
    super(msg);
  }

}