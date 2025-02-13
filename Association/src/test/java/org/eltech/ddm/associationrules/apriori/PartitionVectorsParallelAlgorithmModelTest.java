package org.eltech.ddm.associationrules.apriori;

import org.eltech.ddm.associationrules.AssociationRulesFunctionSettings;
import org.eltech.ddm.associationrules.apriori.partition.PartitionVectorsParallelAlgorithm;
import org.eltech.ddm.environment.ConcurrencyExecutionEnvironment;
import org.eltech.ddm.inputdata.DataSplitType;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.miningcore.MiningException;
import org.eltech.ddm.miningcore.miningfunctionsettings.DataProcessingStrategy;
import org.eltech.ddm.miningcore.miningfunctionsettings.MiningModelProcessingStrategy;
import org.eltech.ddm.miningcore.miningtask.EMiningBuildTask;

public class PartitionVectorsParallelAlgorithmModelTest extends AprioriModelTest {



	@Override
	protected AprioriMiningModel buildModel(AssociationRulesFunctionSettings miningSettings, MiningInputStream inputData) throws MiningException {
		miningSettings.getAlgorithmSettings().setDataSplitType(DataSplitType.block);
		miningSettings.getAlgorithmSettings().setDataProcessingStrategy(DataProcessingStrategy.SeparatedDataSet);
		miningSettings.getAlgorithmSettings().setModelProcessingStrategy(MiningModelProcessingStrategy.SingleMiningModel);
		
		ExecutionEnvironmentSettings executionEnvironmentSettings = new ExecutionEnvironmentSettings();
		executionEnvironmentSettings.setNumberHandlers(2);

		ConcurrencyExecutionEnvironment environment = new ConcurrencyExecutionEnvironment(executionEnvironmentSettings);
		
		PartitionVectorsParallelAlgorithm algorithm = new PartitionVectorsParallelAlgorithm(miningSettings);

		EMiningBuildTask buildTask = new EMiningBuildTask();
		buildTask.setInputStream(inputData);
		buildTask.setMiningAlgorithm(algorithm); 
		buildTask.setMiningSettings(miningSettings);
		buildTask.setExecutionEnvironment(environment);
		AprioriMiningModel model = (AprioriMiningModel) buildTask.execute();
		
		return model;
	}

}
