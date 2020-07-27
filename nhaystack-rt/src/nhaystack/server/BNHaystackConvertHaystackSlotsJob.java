//
// Copyright (c) 2018. Tridium, Inc. All rights reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Jul 2018  Andrew Saunders  Creation
//
package nhaystack.server;

import javax.baja.job.BJobState;
import javax.baja.job.BSimpleJob;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.space.BComponentSpace;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BString;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.Lexicon;

@NiagaraType
public class BNHaystackConvertHaystackSlotsJob extends BSimpleJob
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackConvertHaystackSlotsJob(2979906276)1.0$ @*/
/* Generated Mon Jul 09 10:30:00 EDT 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackConvertHaystackSlotsJob.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackConvertHaystackSlotsJob()
    {
        this.service = null;
    }

    public BNHaystackConvertHaystackSlotsJob(BNHaystackService service)
    { 
        this.service = service;
    }

    @Override
    public void run(Context cx) throws Exception
    {
        BComponentSpace componentSpace = Sys.getStation().getComponentSpace();
        if (componentSpace == null)
        {
            return;
        }

        try
        {
            service.setSlotConversionInProgress(true);

            final BComponent[] allComponents = componentSpace.getAllComponents();
            log().message(LEX.getText("haystack.slot.conv.start",
                new Object[] {allComponents.length}));
            for (int i = 0; i < allComponents.length; ++i)
            {
                HaystackSlotUtil.migrateHaystackTags(allComponents[i], this);
                progress((100 * i) / allComponents.length);
            }

            log().message(LEX.getText("haystack.slot.conv.end",
                new Object[] {count, warningCount, errorCount}));
        }
        finally
        {
            service.setSlotConversionInProgress(false);
        }

        // The conversion job sometimes runs at service started and, if it runs
        // for a long time, it may prevent nhaystack initialization. Run
        // initialization now if it tried to run while conversion was in
        // progress.
        if (service.wasInitBlockedBySlotConversion())
        {
            log().message(LEX.getText("haystack.slot.conv.initializeHaystack"));
            service.initializeHaystack();
        }
    }

    @Override
    public void success()
    {
        if (errorCount > 0)
        {
            log().failed(LEX.getText("haystack.slot.conv.complete.errors"));
            complete(BJobState.failed);
        }
        else if (warningCount > 0)
        {
            log().success(LEX.getText("haystack.slot.conv.complete.warnings"));
            complete(BJobState.success);
            clearUpgradeFault();
        }
        else
        {
            log().success(LEX.getText("haystack.slot.conv.complete.noErrors"));
            complete(BJobState.success);
            clearUpgradeFault();
        }
    }

    //////////////////////////////
    // Access
    //////////////////////////////

    public void setUpgradeFault(String fault)
    {
        Property upgradeProp = service.getProperty(UPGRADE_FAULT);
        if (upgradeProp == null)
        {
            service.add(UPGRADE_FAULT, BString.make(fault), Flags.TRANSIENT);
        }
        else
        {
            service.set(upgradeProp, BString.make(fault));
        }

        Property retryProp = service.getProperty(RETRY_UPGRADE_ON_RESTART);
        if (retryProp == null)
        {
            service.add(RETRY_UPGRADE_ON_RESTART, BBoolean.TRUE);
        }
        else
        {
            service.set(retryProp, BBoolean.TRUE);
        }
    }

    private void clearUpgradeFault()
    {
        Property upgradeProp = service.getProperty(UPGRADE_FAULT);
        if (upgradeProp != null)
        {
            service.remove(upgradeProp);
        }

        Property retryProp = service.getProperty(RETRY_UPGRADE_ON_RESTART);
        if (retryProp != null)
        {
            service.remove(retryProp);
        }
    }

    public void incWarningCount()
    {
        ++warningCount;
    }

    public void incErrorCount()
    {
        ++errorCount;
    }

    public void incCount()
    {
        ++count;
    }

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    public static final String UPGRADE_FAULT = "upgradeFault";
    public static final String RETRY_UPGRADE_ON_RESTART = "retryUpgradeOnRestart";

    private final BNHaystackService service;
    private long count;
    private long warningCount;
    private long errorCount;
}
