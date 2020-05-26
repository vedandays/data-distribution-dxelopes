package org.eltech.ddm.runner;

import org.eltech.ddm.classification.ClassificationFunctionSettings;
import org.eltech.ddm.classification.naivebayes.continious.ContinuousBayesModel;
import org.eltech.ddm.classification.naivebayes.continious.ContinuousNaiveBayesAlgorithm;
import org.eltech.ddm.distribution.res.ResParser;
import org.eltech.ddm.distribution.settings.ASettings;
import org.eltech.ddm.distribution.settings.ConnectionSettings;
import org.eltech.ddm.distribution.settings.FileSettings;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RunSystem {

    public static String AGENTS_INFO_PATH = "E:\\data\\agents_info.csv";
    public static String TARGET_ATTRIBUTE = "atr42";
    public static String LOGICAL_DATA = "E:\\data\\data\\mydata\\myLogicalData.csv";
    //    public static String[] AGENTS_ARRAY = {"Miner1,192.168.0.105,DESKTOP-E233JR5,1098,7778,org.eltech.ddm.agents.AgentMiner,D:\\data\\data_Iris.csv"};
    public static List<ASettings> AGENTS_ARRAY = new ArrayList<>();
    public static AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    static {
        // file settings
//        ASettings fileSettings = new FileSettings("Miner1,192.168.0.106,DESKTOP-DONOSPA,1098,7778,org.eltech.ddm.agents.AgentMiner,F:\\Andrey\\data\\data\\data_Iris.csv");
        ASettings fileSettings = new FileSettings("Miner1,192.168.0.105,DESKTOP-E233JR5,1098,7778,org.eltech.ddm.agents.AgentMiner,D:\\test1.csv");
//        AGENTS_ARRAY.add(fileSettings);

        // sql settings
        ConnectionSettings postgresqlSettings = new ConnectionSettings("Miner2,192.168.0.105,DESKTOP-E233JR5,1098,7778,org.eltech.ddm.agents.AgentMiner,D:\\data\\data_Iris.csv");
        postgresqlSettings.setUrl("jdbc:postgresql://localhost:5432/kddcup");
        postgresqlSettings.setUser("postgres");
        postgresqlSettings.setPassword("qwerty");
        postgresqlSettings.setSchemaName("public");
        postgresqlSettings.setColumnNames(Arrays.asList("test_table_42"));
        AGENTS_ARRAY.add(postgresqlSettings);
    }


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
            ContinuousBayesModel resultModel = (ContinuousBayesModel) createBuidTask(null, null, AGENTS_ARRAY).execute();

            System.out.println("resultModel:");
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

    private static EMiningBuildTask createBuidTask(DataDistribution dist, String agentsInfoPath, List<ASettings>  agentsArray) throws MiningException {
        AgentExecutionEnvironmentSettings executionSettings =
                new AgentExecutionEnvironmentSettings(dist, agentsArray);
        AgentExecutionEnvironment environment = new AgentExecutionEnvironment(executionSettings);
        DataDistribution dataDistribution;

        while (!atomicBoolean.get());

        dataDistribution = ResParser.readResult();
        executionSettings.setDataDistribution(dataDistribution);

        MiningAlgorithm algorithm = new ContinuousNaiveBayesAlgorithm(miningSettings);

        EMiningBuildTask buildTask = new EMiningBuildTask();
        buildTask.setMiningAlgorithm(algorithm);
        buildTask.setMiningSettings(miningSettings);
        buildTask.setExecutionEnvironment(environment);

        return buildTask;
    }
}
