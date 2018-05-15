//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.ui.point;

import javax.baja.control.BBooleanPoint;
import javax.baja.control.BControlPoint;
import javax.baja.control.BNumericPoint;
import javax.baja.control.BStringPoint;
import javax.baja.driver.point.BIPointFolder;
import javax.baja.driver.point.BProxyExt;
import javax.baja.driver.ui.point.BPointManager;
import javax.baja.driver.ui.point.PointController;
import javax.baja.driver.ui.point.PointModel;
import javax.baja.gx.BImage;
import javax.baja.job.BJob;
import javax.baja.nre.annotations.AgentOn;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.Context;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.CommandArtifact;
import javax.baja.workbench.mgr.MgrColumn;
import javax.baja.workbench.mgr.MgrController;
import javax.baja.workbench.mgr.MgrEditRow;
import javax.baja.workbench.mgr.MgrLearn;
import javax.baja.workbench.mgr.MgrModel;
import javax.baja.workbench.mgr.MgrTypeInfo;
import nhaystack.driver.BNHaystackServer;
import nhaystack.driver.point.BNHaystackBoolPoint;
import nhaystack.driver.point.BNHaystackBoolWritable;
import nhaystack.driver.point.BNHaystackNumberPoint;
import nhaystack.driver.point.BNHaystackNumberWritable;
import nhaystack.driver.point.BNHaystackProxyExt;
import nhaystack.driver.point.BNHaystackStrPoint;
import nhaystack.driver.point.BNHaystackStrWritable;
import nhaystack.driver.point.learn.BNHaystackPointEntry;

/**
  * BNHaystackPointManager manages the proxy points
  * under a BNHaystackServer.
  */
