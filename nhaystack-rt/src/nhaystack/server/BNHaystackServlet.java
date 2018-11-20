//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//   09 May 2018  Eric Anderson       Migrated to slot annotations, added missing @Overrides annotations
//
package nhaystack.server;

import java.io.IOException;
import java.util.logging.Logger;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BIcon;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.web.BWebServlet;
import javax.baja.web.WebOp;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.projecthaystack.server.HServer;
import org.projecthaystack.server.HServlet;

/**
  * BNHaystackServlet relays GET and POST requests 
  * to the NHServer that is made available 
  * by the BNHaystackService.
  */
@NiagaraType
public class BNHaystackServlet extends BWebServlet
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackServlet(2979906276)1.0$ @*/
/* Generated Sat Nov 18 18:37:27 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackServlet.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackServlet()
    {
        setServletName("haystack");
    }

    @Override
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

    @Override
    public void doGet(WebOp op) throws IOException, ServletException
    {   
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

    @Override
    public void doPost(WebOp op) throws IOException, ServletException
    {
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

        @Override
        public String getPathInfo() { return pathInfo; }

        @Override
        public String getServletPath()
        {
            return '/' + getServletName();
        }

        private final String pathInfo;
    }

////////////////////////////////////////////////////////////////
// NServlet
////////////////////////////////////////////////////////////////

    private class NServlet extends HServlet
    {
      private static final long serialVersionUID = -6738104616680194375L;

      @Override
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

    @Override
    public BIcon getIcon() { return ICON; }
    private static final BIcon ICON = BIcon.make("module://nhaystack/nhaystack/icons/tag.png");

    private final HServlet servlet = new NServlet();
}
