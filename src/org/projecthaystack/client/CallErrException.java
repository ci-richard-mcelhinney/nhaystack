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
 * CallErrException is thrown then a HClient.call returns a
 * HGrid with the err marker tag indicating a server side error.
 */
public class CallErrException extends CallException
{

  /** Constructor with error grid */
  public CallErrException(HGrid grid)
  {
    super(msg(grid));
    this.grid = grid;
  }

  private static String msg(HGrid grid)
  {
    HVal dis = grid.meta().get("dis", false);
    if (dis instanceof HStr) return ((HStr)dis).val;
    return "server side error";
  }

  /** Error grid returned by server */
  public final HGrid grid;

  /** Get the server side stack trace or return null if not available */
  public String trace()
  {
    HVal val = grid.meta().get("errTrace", false);
    if (val instanceof HStr) return ((HStr)val).toString();
    return null;
  }

}