//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   05 Feb 2013  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations,
//                               added use of generics
//

package nhaystack;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BObject;
import javax.baja.sys.BSimple;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import org.projecthaystack.HTimeZone;

/**
 *  BHTimeZone wraps a Haystack Timezone.
 */
@NiagaraType
public final class BHTimeZone
    extends BSimple
{
    /** * The default is HTimeZone.DEFAULT. */
    public static final BHTimeZone DEFAULT = new BHTimeZone(HTimeZone.DEFAULT);

/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.BHTimeZone(2979906276)1.0$ @*/
/* Generated Wed Nov 29 14:36:23 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHTimeZone.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public static BHTimeZone make(HTimeZone timeZone) 
    { 
        return new BHTimeZone(timeZone);
    }

    private BHTimeZone(HTimeZone timeZone) 
    { 
        this.timeZone = timeZone;
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    public int hashCode() 
    { 
        return timeZone.hashCode(); 
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        if (!(obj instanceof BHTimeZone)) return false;
        BHTimeZone that = (BHTimeZone) obj;
        return timeZone.equals(that.timeZone);
    }

////////////////////////////////////////////////////////////////
// BSimple
////////////////////////////////////////////////////////////////

    @Override
    public void encode(DataOutput encoder) throws IOException
    { 
        encoder.writeUTF(timeZone.name);
    }

    @Override
    public BObject decode(DataInput decoder) throws IOException
    { 
        return new BHTimeZone(HTimeZone.make(decoder.readUTF()));
    }  

    @Override
    public String encodeToString() throws IOException
    { 
        return timeZone.name;
    }

    @Override
    public BObject decodeFromString(String s) throws IOException
    { 
        return new BHTimeZone(HTimeZone.make(s));
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    /**
      * Return the underlying timeZone.
      */
    public HTimeZone getTimeZone() { return timeZone; }

////////////////////////////////////////////////////////////////
// Attributes
//////////////////////////////////////////////////////////////// 

    public static final Set<String> TZ_REGIONS;
    static
    {
        TZ_REGIONS = new HashSet<>(Arrays.asList(
          "Africa",
          "America",
          "Antarctica",
          "Asia",
          "Atlantic",
          "Australia",
          "Etc",
          "Europe",
          "Indian",
          "Pacific"));
    }

    private final HTimeZone timeZone;
}
