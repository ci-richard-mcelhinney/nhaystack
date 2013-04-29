//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Apr 2013  Mike Jarmy  Creation
//
package nhaystack.ui.view;

import java.util.*;

import javax.baja.gx.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.enums.*;
import javax.baja.ui.pane.*;
import javax.baja.ui.tree.*;
import javax.baja.util.*;
import javax.baja.workbench.view.*;
import javax.baja.xml.*;

import nhaystack.server.*;
import nhaystack.site.*;

/**
  * BNHaystackServiceView is a view on BNHaystackService
  */
public class BNHaystackServiceView extends BWbComponentView
{
    /*-
    class BNHaystackServiceView
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BNHaystackServiceView(1870466022)1.0$ @*/
/* Generated Tue Apr 09 09:47:22 EDT 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackServiceView.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackServiceView()
    {
        addSlot = new BAddHaystackSlot(this);
        addSlot.setEditable(false);
        addSlot.setText(LEX.getText("dragHere"));

        BBorderPane b1 = new BBorderPane(addSlot);
        b1.setBorder(BBorder.make("inset"));
        b1.setPadding(BInsets.make(2, 2, 2, 2));
        b1.setMargin(BInsets.make(4, 4, 8, 4));

        ///////////////////////////////// 

        tree = new BTree();

        BBorderPane b2 = new BBorderPane(tree);
        b2.setPadding(BInsets.make(4));

        BScrollPane sp = new BScrollPane();
        sp.setContent(b2);

        BEdgePane e1 = new BEdgePane();
        BLabel label = new BLabel(LEX.getText("siteEquipPoint"), BOLD);
        label.setHalign(BHalign.left);
        e1.setTop(label);
        e1.setCenter(sp);

        ///////////////////////////////// 

        BBorderPane b3 = new BBorderPane(e1);
        b3.setPadding(BInsets.make(4));

        BEdgePane e2 = new BEdgePane();
        e2.setTop(b1);
        e2.setCenter(b3);

        setContent(e2);
    }

    public void stopped() throws Exception 
    {
        super.stopped();
        unregisterForAllComponentEvents();
    }

    protected void doLoadValue(BObject value, Context cx)
    {
        BNHaystackService service = (BNHaystackService) value;

        String xml = ((BString) service.invoke(
            BNHaystackService.fetchSepNav, null)).getString();
        try
        {
            XElem root = XParser.make(xml).parse();

            XElem[] xsites = root.elems("site");
            if (xsites.length == 0)
            {
                tree.setModel(new EmptyTreeModel());
            }
            else
            {
                NavTreeModel model = new NavTreeModel();
                model.sites = new SiteNode[xsites.length];
                for (int i = 0; i < model.sites.length; i++)
                    model.sites[i] = new SiteNode(model, xsites[i]);
                tree.setModel(model);
            }
        }
        catch (Exception e)
        {
            throw new BajaRuntimeException(e);
        }
    }

    class NavTreeModel extends TreeModel
    {
        public int getRootCount()
        {
            return sites.length;
        }
        public TreeNode getRoot(int index)
        {
            return sites[index];
        }

        SiteNode[] sites;
    }

    class SiteNode extends TreeNode
    {
        SiteNode(NavTreeModel model, XElem xsite)
        {
            super(model);
            this.text = xsite.get("navName");

            XElem[] xequips = xsite.elems("equip");
            equips = new EquipNode[xequips.length];
            for (int i = 0; i < equips.length; i++)
                equips[i] = new EquipNode(model, xequips[i]);

            setExpanded(true);
        }

        public String getText() { return text; }
        public BImage getIcon() { return BImage.make(BHSite.ICON); }
        public int getChildCount() { return equips.length; }
        public TreeNode getChild(int index) { return equips[index]; }

        private final String text;
        private final EquipNode[] equips;
    };

    class EquipNode extends TreeNode
    {
        EquipNode(NavTreeModel model, XElem xequip)
        {
            super(model);
            this.text = xequip.get("navName");

            XElem[] xpoints = xequip.elems("point");
            points = new PointNode[xpoints.length];
            for (int i = 0; i < points.length; i++)
                points[i] = new PointNode(model, xpoints[i]);

            setExpanded(true);
        }

        public String getText() { return text; }
        public BImage getIcon() { return BImage.make(BHEquip.ICON); }
        public int getChildCount() { return points.length; }
        public TreeNode getChild(int index) { return points[index]; }

        private final String text;
        private final PointNode[] points;
    };

    class PointNode extends TreeNode
    {
        PointNode(NavTreeModel model, XElem xpoint)
        {
            super(model);
            this.text = xpoint.get("navName");

            String type = xpoint.get("axType");
            this.icon = (BImage) POINT_ICONS.get(type);
            if (this.icon == null)
            {
                BTypeSpec spec = BTypeSpec.make(type);
                BObject obj = spec.getInstance();
                this.icon = BImage.make(obj.getIcon());
                POINT_ICONS.put(type, this.icon);
            }
        }

        public String getText() { return text; }
        public BImage getIcon() { return icon; }
        public int getChildCount() { return 0; }
        public TreeNode getChild(int index) { throw new IllegalStateException(); }

        private final String text;
        private BImage icon;
    };

    class EmptyTreeModel extends TreeModel
    {
        public int getRootCount() { return 1; }

        public TreeNode getRoot(int index)
        {
            if (index != 0) throw new IllegalStateException();

            return new TreeNode(this)
            {
                public String getText() { return LEX.getText("noSites"); }
                public BImage getIcon() { return SITE; }
                public int getChildCount() { return 0; }
                public TreeNode getChild(int index) { throw new IllegalStateException(); }
            };
        }
    }

////////////////////////////////////////////////////////////////
// Attributes 
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");
    private static BImage SITE = BImage.make(new BHSite().getIcon());
    private static BFont BOLD = BFont.make("Tahoma", 11.0, BFont.BOLD);

    private static final Map POINT_ICONS = new HashMap();

    private final BTree tree;
    private BAddHaystackSlot addSlot;
}
