package org.eltech.ddm.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.eltech.ddm.miningcore.MiningException;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class JobFailed implements Serializable {
    private final MiningException exception;
}
