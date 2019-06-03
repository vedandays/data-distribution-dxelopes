package org.eltech.ddm.runner;

import org.eltech.ddm.classification.ClassificationFunctionSettings;
import org.eltech.ddm.classification.naivebayes.continious.ContinuousBayesModel;
import org.eltech.ddm.classification.naivebayes.continious.ContinuousNaiveBayesAlgorithm;
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
    public static String TARGET_ATTRIBUTE = "outcome_pregnancy";
    public static String LOGICAL_DATA = "/home/derkach/test/100mb.csv";
    public static String[] AGENTS_ARRAY =
            {"Miner1,192.168.31.192,mage,1099,34561,org.eltech.ddm.agents.AgentMiner,/home/derkach/test/100v1.csv",
            "Miner2,192.168.31.192,mage,1099,34561,org.eltech.ddm.agents.AgentMiner,/home/derkach/test/100v2.csv",
            "Miner3,192.168.31.192,mage,1099,34561,org.eltech.ddm.agents.AgentMiner,/home/derkach/test/100v3.csv",
            "Miner4,192.168.31.192,mage,1099,34561,org.eltech.ddm.agents.AgentMiner,/home/derkach/test/100v4.csv"};


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
                    (ContinuousBayesModel) createBuidTask(DataDistribution.VERTICAL_DISTRIBUTION, null, AGENTS_ARRAY)
                            .execute();

            System.out.println(resultModel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void createMiningSettings() throws MiningException {
        MiningCsvStream stream = new MiningCsvStream(LOGICAL_DATA, null, true);
        ELogicalData logicalData = stream.getLogicalData();
        ELogicalAttribute targetAttribute = logicalData.getAttribute(TARGET_ATTRIBUTE);

        EMiningAlgorithmSettings algorithmSettings = new EMiningAlgorithmSettings();
        algorithmSettings.setName("BAYES");
        algorithmSettings.setClassname(ContinuousNaiveBayesAlgorithm.class.getName());

        miningSettings = new ClassificationFunctionSettings(logicalData);
        miningSettings.setTarget(targetAttribute);
        miningSettings.setAlgorithmSettings(algorithmSettings);
        miningSettings.verify();
    }

    private static EMiningBuildTask createBuidTask(DataDistribution dist, String agentsInfoPath, String[] agentsArray) throws MiningException {
        AgentExecutionEnvironmentSettings executionSettings =
                new AgentExecutionEnvironmentSettings(dist, agentsArray);
        AgentExecutionEnvironment environment = new AgentExecutionEnvironment(executionSettings);
        MiningAlgorithm algorithm = new ContinuousNaiveBayesAlgorithm(miningSettings);

        EMiningBuildTask buildTask = new EMiningBuildTask();
        buildTask.setMiningAlgorithm(algorithm);
        buildTask.setMiningSettings(miningSettings);
        buildTask.setExecutionEnvironment(environment);

        return buildTask;
    }


}
