//
// Copyright (c) 2018, VRT Systems
//
// Based on BNHaystackHistoryImportManager
// Copyright (c) 2012, J2 Innovations
//
// Licensed under the Academic Free License version 3.0
//
// History:
//   24 Apr 2018  Stuart Longland  Creation based on BNHaystackHistoryImportManager

package nhaystack.ui.history;

import nhaystack.driver.history.*;
import nhaystack.driver.history.learn.BNHaystackHistoryExpEntry;

import javax.baja.driver.history.*;
import javax.baja.driver.ui.history.*;
import javax.baja.driver.util.*;
import javax.baja.gx.*;
import javax.baja.ui.*;
import javax.baja.job.*;
import javax.baja.sys.*;
import javax.baja.workbench.mgr.*;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.nre.annotations.AgentOn;

/**
  * BNHaystackHistoryExportManager manages the transfer of local Baja
  * history data into remote Project Haystack servers.
  */
@NiagaraType(
  agent =   @AgentOn(
    types = "nhaystack:NHaystackHistoryDeviceExt"
  )
)
public final class BNHaystackHistoryExportManager extends BHistoryExportManager
{

/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.learn.BNHaystackHistoryExportManager(148152857)1.0$ @*/
/* Generated Tue Apr 24 10:37:09 AEST 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackHistoryExportManager.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  protected ExportModel   makeExportModel() { return new Model(this);      }
  public    MgrController makeController()  { return new Controller(this); }
  protected MgrLearn      makeLearn()       { return new Learn(this);      }

////////////////////////////////////////////////////////////////
// Model
////////////////////////////////////////////////////////////////

  private class Model extends ExportModel
  {
    public Model(BNHaystackHistoryExportManager exportManager)
    {
      super(exportManager);
    }

    protected MgrColumn[] makeColumns()
    {
      return cols;
    }

    public MgrTypeInfo[] getNewTypes()
    {
      return MgrTypeInfo.makeArray(BNHaystackHistoryExport.TYPE);
    }
  }

////////////////////////////////////////////////////////////////
// Learn
////////////////////////////////////////////////////////////////

  class Learn extends MgrLearn
  {
    Learn(BNHaystackHistoryExportManager mgr) { super(mgr); }

    protected MgrColumn[] makeColumns()
    {
      return new MgrColumn[]
      {
        new MgrColumn.Name(),
            new MgrColumn.Prop(BNHaystackHistoryExpEntry.historyId),
            new MgrColumn.Prop(BNHaystackHistoryExpEntry.tz),
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
      BNHaystackHistoryExpEntry entry = (BNHaystackHistoryExpEntry)dis;

      row.setDefaultName(entry.getName());

      row.setCell(colHistoryId, entry.getHistoryId());
      row.setCell(colTz, entry.getTz());
    }

    public boolean isExisting(Object discovery, BComponent component)
    {
      boolean res = false;

      if (discovery instanceof BNHaystackHistoryExpEntry)
      {
        BNHaystackHistoryExpEntry entry = (BNHaystackHistoryExpEntry) discovery;
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
    BNHaystackHistoryExpEntry[] rows = event.getChildren(
        BNHaystackHistoryExpEntry.class);

    for (int i = 0; i < rows.length; i++)
      rows[i].loadSlots();

    getLearn().updateRoots(rows);
  }

////////////////////////////////////////////////////////////////
// Controller
////////////////////////////////////////////////////////////////

  class Controller extends ArchiveManagerController
  {
    Controller(BHistoryExportManager mgr) { super(mgr); }

    public CommandArtifact doDiscover(Context context) throws Exception
    {
      super.doDiscover(context);

      BNHaystackHistoryDeviceExt ext = (BNHaystackHistoryDeviceExt)getCurrentValue();
      getLearn().setJob(ext.getHaystackServer().submitLearnExpHistoriesJob());
      return null;
    }
  }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

  MgrColumn colPath
    = new MgrColumn.Path(MgrColumn.UNSEEN);
  MgrColumn colName
    = new MgrColumn.Name();

  MgrColumn colStatus
    = new MgrColumn.Prop(BDescriptor.status, MgrColumn.UNSEEN);
  MgrColumn colState
    = new MgrColumn.Prop(BDescriptor.state, MgrColumn.UNSEEN);
  MgrColumn colEnabled
    = new MgrColumn.Prop(BDescriptor.enabled,
        MgrColumn.EDITABLE | MgrColumn.UNSEEN);
  MgrColumn colExecutionTime
    = new MgrColumn.Prop(BDescriptor.executionTime, MgrColumn.EDITABLE);
  MgrColumn colUploadFromTime
    = new MgrColumn.Prop(BNHaystackHistoryExport.uploadFromTime,MgrColumn.EDITABLE);

  MgrColumn colLastAttempt
    = new MgrColumn.Prop(BDescriptor.lastAttempt,
        MgrColumn.EDITABLE | MgrColumn.READONLY);
  MgrColumn colLastSuccess
    = new MgrColumn.Prop(BDescriptor.lastSuccess,
        MgrColumn.EDITABLE | MgrColumn.READONLY);
  MgrColumn colLastFailure
    = new MgrColumn.Prop(BDescriptor.lastFailure,
        MgrColumn.EDITABLE | MgrColumn.READONLY);
  MgrColumn colFaultCause
    = new MgrColumn.Prop(BDescriptor.faultCause,
        MgrColumn.EDITABLE | MgrColumn.READONLY);

  MgrColumn colId
    = new MgrColumn.Prop(BNHaystackHistoryExport.id, MgrColumn.EDITABLE);
  MgrColumn colHistoryId
    = new MgrColumn.Prop(BArchiveDescriptor.historyId, MgrColumn.EDITABLE);
  MgrColumn colTz
    = new MgrColumn.Prop(BNHaystackHistoryExport.tz,
        MgrColumn.EDITABLE);

  MgrColumn colUploadSize
    = new MgrColumn.Prop(BNHaystackHistoryExport.uploadSize,
        MgrColumn.EDITABLE);

  MgrColumn[] cols =
  {
    colPath, colName,
    colStatus, colState, colEnabled, colExecutionTime, colUploadFromTime,
    colLastAttempt, colLastSuccess, colLastFailure, colFaultCause,
    colId, colUploadSize, colHistoryId,
    colTz,
  };

  private static final BImage img = BImage.make(
      BNHaystackHistoryExport.TYPE.getInstance().getIcon());
}
