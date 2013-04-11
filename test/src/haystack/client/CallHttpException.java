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
 * CallHttpException is thrown by HClient when communication
 * is successful with a server, but we receive an error HTTP
 * error response.
 */
public class CallHttpException extends CallException
{

  /** Constructor with code such as 404 and response message */
  public CallHttpException(int code, String msg)
  {
    super("" + code  + ": " + msg);
    this.code = code;
  }

  /** Response code such as 404 */
  public final int code;

}