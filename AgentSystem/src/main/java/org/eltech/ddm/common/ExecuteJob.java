package org.eltech.ddm.common;


import org.eltech.ddm.environment.DataDistribution;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.miningcore.algorithms.MiningBlock;
import org.eltech.ddm.miningcore.miningfunctionsettings.EMiningFunctionSettings;
import org.eltech.ddm.miningcore.miningmodel.EMiningModel;
import org.omg.java.cwm.analysis.datamining.miningcore.miningfunctionsettings.MiningFunctionSettings;

import java.io.Serializable;
import java.util.function.Supplier;

public class ExecuteJob implements Serializable {
    private MiningBlock block;
    private MiningFunctionSettings settings;
    private Class<? extends EMiningModel> miningModel;
    private MiningInputStream inputStream;
    private DataDistribution dataDistribution;

    public ExecuteJob(MiningBlock block,
                      MiningFunctionSettings settings,
                      Class<? extends EMiningModel> miningModel,
                      MiningInputStream inputStream,
                      DataDistribution dataDistribution) {
        this.block = block;
        this.settings = settings;
        this.miningModel = miningModel;
        this.inputStream = inputStream;
        this.dataDistribution = dataDistribution;
    }

    public ExecuteJob() {
    }

    public void setBlock(MiningBlock block) {
        this.block = block;
    }

    public void setSettings(MiningFunctionSettings settings) {
        this.settings = settings;
    }

    public void setMiningModel(Class<? extends EMiningModel> miningModel) {
        this.miningModel = miningModel;
    }

    public void setInputStream(MiningInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setDataDistribution(DataDistribution dataDistribution) {
        this.dataDistribution = dataDistribution;
    }

    public MiningBlock getBlock() {
        return block;
    }

    public MiningFunctionSettings getSettings() {
        return settings;
    }

    public Class<? extends EMiningModel> getMiningModel() {
        return miningModel;
    }

    public MiningInputStream getInputStream() {
        return inputStream;
    }

    public DataDistribution getDataDistribution() {
        return dataDistribution;
    }
}


