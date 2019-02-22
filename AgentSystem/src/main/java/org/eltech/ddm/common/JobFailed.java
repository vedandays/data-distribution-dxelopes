package org.eltech.ddm.common;

import org.eltech.ddm.miningcore.MiningException;
import java.io.Serializable;


public class JobFailed implements Serializable {
    private final MiningException exception;

    public JobFailed(MiningException exception) {
        this.exception = exception;
    }

    public MiningException getException() {
        return exception;
    }
}
