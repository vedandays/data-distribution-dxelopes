package org.eltech.ddm.runner;

import org.eltech.ddm.classification.ClassificationFunctionSettings;
import org.eltech.ddm.classification.naivebayes.continious.ContinuousBayesModel;
import org.eltech.ddm.classification.naivebayes.continious.ContinuousNaiveBayesAlgorithm;
import org.eltech.ddm.common.ExecuteResult;
import org.eltech.ddm.environment.DataDistribution;
import org.eltech.ddm.handlers.AgentExecutionEnvironment;
import org.eltech.ddm.handlers.AgentExecutionEnvironmentSettings;
import org.eltech.ddm.inputdata.file.csv.MiningCsvStream;
import org.eltech.ddm.miningcore.MiningException;
import org.eltech.ddm.miningcore.algorithms.MiningAlgorithm;
import org.eltech.ddm.miningcore.miningdata.ELogicalAttribute;
import org.eltech.ddm.miningcore.miningdata.ELogicalData;
import org.eltech.ddm.miningcore.miningfunctionsettings.EMiningAlgorithmSettings;
import org.eltech.ddm.miningcore.miningtask.EMiningBuildTask;

public class RunSystem {

    public static String AGENTS_INFO_PATH = "/home/derkach/test/agents_info.csv";

    protected static EMiningAlgorithmSettings miningAlgorithmSettings;
    protected static ClassificationFunctionSettings miningSettings;

    public static void main(String[] args) {

        try {
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }

        testDistr();
    }


    public static void setUp() throws Exception {
        // Create mining algorithm settings
        miningAlgorithmSettings = new EMiningAlgorithmSettings();
        miningAlgorithmSettings.setAlgorithm("Bayes");
        miningAlgorithmSettings.setClassname(ContinuousNaiveBayesAlgorithm.class.getName());
    }

    public static void testDistr() {

        try {
            createMiningSettings();
            ContinuousBayesModel resultModel =
                    (ContinuousBayesModel) createBuidTask(DataDistribution.HORIZONTAL_DISTRIBUTION, AGENTS_INFO_PATH).execute();

            System.out.println(resultModel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void createMiningSettings() throws MiningException {
        MiningCsvStream stream = new MiningCsvStream("100mb.csv", null, false);
        ELogicalData logicalData = stream.getLogicalData();
        ELogicalAttribute targetAttribute = logicalData.getAttribute("outcome_pregnancy");

        EMiningAlgorithmSettings algorithmSettings = new EMiningAlgorithmSettings();
        algorithmSettings.setName("BAYES");
        algorithmSettings.setClassname(ContinuousNaiveBayesAlgorithm.class.getName());

        miningSettings = new ClassificationFunctionSettings(logicalData);
        miningSettings.setTarget(targetAttribute);
        miningSettings.setAlgorithmSettings(algorithmSettings);
        miningSettings.verify();
    }

    private static EMiningBuildTask createBuidTask(DataDistribution dist, String agentsInfoPath) throws MiningException {
        AgentExecutionEnvironmentSettings executionSettings =
                new AgentExecutionEnvironmentSettings(dist, agentsInfoPath);
        AgentExecutionEnvironment environment = new AgentExecutionEnvironment(executionSettings);
        MiningAlgorithm algorithm = new ContinuousNaiveBayesAlgorithm(miningSettings);

        EMiningBuildTask buildTask = new EMiningBuildTask();
        buildTask.setMiningAlgorithm(algorithm);
        buildTask.setMiningSettings(miningSettings);
        buildTask.setExecutionEnvironment(environment);

        return buildTask;
    }


}
