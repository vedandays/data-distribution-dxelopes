package org.eltech.ddm.common;

import lombok.*;
import org.eltech.ddm.miningcore.miningmodel.EMiningModel;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteResult implements Serializable {
    private EMiningModel model;
}
