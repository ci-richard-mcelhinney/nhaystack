package nhaystack.driver.history;

import javax.baja.driver.point.*;
import javax.baja.driver.history.*;
import javax.baja.driver.ui.*;
import javax.baja.driver.ui.history.*; 
import javax.baja.driver.util.*;
import javax.baja.gx.*;
import javax.baja.ui.*;
import javax.baja.job.*;
import javax.baja.sys.*;
import javax.baja.util.*;
import javax.baja.workbench.mgr.*; 
import javax.baja.workbench.mgr.folder.*;
import nhaystack.driver.*;
import nhaystack.driver.history.*;

public final class BNHaystackHistoryImportManager extends BHistoryImportManager
{
    /*-
      class BNHaystackHistoryImportManager
      {

      }
      -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.BNHaystackHistoryImportManager(973330622)1.0$ @*/
/* Generated Fri Apr 04 07:54:01 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackHistoryImportManager.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

////////////////////////////////////////////////////////////////
// History import manager
////////////////////////////////////////////////////////////////

    protected ImportModel   makeImportModel() { return new Model(this);      }
    public    MgrController makeController()  { return new Controller(this); }
    protected MgrLearn      makeLearn()       { return new Learn(this);      }

////////////////////////////////////////////////////////////////
// History import model
////////////////////////////////////////////////////////////////

    private class Model extends ImportModel
    {
        public Model(BNHaystackHistoryImportManager importManager)
        {
            super(importManager);
        }

        /**
         * Return the import columns
         */
        protected MgrColumn[] makeColumns()
        {
            return cols;
        }

        /**
         * Get the list of types supported by the new operation.  The
         * first entry in the list should be the default type.
         */  
        public MgrTypeInfo[] getNewTypes()
        {
            return MgrTypeInfo.makeArray(BNHaystackHistoryImport.TYPE);
        } 
    }

////////////////////////////////////////////////////////////////
// Learn
////////////////////////////////////////////////////////////////

    class Learn extends MgrLearn
    {                           
        Learn(BNHaystackHistoryImportManager mgr) { super(mgr); }

        protected MgrColumn[] makeColumns()
        {                     
            return new MgrColumn[]
            {
                new MgrColumn.Name(),
                new MgrColumn.Prop(BNHaystackHistoryEntry.id),  
            };    
        } 

        public boolean isMatchable(Object dis, BComponent db)
        {
            return isExisting(dis, db);
        }            

        public BImage getIcon(Object dis)
        {
            return img; 
        }

        public MgrTypeInfo[] toTypes(Object dis)
        {
            return getModel().getNewTypes(); 
        }

        public void toRow(Object dis, MgrEditRow row) throws Exception
        {                                      
            // When the device is moved to the other table we now copy the information across to the other object
            BNHaystackHistoryEntry entry = (BNHaystackHistoryEntry)dis;

            // Set the default column data        
            row.setDefaultName(entry.getName());
            row.setCell(colId, entry.getId());
            row.setCell(colKind, BString.make(entry.getKind()));
            row.setCell(colTz, BString.make(entry.getTz()));

            row.setCell(colHistoryId, entry.getHistoryId());
        }                   

        public boolean isExisting(Object discovery, BComponent component)
        {
            boolean res = false;

            if (discovery instanceof BNHaystackHistoryEntry)
            {
                BNHaystackHistoryEntry entry = (BNHaystackHistoryEntry) discovery;      
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

////////////////////////////////////////////////////////////////
// Discovery methods
////////////////////////////////////////////////////////////////

    /**
     * Called once the discovery job has been executed
     */ 
    private void updateDiscoveryRows(BComponent event)
    {
        BNHaystackHistoryEntry[] rows = (BNHaystackHistoryEntry[])event.getChildren(BNHaystackHistoryEntry.class); 

        for (int i = 0; i < rows.length; i++)
            rows[i].loadSlots();  

        getLearn().updateRoots(rows);
    }

////////////////////////////////////////////////////////////////
// Controller
////////////////////////////////////////////////////////////////

    class Controller extends ArchiveManagerController
    {             
        Controller(BHistoryImportManager mgr) { super(mgr); } 

        /**
         * Execute a discover
         */
        public CommandArtifact doDiscover(Context context) throws Exception
        {                      
            super.doDiscover(context);          

            BNHaystackHistoryDeviceExt ext = (BNHaystackHistoryDeviceExt)getCurrentValue();
            getLearn().setJob(ext.getHaystackServer().submitLearnHistoriesJob()); 
            return null;
        }
    }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

    MgrColumn colPath = new MgrColumn.Path(MgrColumn.UNSEEN);
    MgrColumn colName = new MgrColumn.Name();

    MgrColumn colStatus        = new MgrColumn.Prop(BDescriptor.status,        MgrColumn.UNSEEN);
    MgrColumn colState         = new MgrColumn.Prop(BDescriptor.state,         MgrColumn.UNSEEN);
    MgrColumn colEnabled       = new MgrColumn.Prop(BDescriptor.enabled,       MgrColumn.EDITABLE | MgrColumn.UNSEEN);
    MgrColumn colExecutionTime = new MgrColumn.Prop(BDescriptor.executionTime, MgrColumn.UNSEEN);
    MgrColumn colLastAttempt   = new MgrColumn.Prop(BDescriptor.lastAttempt,   MgrColumn.UNSEEN);
    MgrColumn colLastSuccess   = new MgrColumn.Prop(BDescriptor.lastSuccess,   MgrColumn.UNSEEN);
    MgrColumn colLastFailure   = new MgrColumn.Prop(BDescriptor.lastFailure,   MgrColumn.UNSEEN);
    MgrColumn colFaultCause    = new MgrColumn.Prop(BDescriptor.faultCause,    MgrColumn.UNSEEN);

    MgrColumn colId = new MgrColumn.Prop(BNHaystackHistoryImport.id, MgrColumn.EDITABLE);
    MgrColumn colKind = new MgrColumn.Prop(BNHaystackHistoryImport.kind, MgrColumn.EDITABLE);
    MgrColumn colTz = new MgrColumn.Prop(BNHaystackHistoryImport.tz, MgrColumn.EDITABLE);

    MgrColumn colOnDemandPollEnabled   = new MgrColumn.Prop(BHistoryImport.onDemandPollEnabled,   MgrColumn.EDITABLE | MgrColumn.UNSEEN);
    MgrColumn colOnDemandPollFrequency = new MgrColumn.Prop(BHistoryImport.onDemandPollFrequency, MgrColumn.EDITABLE | MgrColumn.UNSEEN);

    MgrColumn colHistoryId   = new MgrColumn.Prop(BArchiveDescriptor.historyId,   MgrColumn.EDITABLE);

    MgrColumn[] cols = 
    { 
        colPath, colName,
        colStatus, colState, colEnabled, colExecutionTime,
        colLastAttempt, colLastSuccess, colLastFailure, colFaultCause,
        colId, colKind, colTz,
        colOnDemandPollEnabled, colOnDemandPollFrequency,
        colHistoryId
    };

    private static final BImage img = BImage.make(BNHaystackHistoryImport.TYPE.getInstance().getIcon());
}
