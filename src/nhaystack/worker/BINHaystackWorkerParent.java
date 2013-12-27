/**
  * Copyright (c) 2012 All Right Reserved, J2 Innovations
  */
package nhaystack.worker;

import javax.baja.status.*;
import javax.baja.sys.*;

/**
  * BINHaystackWorkerParent is the parent of a BNHaystackWorker
  */
public interface BINHaystackWorkerParent extends BInterface
{
    public static final Type TYPE = Sys.loadType(BINHaystackWorkerParent.class);

    public BStatus getStatus();
}
