package org.eltech.ddm.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eltech.ddm.environment.DataDistribution;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.miningcore.algorithms.MiningBlock;
import org.eltech.ddm.miningcore.miningfunctionsettings.EMiningFunctionSettings;
import org.eltech.ddm.miningcore.miningmodel.EMiningModel;
import org.omg.java.cwm.analysis.datamining.miningcore.miningfunctionsettings.MiningFunctionSettings;

import java.io.Serializable;
import java.util.function.Supplier;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteJob implements Serializable {
    private MiningBlock block;
    private MiningFunctionSettings settings;
    private Class<? extends EMiningModel> miningModel;
    private MiningInputStream inputStream;
    private DataDistribution dataDistribution;
}


