//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   27 Sep 2012  Brian Frank  Creation
//
package haystack.client;

import haystack.*;

/**
 * CallNetworkException is thrown by HClient when there is a network I/O
 * or connection problem with communication to the server.
 */
public class CallNetworkException extends CallException
{

  /** Constructor with cause exception */
  public CallNetworkException(Exception cause)
  {
    super(cause.toString(), cause);
  }

}