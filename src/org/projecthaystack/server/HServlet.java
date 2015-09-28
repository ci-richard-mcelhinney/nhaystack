//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   03 Nov 2011  Brian Frank  Creation
//
package org.projecthaystack.server;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.projecthaystack.*;

/**
 * HServlet implements the haystack HTTP REST API for
 * querying entities and history data.
 *
 * @see <a href='http://project-haystack.org/doc/Rest'>Project Haystack</a>
 */
public class HServlet extends HttpServlet
{

//////////////////////////////////////////////////////////////////////////
// Database Hook
//////////////////////////////////////////////////////////////////////////

  /**
   * Get the database to use for this servlet.
   * If not overridden then a test database is created.
   */
  public HServer db()
  {
    return new org.projecthaystack.test.TestDatabase();
  }

//////////////////////////////////////////////////////////////////////////
// HttpServlet Hooks
//////////////////////////////////////////////////////////////////////////

  public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException
  {
    onService("GET", req, res);
  }

  public void doPost(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException
  {
    onService("POST", req, res);
  }

//////////////////////////////////////////////////////////////////////////
// Service
//////////////////////////////////////////////////////////////////////////

  private void onService(String method, HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException
  {
    // get the database
    HServer db = db();

    // if root, then redirect to {haystack}/about
    String path = req.getPathInfo();
    if (path == null || path.length() == 0 || path.equals("/"))
    {
      res.sendRedirect(req.getServletPath() + "/about");
      return;
    }

    // parse URI path into "/{opName}/...."
    int slash = path.indexOf('/', 1);
    if (slash < 0) slash = path.length();
    String opName = path.substring(1, slash);

    // resolve the op
    HOp op = db.op(opName, false);
    if (op == null)
    {
      res.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // route to the op
    try
    {
      op.onService(db, req, res);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new ServletException(e);
    }
  }

//////////////////////////////////////////////////////////////////////////
// Debug
//////////////////////////////////////////////////////////////////////////

  void dumpReq(HttpServletRequest req) { dumpReq(req, null); }
  void dumpReq(HttpServletRequest req, PrintWriter out)
  {
    try
    {
      if (out == null) out = new PrintWriter(System.out);
      out.println("==========================================");
      out.println("method      = " + req.getMethod());
      out.println("pathInfo    = " + req.getPathInfo());
      out.println("contextPath = " + req.getContextPath());
      out.println("servletPath = " + req.getServletPath());
      out.println("query       = " + (req.getQueryString() == null ? "null" : URLDecoder.decode(req.getQueryString(), "UTF-8")));
      out.println("headers:");
      Enumeration e = req.getHeaderNames();
      while (e.hasMoreElements())
      {
        String key = (String)e.nextElement();
        String val = req.getHeader(key);
        out.println("  " + key + " = " + val);
      }
      out.flush();
    }
    catch (Exception e) { e.printStackTrace(); }
  }

}