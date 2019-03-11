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
import org.eltech.ddm.miningcore.miningmodel.EMiningModel;
import org.eltech.ddm.miningcore.miningtask.EMiningBuildTask;
import org.omg.java.cwm.analysis.datamining.miningcore.miningmodel.MiningModel;

public class RunSystem {

    private final String[] HORIZONTAL_TEST_SET_1 = {"pregnancy/2H/HDataSet1.csv", "pregnancy/2H/HDataSet2.csv"};
    private final String[] HORIZONTAL_TEST_SET_2 = {"pregnancy/4H/HDataSet1.csv", "pregnancy/4H/HDataSet2.csv", "pregnancy/4H/HDataSet3.csv", "pregnancy/4H/HDataSet4.csv"};
    private final String[] VER_TEST_SET_1 = {"pregnancy/2V/VDataSet1.csv", "pregnancy/2V/VDataSet2.csv"};
    private final String[] VER_TEST_SET_2 = {"pregnancy/4V/VDataSet1.csv", "pregnancy/4V/VDataSet2.csv", "pregnancy/4V/VDataSet3.csv", "pregnancy/4V/VDataSet4.csv"};


    protected static EMiningAlgorithmSettings miningAlgorithmSettings;
    protected MiningAlgorithm algorithm;
    protected static ClassificationFunctionSettings miningSettings;

    private final int NUMBER_HANDLERS = 2;


    public static void main(String[] args) {
        /*TODO: 1) логгер
         *      2) kill/suspend каким образом
         *      3) учесть негавтивные сценарии
         *      4) AchieveREInitiator в агентах*/

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
            ContinuousBayesModel resultModel = (ContinuousBayesModel) createBuidTask(DataDistribution.VERTICAL_DISTRIBUTION).execute();

            System.out.println(resultModel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Cluster nodes must be launched before the test starts.
     * Example Runner.exe 127.0.0.1 2551 8 4 127.0.0.1:2551
     */
    public void test4ActorsIrisSingleNodeHorizontal() {

        try {
            createMiningSettings();
            ContinuousBayesModel resultModel = (ContinuousBayesModel) createBuidTask(DataDistribution.HORIZONTAL_DISTRIBUTION,HORIZONTAL_TEST_SET_1).execute();

            createMiningSettings();
            resultModel = (ContinuousBayesModel) createBuidTask(DataDistribution.HORIZONTAL_DISTRIBUTION, HORIZONTAL_TEST_SET_2).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cluster nodes must be launched before the test starts.
     * Example Runner.exe 127.0.0.1 2551 8 4 127.0.0.1:2551
     */
    public void test4ActorsIrisSingleNodeVertical() {
        try {
            createMiningSettings();
            ContinuousBayesModel resultModel = (ContinuousBayesModel) createBuidTask(DataDistribution.VERTICAL_DISTRIBUTION,VER_TEST_SET_1).execute();
            createMiningSettings();
            resultModel = (ContinuousBayesModel) createBuidTask(DataDistribution.VERTICAL_DISTRIBUTION, VER_TEST_SET_2).execute();
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

    private static EMiningBuildTask createBuidTask(DataDistribution dist, String... files) throws MiningException {
        AgentExecutionEnvironmentSettings executionSettings =
                new AgentExecutionEnvironmentSettings(dist);
        AgentExecutionEnvironment environment = new AgentExecutionEnvironment(executionSettings);
        MiningAlgorithm algorithm = new ContinuousNaiveBayesAlgorithm(miningSettings);

        EMiningBuildTask buildTask = new EMiningBuildTask();
        buildTask.setMiningAlgorithm(algorithm);
        buildTask.setMiningSettings(miningSettings);
        buildTask.setExecutionEnvironment(environment);

        return buildTask;
    }


}
