//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//
package nhaystack.server;

import java.io.*;
import java.util.logging.*;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.baja.sys.*;
import javax.baja.web.*;

import org.projecthaystack.server.*;

/**
  * BNHaystackServlet relays GET and POST requests 
  * to the NHServer that is made available 
  * by the BNHaystackService.
  */
public class BNHaystackServlet extends BWebServlet
{
    /*-
    class BNHaystackServlet
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $haystack.ax.BNHaystackServlet(4016421877)1.0$ @*/
/* Generated Thu Sep 27 14:59:43 EDT 2012 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackServlet.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackServlet()
    {
        setServletName("haystack");
    }

    public void serviceStarted() throws Exception
    {
        // Disable the servlet when its started.  It will be re-enabled 
        // when BNHaystackService.initializeHaystack() is called
        enableWithMessage(false);
    }

    void enableWithMessage(boolean flag)
    {
        LOG.info("NHaystack Servlet " + (flag ? "enabled" : "disabled"));
        setEnabled(flag);
    }

////////////////////////////////////////////////////////////////
// BWebServlet
////////////////////////////////////////////////////////////////

    public void doGet(WebOp op) throws IOException, ServletException
    {   
//System.out.println("BNHaystackServlet.doGet: " + op);
        // save the op so we can use it later to check permissions
        ThreadContext.putContext(Thread.currentThread(), op);

        try
        {
            servlet.doGet(
//                new RequestWrapper(op.getRequest()),
                op.getRequest(),
                op.getResponse());
        }
        finally
        {
            // remove the op
            ThreadContext.removeContext(Thread.currentThread());
        }
    }

    public void doPost(WebOp op) throws IOException, ServletException
    {
//System.out.println("BNHaystackServlet.doPost: " + op);
        // save the op so we can use it later to check permissions
        ThreadContext.putContext(Thread.currentThread(), op);

        try
        {
            servlet.doPost(
//                new RequestWrapper(op.getRequest()),
                op.getRequest(),
                op.getResponse());
        }
        finally
        {
            // remove the op
            ThreadContext.removeContext(Thread.currentThread());
        }
    }

////////////////////////////////////////////////////////////////
// RequestWrapper
////////////////////////////////////////////////////////////////

    /**
      * work around for problems with the Niagara Servlet API Implementation
      */
    private class RequestWrapper extends HttpServletRequestWrapper     
    {
        RequestWrapper(HttpServletRequest req)
        {
            super(req);

            // lop off the servlet name from the front of the request path
            String path = req.getPathInfo();
            this.pathInfo = path.substring(
                getServletName().length() + 1);
        }

        public String getPathInfo() { return pathInfo; }

        public String getServletPath()
        {
            return "/" + getServletName();
        }

        private final String pathInfo;
    }

////////////////////////////////////////////////////////////////
// NServlet
////////////////////////////////////////////////////////////////

    private class NServlet extends HServlet
    {
        public HServer db()
        {        
            if (db == null)
            {
                BNHaystackService service = (BNHaystackService) 
                    BNHaystackServlet.this.getParent();
                db = service.getHaystackServer();
            }
            return db;
        }
        private HServer db;
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger("nhaystack");

    public BIcon getIcon() { return ICON; }
    private static final BIcon ICON = BIcon.make("module://nhaystack/nhaystack/icons/tag.png");

    private final HServlet servlet = new NServlet();
}
