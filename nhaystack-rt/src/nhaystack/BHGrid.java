//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   10 Feb 2013  Mike Jarmy  Creation

package nhaystack;

import java.io.*;
import javax.baja.sys.*;
import org.projecthaystack.*;
import org.projecthaystack.io.*;

/**
 *  BHGrid wraps a Haystack HGrid
 */
public final class BHGrid
    extends BSimple
{
    /**
      * Make a BHGrid instance from an HGrid.
      */
    public static BHGrid make(HGrid grid) 
    { 
        return new BHGrid(grid);  
    }

    /**
      * Make a BHGrid instance from a ZINC-encoded string.
      */
    public static BHGrid make(String s) 
    { 
        HZincReader zr = new HZincReader(s);
        return new BHGrid(zr.readGrid());
    }

    private BHGrid(HGrid grid) 
    { 
        this.grid = grid;
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    public int hashCode() 
    { 
        return grid.hashCode(); 
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        if (!(obj instanceof BHGrid)) return false;
        BHGrid that = (BHGrid) obj;
        return (grid.equals(that.grid));
    }

////////////////////////////////////////////////////////////////
// BSimple
////////////////////////////////////////////////////////////////

    /**
      * Encode to ZINC format
      */
    public void encode(DataOutput encoder) throws IOException
    { 
        encoder.writeUTF(HZincWriter.gridToString(grid));
    }

    /**
      * Decode from ZINC format
      */
    public BObject decode(DataInput decoder) throws IOException
    { 
        HZincReader zr = new HZincReader(decoder.readUTF());
        return new BHGrid(zr.readGrid());
    }  

    /**
      * Encode to ZINC format
      */
    public String encodeToString() throws IOException
    { 
        return HZincWriter.gridToString(grid);
    }

    /**
      * Decode from ZINC format
      */
    public BObject decodeFromString(String s) throws IOException
    { 
        HZincReader zr = new HZincReader(s);
        return new BHGrid(zr.readGrid());
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    /**
      * Return the underlying HGrid.
      */
    public HGrid getGrid() { return grid; }

////////////////////////////////////////////////////////////////
// Attributes
//////////////////////////////////////////////////////////////// 

    public BIcon getIcon() { return ICON; }
    private static final BIcon ICON = BIcon.make("module://nhaystack/nhaystack/icons/tag.png");

    /** * The default is HGrid.EMPTY. */
    public static final BHGrid DEFAULT = new BHGrid(HGrid.EMPTY);

    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BHGrid.class);

    private final HGrid grid;
}
