//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Apr 2013  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations
//
package nhaystack.ui.view;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.nre.util.TextUtil;
import javax.baja.space.Mark;
import javax.baja.sys.BComponent;
import javax.baja.sys.BObject;
import javax.baja.sys.BValue;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.CommandArtifact;
import javax.baja.ui.text.BTextEditor;
import javax.baja.ui.transfer.TransferConst;
import javax.baja.ui.transfer.TransferContext;
import javax.baja.ui.transfer.TransferFormat;
import nhaystack.BHDict;
import nhaystack.res.Resources;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;

/**
  * BAddHaystackSlot adds a "haystack" BHDict slot to BComponents that
  * are dropped on it. 
  */
@NiagaraType
public class BAddHaystackSlot extends BTextEditor
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.view.BAddHaystackSlot(2979906276)1.0$ @*/
/* Generated Mon Nov 20 10:40:07 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BAddHaystackSlot.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BAddHaystackSlot() { }

    public BAddHaystackSlot(BNHaystackServiceView view)
    {
        this.view = view;
    }

    @Override
    public int dragOver(TransferContext ctx)
    {
        if (!view.service.getEnabled()) return 0;

        Mark mark = (Mark) ctx.getEnvelope().getData(TransferFormat.mark);
        for (int i = 0; i < mark.size(); i++)
        {
            BObject obj = mark.getValue(i);
            if (obj instanceof BComponent)
                return TransferConst.ACTION_COPY;
        }
        return 0;
    }

    @Override
    public CommandArtifact drop(TransferContext ctx)
        throws Exception
    {
        if (!view.service.getEnabled()) return null;

        Mark mark = (Mark) ctx.getEnvelope().getData(TransferFormat.mark);
        for (int i = 0; i < mark.size(); i++)
        {
            BObject obj = mark.getValue(i);
            if (obj instanceof BComponent)
            {
                BComponent comp = (BComponent) obj;
                view.registerForComponentEvents(comp, 1);

                BValue haystack = comp.get("haystack");
                if (haystack == null)
                    comp.add("haystack", BHDict.make(retrieveTagsDict(comp.getName())));
            }
        }

        return null;
    }

    /** 
     * retrieveTagsDict() is used to generate tags as a HDict based on the name of the BComponent.
     * CSV List is included in Resources.jar 
     * lookout function (getAutoMarkers(string)) can also be found in Resources.jar
     * return : dict
     **/
    private static HDict retrieveTagsDict(String pointName){
      // get list of tags from csv file
      String[] autoMarkers = Resources.getAutoMarkers(pointName);

      // Make default dictBuilder (empty tag list)... will be returned if pointName not found in csv list
      HDictBuilder dictBuilder = new HDictBuilder();

      // If tags are found, add them to dictBuilder
      if (autoMarkers.length > 0)
      {
          for (String autoMarker : autoMarkers)
          {
              String[] newTag = TextUtil.split(autoMarker, ':');
              // Try / catch to catch bad tag names
              try
              {
                  if (newTag.length < 2)
                  {
                      dictBuilder.add(newTag[0]);
                  }
                  else
                  {
                      // Tag of type string:value like stage:1
                      // Detect if value is float...if so, pass as double. If not, pass as string
                      double val;
                      if (isFloat(newTag[1]))
                      {
                          val = Float.valueOf(newTag[1]).floatValue();
                          dictBuilder.add(newTag[0], val);
                      }
                      else
                      {
                          dictBuilder.add(newTag[0], newTag[1]);
                      }
                  }
              }
              catch (Exception e)
              {
                  throw new BajaRuntimeException("Bad tag name : " + autoMarker);
              }
          }

          // Add axAnnotated tags
          dictBuilder.add("axAnnotated");
      }

      // Return a HDict format to be used with BHDict.make(HDict)
      return dictBuilder.toDict();
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private BNHaystackServiceView view;
    
    // Helper to detect if arguments after : are numeric value or string
    // ex. stage:1 vs marker:string
    private static boolean isFloat(String s)
    {
        try
        {
            Double.parseDouble(s);
            return true;
        }
        catch(NumberFormatException e)
        {
            // not a double
            return false;
        }
    }
}
