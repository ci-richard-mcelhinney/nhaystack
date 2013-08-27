//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   27 Sep 2012  Brian Frank  Creation
//
package haystack.client;

import org.projecthaystack.*;

/**
 * CallAuthException indicates authentication with server failed
 * and credentials are invalid.
 */
public class CallAuthException extends CallException
{

  /** Constructor with message */
  public CallAuthException(String msg)
  {
    super(msg);
  }

}