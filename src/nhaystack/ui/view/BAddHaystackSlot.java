//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Apr 2013  Mike Jarmy  Creation
//
package nhaystack.ui.view;

import javax.baja.space.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.text.*;
import javax.baja.ui.transfer.*;
import nhaystack.*;

import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;

import nhaystack.res.Resources;
import javax.baja.nre.util.TextUtil;

/**
  * BAddHaystackSlot adds a "haystack" BHDict slot to BComponents that
  * are dropped on it. 
  */
public class BAddHaystackSlot extends BTextEditor
{
    /*-
    class BAddHaystackSlot
    {
    }
  -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.view.BAddHaystackSlot(658320125)1.0$ @*/
/* Generated Tue May 30 17:08:43 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BAddHaystackSlot.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BAddHaystackSlot() { }

    public BAddHaystackSlot(BNHaystackServiceView view)
    {
        this.view = view;
    }

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
                    //comp.add("haystack", BHDict.DEFAULT);
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
    private HDict retrieveTagsDict(String pointName){
      // get list of tags from csv file
      String[] autoTagList = Resources.getAutoMarkers(pointName);
      // Make default dictBuilder (empty tag list)... will be returned if pointName not found in csv list
      HDictBuilder dictBuilder = new HDictBuilder();
      // If tags are found, add them to dictBuilder
      String[] newtag = null;
      if ( autoTagList.length > 0)
      {
        for (int y = 0; y < autoTagList.length; y++)
        {
            
          newtag = TextUtil.split(autoTagList[y],':');
          // Try / catch to catch bad tag names 
          if (newtag.length < 2)
          {
              try {
                dictBuilder.add(newtag[0]);
               }
               catch (Exception e)
               {
                 throw new BajaRuntimeException("Bad tag name : " + autoTagList[y]);
               }    
           }
           else
              {
               // Tag of type string:value like stage:1
               // Detect if value is float...if so, pass as double. If not, pass as string
               try {  
                 double val = 0;
                 if (isFloat(newtag[1])){
                   val = Float.valueOf(newtag[1]).floatValue();
                   dictBuilder.add(newtag[0],val);
                 }
                 else {
                   dictBuilder.add(newtag[0],newtag[1]);
                 }                    
               }
               catch (Exception e)
               {
                 throw new BajaRuntimeException("Bad tag name : " + autoTagList[y]);
               }    
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
    
    //Helper to detect if arguments after : are numeric value or string
    // ex. stage:1 vs marker:string
    private boolean isFloat(String s){
      try
      {
        Double.parseDouble(s);
        return true;
      }
      catch(NumberFormatException e)
      {
        //not a double
        return false;
      }
    }
}
