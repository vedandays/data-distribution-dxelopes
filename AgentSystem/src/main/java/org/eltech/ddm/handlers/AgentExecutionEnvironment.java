package org.eltech.ddm.handlers;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.eltech.ddm.agents.AgentInfo;
import org.eltech.ddm.distribution.central.CentralAgent;
import org.eltech.ddm.distribution.settings.ConnectionSettings;
import org.eltech.ddm.distribution.settings.FileSettings;
import org.eltech.ddm.environment.ExecutionEnvironment;
import org.eltech.ddm.handlers.thread.ConcurrencyExecutorFactory;
import org.eltech.ddm.handlers.thread.ConcurrencyMiningExecutor;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.inputdata.db.MiningDBStream;
import org.eltech.ddm.inputdata.file.csv.MiningCsvStream;
import org.eltech.ddm.miningcore.MiningErrorCode;
import org.eltech.ddm.miningcore.MiningException;
import org.eltech.ddm.miningcore.algorithms.*;

import java.util.ArrayList;
import java.util.List;
/**
 * Agent execution environment provides environment for performing algorithms
 * in multi-agent system way.
 * @author Derkach Petr
 */
public class AgentExecutionEnvironment extends ExecutionEnvironment<AgentMiningExecutor, AgentExecutorFactory> {

    private AgentExecutionEnvironmentSettings settings;
    private ConcurrencyExecutorFactory singleThreadFactory;


    public AgentExecutionEnvironment(AgentExecutionEnvironmentSettings settings) throws ParallelExecutionException {
        this.settings = settings;
        initEnvironment();
    }

    @Override
    protected void initEnvironment() throws ParallelExecutionException {
        initJadePlatform();
        miningExecutorFactory = new AgentExecutorFactory(settings);
        singleThreadFactory = new ConcurrencyExecutorFactory(1);

    }

    @Override
    protected MiningExecutor createExecutorTree(MiningSequence sequence) throws MiningException {
        List<MiningExecutor> executors = createExecutors(sequence);
        MiningExecutor handler = executors.get(0);
        fullExecutor(handler.getBlock(), handler);
        return handler;
    }

    @Override
    public void deploy(MiningAlgorithm algorithm) throws MiningException {
        MiningSequence sequence = null;
        switch (settings.getDataDistribution()) {
            case CENTRALIZATION: {
                sequence = algorithm.getCentralizedParallelAlgorithm();
                break;
            }
            case HORIZONTAL_DISTRIBUTION: {
                sequence = algorithm.getHorDistributedAlgorithm();
                break;
            }
            case VERTICAL_DISTRIBUTION: {
                sequence = algorithm.getVerDistributedAlgorithm();
                break;
            }
        }
        if (sequence == null)
            throw new MiningException(MiningErrorCode.PARALLEL_EXECUTION_ERROR,
                    "The algorithm has not structure for data distribution " + settings.getDataDistribution());
        mainExecutor = createExecutorTree(sequence);

    }

    @Override
    protected List<MiningExecutor> createExecutors(MiningBlock block) throws MiningException {

        List<MiningExecutor> execs = new ArrayList<>();

        for (AgentInfo agent : settings.getAgentInfoArrayList()) {
            if (block instanceof MiningLoopVectors) {
                MiningInputStream inputStream;
                if (agent.getConnectionSettings() instanceof FileSettings) {//создание MiningCsvStream
                    inputStream = MiningCsvStream.createWithoutInit(agent.getFilePath(), true);
                } else {
                    ConnectionSettings connectionSettings = (ConnectionSettings) agent.getConnectionSettings();
                    inputStream = MiningDBStream.createWithoutInit(connectionSettings.getUrl(), connectionSettings.getUser(),
                            connectionSettings.getPassword(), connectionSettings.getColumnNames().get(0));
                }
                AgentMiningExecutor executor = getMiningExecutorFactory().create(block, inputStream, agent);
                execs.add(executor);
            } else if (block instanceof MiningSequence) {
                execs.add(getNonAgentExecutor(block));
            } else {
                MiningExecutor executor = null;
                if (block.isDataBlock())
                    executor = getMiningExecutorFactory().create(block, agent);
                else
                    executor = getMiningExecutorFactory().create(block, agent);
                execs.add(executor);
            }
        }
        return execs;
    }

    //initialization Jade Platform and setting main container to settings
    private void initJadePlatform() {
        String host = "192.168.0.106"; // Platform IP
        int port = 1098; // default-port 1099

        String MTP_hostIP = "192.168.0.106";
        String MTP_Port = "7778";

        Runtime runtime = Runtime.instance();

        Profile profile = new ProfileImpl(host, port, null, true);
        profile.setParameter(Profile.MTPS, "jade.mtp.http.MessageTransportProtocol(http://" + MTP_hostIP + ":" + MTP_Port + "/acc)");

        // create container
        AgentContainer container = runtime.createMainContainer(profile);
        createAgentAnalyzer(container);

        this.settings.setMainContainer(container);
    }

    private void createAgentAnalyzer(AgentContainer container) {
        try {
            AgentController ac = container.createNewAgent("centralAgent", CentralAgent.class.getName(), null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    //main default executor for merge results
    private ConcurrencyMiningExecutor getNonAgentExecutor(MiningBlock block) {
        try {
            return singleThreadFactory.create(block);
        } catch (ParallelExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
