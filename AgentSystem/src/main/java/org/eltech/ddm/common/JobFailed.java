package org.eltech.ddm.common;

import org.eltech.ddm.miningcore.MiningException;
import java.io.Serializable;


public class JobFailed implements Serializable {
    private final Exception exception;

    public JobFailed(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}