@NiagaraType(
  agent = @AgentOn(
    types = {
      "nhaystack:NHaystackPointDeviceExt",
      "nhaystack:NHaystackPointFolder"
    }
  )
)
public class BNHaystackPointManager extends BPointManager
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.point.BNHaystackPointManager(3066859350)1.0$ @*/
/* Generated Mon Nov 20 10:37:33 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackPointManager.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/                

    @Override
    protected MgrModel      makeModel()      { return new Model(this);      }
    @Override
    protected MgrController makeController() { return new Controller(this); }
    @Override
    protected MgrLearn      makeLearn()      { return new Learn(this);      }

////////////////////////////////////////////////////////////////
// Model
////////////////////////////////////////////////////////////////

    class Model extends PointModel
    {
        Model(BPointManager manager) { super(manager); }

        @Override
        protected MgrColumn[] makeColumns()
        {        
            return cols;   
        }

        @Override
        public BComponent newInstance(MgrTypeInfo type) throws Exception
        {                  
            return type.newInstance();
        }

        @Override
        public MgrTypeInfo[] getNewTypes()
        {
            return new MgrTypeInfo[]
            {
                TYPE_NUMBER, TYPE_NUMBER_WRITABLE,
                TYPE_BOOL,   TYPE_BOOL_WRITABLE,
                TYPE_STR,    TYPE_STR_WRITABLE,
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
        Learn(BNHaystackPointManager mgr)
        {
            super(mgr);
        }

        @Override
        protected MgrColumn[] makeColumns()
        {
            return new MgrColumn[]
            {
                new MgrColumn.Name(),
                new MgrColumn.Prop(BNHaystackPointEntry.id),
            };
        } 

        @Override
        public boolean isMatchable(Object dis, BComponent db)
        {
            return isExisting(dis, db);
        }

        @Override
        public BImage getIcon(Object dis)
        {
            BNHaystackPointEntry entry = (BNHaystackPointEntry) dis;

            switch (entry.getKind())
            {
            case "Bool":
                return BOOL_ICON;
            case "Number":
                return NUMBER_ICON;
            case "Str":
                return STR_ICON;
            default:
                throw new IllegalStateException();
            }
        }

        @Override
        public MgrTypeInfo[] toTypes(Object dis)
        {
            BNHaystackPointEntry entry = (BNHaystackPointEntry) dis;

            switch (entry.getKind())
            {
            case "Bool":
                return entry.getWritable() ?
                    new MgrTypeInfo[] { TYPE_BOOL_WRITABLE } :
                    new MgrTypeInfo[] { TYPE_BOOL };
            case "Number":
                return entry.getWritable() ?
                    new MgrTypeInfo[] { TYPE_NUMBER_WRITABLE } :
                    new MgrTypeInfo[] { TYPE_NUMBER };
            case "Str":
                return entry.getWritable() ?
                    new MgrTypeInfo[] { TYPE_STR_WRITABLE } :
                    new MgrTypeInfo[] { TYPE_STR };
            default:
                throw new IllegalStateException();
            }
      }

        @Override
        public void toRow(Object dis, MgrEditRow row) throws Exception
        {
            BNHaystackPointEntry entry = (BNHaystackPointEntry) dis;

            row.setDefaultName(entry.getName());
            row.setCell(colId, entry.getId());
            row.setCell(colFacets, entry.getFacets());
            row.setCell(colImportedTags, entry.getImportedTags());
        }

        @Override
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

        @Override
        public void jobComplete(BJob job)
        {
            super.jobComplete(job);
            updateDiscoveryRows(job);
        }
    } 

    private void updateDiscoveryRows(BComponent event)
    {
        BNHaystackPointEntry[] rows = event.getChildren(BNHaystackPointEntry.class);

        for (BNHaystackPointEntry row : rows)
            row.loadSlots();

        getLearn().updateRoots(rows);
    }

////////////////////////////////////////////////////////////////
// Controller
////////////////////////////////////////////////////////////////

    class Controller extends PointController
    {             
        Controller(BPointManager mgr) { super(mgr); } 

        @Override
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

    static final MgrTypeInfo TYPE_NUMBER          = MgrTypeInfo.make(BNHaystackNumberPoint.TYPE);
    static final MgrTypeInfo TYPE_NUMBER_WRITABLE = MgrTypeInfo.make(BNHaystackNumberWritable.TYPE);
    static final MgrTypeInfo TYPE_BOOL            = MgrTypeInfo.make(BNHaystackBoolPoint.TYPE);
    static final MgrTypeInfo TYPE_BOOL_WRITABLE   = MgrTypeInfo.make(BNHaystackBoolWritable.TYPE);
    static final MgrTypeInfo TYPE_STR             = MgrTypeInfo.make(BNHaystackStrPoint.TYPE);
    static final MgrTypeInfo TYPE_STR_WRITABLE    = MgrTypeInfo.make(BNHaystackStrWritable.TYPE);

    final MgrColumn colPath         = new MgrColumn.Path(MgrColumn.UNSEEN);
    final MgrColumn colName         = new MgrColumn.Name();
    final MgrColumn colType         = new MgrColumn.Type();
    final MgrColumn colToString     = new MgrColumn.ToString("Out", 0);
    final MgrColumn colEnabled      = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.enabled},               MgrColumn.EDITABLE | MgrColumn.UNSEEN);
    final MgrColumn colFacets       = new MgrColumn.PropPath(new Property[] {BControlPoint.facets},                                    MgrColumn.EDITABLE);
    final MgrColumn colTuning       = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.tuningPolicyName},      MgrColumn.EDITABLE);
    final MgrColumn colDeviceFacets = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.deviceFacets},          MgrColumn.EDITABLE | MgrColumn.UNSEEN);
    final MgrColumn colConversion   = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.conversion},            MgrColumn.EDITABLE | MgrColumn.UNSEEN);
    final MgrColumn colFaultCause   = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.faultCause},            MgrColumn.EDITABLE | MgrColumn.READONLY);
    final MgrColumn colReadValue    = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.readValue},             MgrColumn.UNSEEN);
    final MgrColumn colWriteValue   = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BProxyExt.writeValue},            MgrColumn.UNSEEN);

    final MgrColumn colId           = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BNHaystackProxyExt.id},           MgrColumn.EDITABLE);
    final MgrColumn colImportedTags = new MgrColumn.PropPath(new Property[] {BControlPoint.proxyExt, BNHaystackProxyExt.importedTags}, MgrColumn.EDITABLE | MgrColumn.UNSEEN);

    final MgrColumn[] cols =
    { 
        colPath, colName, colType, colToString, colId, 
        colEnabled, colFacets, colTuning, colDeviceFacets, colConversion,
        colFaultCause, colReadValue, colWriteValue,
        colImportedTags,
    }; 

    private static final BImage NUMBER_ICON = BImage.make(BNumericPoint.TYPE.getInstance().getIcon());
    private static final BImage BOOL_ICON   = BImage.make(BBooleanPoint.TYPE.getInstance().getIcon());
    private static final BImage STR_ICON    = BImage.make(BStringPoint .TYPE.getInstance().getIcon());
}
