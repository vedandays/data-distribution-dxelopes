package org.eltech.ddm.clustering.cdbase.kmeans;


import org.eltech.ddm.backend.runner.Runner;
import org.eltech.ddm.classification.ClassificationFunctionSettings;
import org.eltech.ddm.classification.naivebayes.continious.ContinuousBayesModel;
import org.eltech.ddm.classification.naivebayes.continious.ContinuousNaiveBayesAlgorithm;
import org.eltech.ddm.environment.DataDistribution;
import org.eltech.ddm.frotend.environment.ActorsExecutionEnvironment;
import org.eltech.ddm.frotend.environment.ActorsExecutionEnvironmentSettings;
import org.eltech.ddm.inputdata.file.csv.MiningCsvStream;
import org.eltech.ddm.miningcore.MiningException;
import org.eltech.ddm.miningcore.algorithms.MiningAlgorithm;
import org.eltech.ddm.miningcore.miningdata.ELogicalAttribute;
import org.eltech.ddm.miningcore.miningdata.ELogicalData;
import org.eltech.ddm.miningcore.miningfunctionsettings.EMiningAlgorithmSettings;
import org.eltech.ddm.miningcore.miningtask.EMiningBuildTask;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;


public class BayesTest {

    private final int NUMBER_HANDLERS = 2;

    protected EMiningAlgorithmSettings miningAlgorithmSettings;
    protected MiningAlgorithm algorithm;
    protected ClassificationFunctionSettings miningSettings;

    @Before
    public void setUp() throws Exception {
        // Create mining algorithm settings
        miningAlgorithmSettings = new EMiningAlgorithmSettings();
        miningAlgorithmSettings.setAlgorithm("Bayes");
    }

    private final String[] HORIZONTAL_TEST_SET_1 = {"pregnancy/2H/HDataSet1.csv", "pregnancy/2H/HDataSet2.csv"};
    private final String[] HORIZONTAL_TEST_SET_2 = {"pregnancy/4H/HDataSet1.csv", "pregnancy/4H/HDataSet2.csv", "pregnancy/4H/HDataSet3.csv", "pregnancy/4H/HDataSet4.csv"};
    private final String[] VER_TEST_SET_1 = {"pregnancy/2V/VDataSet1.csv", "pregnancy/2V/VDataSet2.csv"};
    private final String[] VER_TEST_SET_2 = {"pregnancy/4V/VDataSet1.csv", "pregnancy/4V/VDataSet2.csv", "pregnancy/4V/VDataSet3.csv", "pregnancy/4V/VDataSet4.csv"};


    public static void main(String[] args) {
        Runner.main(null);
    }
    /**
     * Cluster nodes must be launched before the test starts.
     * Example Runner.exe 127.0.0.1 2551 8 4 127.0.0.1:2551
     */
    @Test
    public void test4ActorsIrisSingleNodeHorizontal() {

        try {
            createMiningSettings();
            ContinuousBayesModel resultModel = (ContinuousBayesModel) createBuidTask(DataDistribution.HORIZONTAL_DISTRIBUTION,HORIZONTAL_TEST_SET_1).execute();

            createMiningSettings();
            resultModel = (ContinuousBayesModel) createBuidTask(DataDistribution.HORIZONTAL_DISTRIBUTION, HORIZONTAL_TEST_SET_2).execute();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Cluster nodes must be launched before the test starts.
     * Example Runner.exe 127.0.0.1 2551 8 4 127.0.0.1:2551
     */
    @Test
    public void test4ActorsIrisSingleNodeVertical() {
        try {
            createMiningSettings();
            ContinuousBayesModel resultModel = (ContinuousBayesModel) createBuidTask(DataDistribution.VERTICAL_DISTRIBUTION,VER_TEST_SET_1).execute();
            createMiningSettings();
            resultModel = (ContinuousBayesModel) createBuidTask(DataDistribution.VERTICAL_DISTRIBUTION, VER_TEST_SET_2).execute();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private void createMiningSettings() throws MiningException {
        MiningCsvStream stream = new MiningCsvStream("data.csv", null, false);
        ELogicalData logicalData = stream.getLogicalData();
        ELogicalAttribute targetAttribute = logicalData.getAttribute("outcome_pregnancy");

        EMiningAlgorithmSettings algorithmSettings = new EMiningAlgorithmSettings();
        algorithmSettings.setName("BAYES");
        algorithmSettings.setClassname("BATES");

        miningSettings = new ClassificationFunctionSettings(logicalData);
        miningSettings.setTarget(targetAttribute);
        miningSettings.setAlgorithmSettings(algorithmSettings);
        miningSettings.verify();
    }

    private EMiningBuildTask createBuidTask(DataDistribution dist, String... files) throws MiningException {
        ActorsExecutionEnvironmentSettings executionSettings =
                new ActorsExecutionEnvironmentSettings(dist)
                        .provideDatafile(files);
        ActorsExecutionEnvironment environment = new ActorsExecutionEnvironment(executionSettings);
        MiningAlgorithm algorithm = new ContinuousNaiveBayesAlgorithm(miningSettings);

        EMiningBuildTask buildTask = new EMiningBuildTask();
        buildTask.setMiningAlgorithm(algorithm);
        buildTask.setMiningSettings(miningSettings);
        buildTask.setExecutionEnvironment(environment);

        return buildTask;
    }


}
