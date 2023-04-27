//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   10 Feb 2013  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BIcon;
import javax.baja.sys.BObject;
import javax.baja.sys.BSimple;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import org.projecthaystack.HGrid;
import org.projecthaystack.io.HZincReader;
import org.projecthaystack.io.HZincWriter;

/**
 *  BHGrid wraps a Haystack HGrid
 */
@NiagaraType
public final class BHGrid
    extends BSimple
{
    /** * The default is HGrid.EMPTY. */
    public static final BHGrid DEFAULT = new BHGrid(HGrid.EMPTY);

/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.BHGrid(2979906276)1.0$ @*/
/* Generated Wed Nov 29 14:36:23 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHGrid.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
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

    @Override
    public int hashCode() 
    { 
        return grid.hashCode(); 
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        if (!(obj instanceof BHGrid)) return false;
        BHGrid that = (BHGrid) obj;
        return grid.equals(that.grid);
    }

////////////////////////////////////////////////////////////////
// BSimple
////////////////////////////////////////////////////////////////

    /**
      * Encode to ZINC format
      */
    @Override
    public void encode(DataOutput encoder) throws IOException
    { 
        encoder.writeUTF(HZincWriter.gridToString(grid));
    }

    /**
      * Decode from ZINC format
      */
    @Override
    public BObject decode(DataInput decoder) throws IOException
    { 
        HZincReader zr = new HZincReader(decoder.readUTF());
        return new BHGrid(zr.readGrid());
    }  

    /**
      * Encode to ZINC format
      */
    @Override
    public String encodeToString() throws IOException
    { 
        return HZincWriter.gridToString(grid);
    }

    /**
      * Decode from ZINC format
      */
    @Override
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

    @Override
    public BIcon getIcon() { return ICON; }
    private static final BIcon ICON = BIcon.make("module://nhaystack/nhaystack/icons/tag.png");

    private final HGrid grid;
}
