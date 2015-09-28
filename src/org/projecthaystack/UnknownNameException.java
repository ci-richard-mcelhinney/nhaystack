//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Jun 2011  Brian Frank  My birthday!
//
package org.projecthaystack;

/**
 * UnknownNameException is thrown when attempting to perform
 * a checked lookup by name for a tag/col not present.
 */
public class UnknownNameException extends RuntimeException
{

  /** Constructor with message */
  public UnknownNameException(String msg)
  {
    super(msg);
  }

}