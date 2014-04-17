//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver.point.learn;

import javax.baja.sys.*;
import javax.baja.gx.*;
import javax.baja.ui.*;
import javax.baja.control.*;
import javax.baja.driver.point.*;
import javax.baja.driver.ui.point.*;
import javax.baja.workbench.mgr.*;
import javax.baja.job.*;

import nhaystack.driver.*;
import nhaystack.driver.point.*;

public class BNHaystackPointManager extends BPointManager
{                
    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BNHaystackPointManager.class);  

    protected MgrModel      makeModel()      { return new Model(this);      }
    protected MgrController makeController() { return new Controller(this); }
    protected MgrLearn      makeLearn()      { return new Learn(this);      }

////////////////////////////////////////////////////////////////
// Model
////////////////////////////////////////////////////////////////

    class Model extends PointModel
    {
        Model(BPointManager manager) { super(manager); }

        protected MgrColumn[] makeColumns()
        {        
            return cols;   
        }

        public BComponent newInstance(MgrTypeInfo type)
        throws Exception
        {                  
            return type.newInstance();
        }  

        public MgrTypeInfo[] getNewTypes()
        {
            return new MgrTypeInfo[]
            {
                TYPE_NUMBER, TYPE_NUMBER_WRITABLE,
                TYPE_BOOL,   TYPE_BOOL_WRITABLE,
            };
        }                                    
    }   

    private BNHaystackServer server()
    {    
        BIPointFolder folder = (BIPointFolder) getCurrentValue();
        return (BNHaystackServer) folder.getDevice();
    }   

////////////////////////////////////////////////////////////////
// Learn
////////////////////////////////////////////////////////////////

    class Learn extends MgrLearn
    {                           
        Learn(BNHaystackPointManager mgr) { super(mgr); }

        protected MgrColumn[] makeColumns()
        {                     
            return new MgrColumn[]
            {
                new MgrColumn.Name(),
                new MgrColumn.Prop(BNHaystackPointEntry.id),  
            };    
        } 

        public boolean isMatchable(Object dis, BComponent db)
        {
            return isExisting(dis, db);
        }            

        public BImage getIcon(Object dis)
        {
            BNHaystackPointEntry entry = (BNHaystackPointEntry) dis;

            if      (entry.getKind().equals("Bool"))   return BOOL_ICON;
            else if (entry.getKind().equals("Number")) return NUMBER_ICON;

            else throw new IllegalStateException();
        }

        public MgrTypeInfo[] toTypes(Object dis)
        {
            BNHaystackPointEntry entry = (BNHaystackPointEntry) dis;

            if (entry.getKind().equals("Bool"))
            {
                return (entry.getWritable()) ?
                    new MgrTypeInfo[] { TYPE_BOOL_WRITABLE } :
                    new MgrTypeInfo[] { TYPE_BOOL };
            }

            else if (entry.getKind().equals("Number"))
            {
                return (entry.getWritable()) ?
                    new MgrTypeInfo[] { TYPE_NUMBER_WRITABLE } :
                    new MgrTypeInfo[] { TYPE_NUMBER };
            }

            else throw new IllegalStateException();
      }

        public void toRow(Object dis, MgrEditRow row) throws Exception
        {                                      
            BNHaystackPointEntry entry = (BNHaystackPointEntry) dis;

            row.setDefaultName(entry.getName());
            row.setCell(colId, entry.getId());
            row.setCell(colFacets, entry.getFacets());
            row.setCell(colImportedTags, entry.getImportedTags());
        }                   

        public boolean isExisting(Object discovery, BComponent component)
        {
            boolean res = false;

            if (discovery instanceof BNHaystackPointEntry)
            {
                BNHaystackPointEntry entry = (BNHaystackPointEntry) discovery;      
                res = entry.is(component);
            }

            return res;
        }    

        public void jobComplete(BJob job)
        {
            super.jobComplete(job);
            updateDiscoveryRows(job);
        }
    } 

    private void updateDiscoveryRows(BComponent event)
    {
        BNHaystackPointEntry[] rows = (BNHaystackPointEntry[])
            event.getChildren(BNHaystackPointEntry.class);

        for (int i = 0; i < rows.length; i++)
            rows[i].loadSlots();  

        getLearn().updateRoots(rows);
    }

////////////////////////////////////////////////////////////////
// Controller
////////////////////////////////////////////////////////////////

    class Controller extends PointController
    {             
        Controller(BPointManager mgr) { super(mgr); } 

        public CommandArtifact doDiscover(Context context) throws Exception
        {                      
            super.doDiscover(context);          
            getLearn().setJob(server().submitLearnPointsJob()); 
            return null;
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    static MgrTypeInfo TYPE_NUMBER          = MgrTypeInfo.make(BNHaystackNumberPoint.TYPE);
    static MgrTypeInfo TYPE_NUMBER_WRITABLE = MgrTypeInfo.make(BNHaystackNumberWritable.TYPE);
    static MgrTypeInfo TYPE_BOOL            = MgrTypeInfo.make(BNHaystackBoolPoint.TYPE);
    static MgrTypeInfo TYPE_BOOL_WRITABLE   = MgrTypeInfo.make(BNHaystackBoolWritable.TYPE);

    MgrColumn colPath        = new MgrColumn.Path(MgrColumn.UNSEEN);
    MgrColumn colName        = new MgrColumn.Name();
    MgrColumn colType        = new MgrColumn.Type();
    MgrColumn colToString    = new MgrColumn.ToString("Out", 0);
    MgrColumn colEnabled     = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.enabled}, MgrColumn.EDITABLE | MgrColumn.UNSEEN);
    MgrColumn colFacets      = new MgrColumn.PropPath(new Property[] {BControlPoint.facets},  MgrColumn.EDITABLE | MgrColumn.UNSEEN);
    MgrColumn colTuning      = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.tuningPolicyName}, MgrColumn.EDITABLE);
    MgrColumn colDeviceFacets= new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.deviceFacets}, MgrColumn.EDITABLE | MgrColumn.UNSEEN);
    MgrColumn colConversion  = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.conversion},   MgrColumn.EDITABLE | MgrColumn.UNSEEN);
    MgrColumn colFaultCause  = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.faultCause},   MgrColumn.UNSEEN);
    MgrColumn colReadValue   = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.readValue},    MgrColumn.UNSEEN);
    MgrColumn colWriteValue  = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.writeValue},   MgrColumn.UNSEEN);

    MgrColumn colId           = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BNHaystackProxyExt.id}, MgrColumn.EDITABLE);
    MgrColumn colImportedTags = new MgrColumn.PropPath(
        new Property[] {BControlPoint.proxyExt, BNHaystackProxyExt.importedTags}, MgrColumn.EDITABLE | MgrColumn.READONLY);

    MgrColumn[] cols = 
    { 
        colPath, colName, colType, colToString, colId, colImportedTags,
        colEnabled, colFacets, colTuning, colDeviceFacets, colConversion,
        colFaultCause, colReadValue, colWriteValue,
    }; 

    private static final BImage NUMBER_ICON = BImage.make(BNumericPoint .TYPE.getInstance().getIcon());
    private static final BImage BOOL_ICON   = BImage.make(BBooleanPoint .TYPE.getInstance().getIcon());
}
