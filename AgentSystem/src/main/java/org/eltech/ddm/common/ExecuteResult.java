package org.eltech.ddm.common;

import org.eltech.ddm.miningcore.miningmodel.EMiningModel;
import java.io.Serializable;

public class ExecuteResult implements Serializable {
    private EMiningModel model;

    public ExecuteResult(EMiningModel model) {
        this.model = model;
    }

    public EMiningModel getModel() {
        return model;
    }

    public void setModel(EMiningModel model) {
        this.model = model;
    }
}
