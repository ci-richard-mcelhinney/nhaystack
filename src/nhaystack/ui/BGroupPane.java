//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.ui;

import javax.baja.gx.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.pane.*;

public class BGroupPane extends BPane
{
    /*-
    class BGroupPane
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BGroupPane(1322706479)1.0$ @*/
/* Generated Sat Feb 09 10:45:55 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BGroupPane.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BGroupPane() {}

    public BGroupPane(String headers[], BPane panes[])
    {
        if (headers.length != panes.length)
            throw new IllegalStateException();

        this.headers = headers;
        this.panes = panes;

        for (int i = 0; i < panes.length; i++)
            add(null, panes[i]);
    }

    public void doLayout(BWidget abwidget[])
    {
        double width = getWidth();
        double y = -BORDER;
        for (int i = 0; i < panes.length; i++)
        {
            y += HEIGHT;
            BPane pane = panes[i];
            double ph = pane.getPreferredHeight();
            pane.setBounds(0, y, width, ph);
            y += ph;
        }
    }

    public void computePreferredSize()
    {
        double width = 0;
        double height = -BORDER;
        for (int i = 0; i < panes.length; i++)
        {
            BPane pane = panes[i];
            pane.computePreferredSize();
            width = Math.max(width, pane.getPreferredWidth());
            height += pane.getPreferredHeight();
            height += HEIGHT;
        }
        setPreferredSize(width, height);
    }

    public void paint(Graphics g)
    {
        super.paint(g);

        try
        {
            g.push();
            g.setFont(BOLD);

            double width = getWidth();

            for (int i = 0; i < panes.length; i++)
            {
                double y = panes[i].getY();

                g.setBrush(BG);
                g.fillRect(
                    0, y - HEIGHT + BORDER, 
                    width, HEIGHT - BORDER*2);

                int half = BORDER/2;
                g.setBrush(BColor.black);
                g.drawString(
                    headers[i],
                    half, y - BORDER - half);
            }
        }
        finally
        {
            g.pop();
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final int HEIGHT = 44;
    private static final int BORDER = 12;
    private static final BBrush BG = BBrush.makeSolid(BColor.make(0xb0b0b0));

    private static BFont BOLD = BFont.make("Tahoma", 11.0, BFont.BOLD);

    private String[] headers;
    private BPane[] panes;
}
