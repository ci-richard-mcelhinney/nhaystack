//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   27 Sep 2012  Brian Frank  Creation
//
package org.projecthaystack.client;

import org.projecthaystack.*;

/**
 * CallException base class for exceptions thrown HClient.call.
 * Subclasses:
 * <ul>
 * <li>CallNetworkException: network communication error</li>
 * <li>CallHttpException: HTTP response error such as 404</li>
 * <li>CallErrException: server errors with server side stack trace</li>
 * <li>CallAuthException: authentication error</li>
 * <ul>
 */
public class CallException extends RuntimeException
{

  /** Constructor with message */
  public CallException(String msg)
  {
    super(msg);
  }

  /** Constructor with message and cause */
  public CallException(String msg, Throwable cause)
  {
    super(msg, cause);
  }

}