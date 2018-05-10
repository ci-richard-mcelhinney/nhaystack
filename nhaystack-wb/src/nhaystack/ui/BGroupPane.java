//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Feb 2013  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations
//
package nhaystack.ui;

import javax.baja.gx.BBrush;
import javax.baja.gx.BColor;
import javax.baja.gx.BFont;
import javax.baja.gx.Graphics;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BWidget;
import javax.baja.ui.pane.BPane;

@NiagaraType
public class BGroupPane extends BPane
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BGroupPane(2979906276)1.0$ @*/
/* Generated Mon Nov 20 11:09:23 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BGroupPane.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BGroupPane() {}

    public BGroupPane(String[] headers, BPane[] panes)
    {
        if (headers.length != panes.length)
            throw new IllegalStateException();

        this.headers = headers;
        this.panes = panes;

        for (BPane pane : panes)
            add(null, pane);
    }

    @Override
    public void doLayout(BWidget[] abwidget)
    {
        double width = getWidth();
        double y = -BORDER;
        for (BPane pane : panes)
        {
            y += HEIGHT;
            double ph = pane.getPreferredHeight();
            pane.setBounds(0, y, width, ph);
            y += ph;
        }
    }

    @Override
    public void computePreferredSize()
    {
        double width = 0;
        double height = -BORDER;
        for (BPane pane : panes)
        {
            pane.computePreferredSize();
            width = Math.max(width, pane.getPreferredWidth());
            height += pane.getPreferredHeight();
            height += HEIGHT;
        }
        setPreferredSize(width, height);
    }

    @Override
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

    private static final BFont BOLD = BFont.make("Tahoma", 11.0, BFont.BOLD);

    private String[] headers;
    private BPane[] panes;
}
