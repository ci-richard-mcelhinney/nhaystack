//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.ui.history;

import javax.baja.driver.history.BArchiveDescriptor;
import javax.baja.driver.history.BHistoryImport;
import javax.baja.driver.ui.history.ArchiveManagerController;
import javax.baja.driver.ui.history.BHistoryImportManager;
import javax.baja.driver.ui.history.ImportModel;
import javax.baja.driver.util.BDescriptor;
import javax.baja.gx.BImage;
import javax.baja.job.BJob;
import javax.baja.nre.annotations.AgentOn;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.CommandArtifact;
import javax.baja.workbench.mgr.MgrColumn;
import javax.baja.workbench.mgr.MgrController;
import javax.baja.workbench.mgr.MgrEditRow;
import javax.baja.workbench.mgr.MgrLearn;
import javax.baja.workbench.mgr.MgrTypeInfo;
import nhaystack.driver.history.BNHaystackHistoryDeviceExt;
import nhaystack.driver.history.BNHaystackHistoryImport;
import nhaystack.driver.history.learn.BNHaystackHistoryEntry;

/**
 * BNHaystackHistoryImportManager manages the transfer of remote haystack
 * history data into local Baja histories.
 */
@NiagaraType(
  agent =   @AgentOn(
    types = "nhaystack:NHaystackHistoryDeviceExt"
  )
)
public final class BNHaystackHistoryImportManager extends BHistoryImportManager
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.history.BNHaystackHistoryImportManager(148152857)1.0$ @*/
/* Generated Mon Nov 20 10:31:35 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackHistoryImportManager.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    protected ImportModel   makeImportModel() { return new Model(this);      }
    @Override
    public    MgrController makeController()  { return new Controller(this); }
    @Override
    protected MgrLearn      makeLearn()       { return new Learn(this);      }

////////////////////////////////////////////////////////////////
// Model
////////////////////////////////////////////////////////////////

    private class Model extends ImportModel
    {
        public Model(BNHaystackHistoryImportManager importManager)
        {
            super(importManager);
        }

        @Override
        protected MgrColumn[] makeColumns()
        {
            return cols;
        }

        @Override
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

        @Override
        protected MgrColumn[] makeColumns()
        {                     
            return new MgrColumn[]
            {
                new MgrColumn.Name(),
                new MgrColumn.Prop(BNHaystackHistoryEntry.id),
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
            return img; 
        }

        @Override
        public MgrTypeInfo[] toTypes(Object dis)
        {
            return getModel().getNewTypes(); 
        }

        @Override
        public void toRow(Object dis, MgrEditRow row) throws Exception
        {                                      
            BNHaystackHistoryEntry entry = (BNHaystackHistoryEntry)dis;

            row.setDefaultName(entry.getName());

            row.setCell(colId, entry.getId());
            row.setCell(colHistoryId, entry.getHistoryId());
            row.setCell(colImportedTags, entry.getImportedTags());
        }                   

        @Override
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

        @Override
        public void jobComplete(BJob job)
        {
            super.jobComplete(job);
            updateDiscoveryRows(job);
        }
    } 

    private void updateDiscoveryRows(BComponent event)
    {
        BNHaystackHistoryEntry[] rows = event.getChildren(BNHaystackHistoryEntry.class);

        for (BNHaystackHistoryEntry row : rows)
            row.loadSlots();

        getLearn().updateRoots(rows);
    }

////////////////////////////////////////////////////////////////
// Controller
////////////////////////////////////////////////////////////////

    class Controller extends ArchiveManagerController
    {             
        Controller(BHistoryImportManager mgr) { super(mgr); } 

        @Override
        public CommandArtifact doDiscover(Context context) throws Exception
        {                      
            super.doDiscover(context);          

            BNHaystackHistoryDeviceExt ext = (BNHaystackHistoryDeviceExt)getCurrentValue();
            getLearn().setJob(ext.getHaystackServer().submitLearnHistoriesJob()); 
            return null;
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    final MgrColumn colPath = new MgrColumn.Path(MgrColumn.UNSEEN);
    final MgrColumn colName = new MgrColumn.Name();

    final MgrColumn colStatus        = new MgrColumn.Prop(BDescriptor.status,        MgrColumn.UNSEEN);
    final MgrColumn colState         = new MgrColumn.Prop(BDescriptor.state,         MgrColumn.UNSEEN);
    final MgrColumn colEnabled       = new MgrColumn.Prop(BDescriptor.enabled,       MgrColumn.EDITABLE | MgrColumn.UNSEEN);
    final MgrColumn colExecutionTime = new MgrColumn.Prop(BDescriptor.executionTime, MgrColumn.EDITABLE);
    final MgrColumn colLastAttempt   = new MgrColumn.Prop(BDescriptor.lastAttempt,   MgrColumn.EDITABLE | MgrColumn.READONLY);
    final MgrColumn colLastSuccess   = new MgrColumn.Prop(BDescriptor.lastSuccess,   MgrColumn.EDITABLE | MgrColumn.READONLY);
    final MgrColumn colLastFailure   = new MgrColumn.Prop(BDescriptor.lastFailure,   MgrColumn.EDITABLE | MgrColumn.READONLY);
    final MgrColumn colFaultCause    = new MgrColumn.Prop(BDescriptor.faultCause,    MgrColumn.EDITABLE | MgrColumn.READONLY);

    final MgrColumn colId           = new MgrColumn.Prop(BNHaystackHistoryImport.id,           MgrColumn.EDITABLE | MgrColumn.READONLY);
    final MgrColumn colHistoryId    = new MgrColumn.Prop(BArchiveDescriptor.historyId,         MgrColumn.EDITABLE);
    final MgrColumn colImportedTags = new MgrColumn.Prop(BNHaystackHistoryImport.importedTags, MgrColumn.EDITABLE | MgrColumn.UNSEEN);

    final MgrColumn colOnDemandPollEnabled   = new MgrColumn.Prop(BHistoryImport.onDemandPollEnabled,   MgrColumn.EDITABLE);
    final MgrColumn colOnDemandPollFrequency = new MgrColumn.Prop(BHistoryImport.onDemandPollFrequency, MgrColumn.EDITABLE);

    final MgrColumn[] cols =
    { 
        colPath, colName,
        colStatus, colState, colEnabled, colExecutionTime,
        colLastAttempt, colLastSuccess, colLastFailure, colFaultCause,
        colId, colHistoryId, 
        colOnDemandPollEnabled, colOnDemandPollFrequency,
        colImportedTags,
    };

    private static final BImage img = BImage.make(BNHaystackHistoryImport.TYPE.getInstance().getIcon());
}
