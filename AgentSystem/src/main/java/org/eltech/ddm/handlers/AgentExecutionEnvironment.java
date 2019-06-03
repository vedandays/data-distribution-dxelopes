package org.eltech.ddm.handlers;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import org.eltech.ddm.agents.AgentInfo;
import org.eltech.ddm.environment.ExecutionEnvironment;
import org.eltech.ddm.handlers.thread.ConcurrencyExecutorFactory;
import org.eltech.ddm.handlers.thread.ConcurrencyMiningExecutor;
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
                AgentMiningExecutor executor = getMiningExecutorFactory().create(block,
                        MiningCsvStream.createWithoutInit(agent.getFilePath(), true), agent);
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
        Runtime rt = Runtime.instance();
        rt.setCloseVM(true);
        Profile mp = new ProfileImpl();
        mp.setParameter(ProfileImpl.MAIN_HOST, "localhost");

        this.settings.setMainContainer(rt.createMainContainer(mp));

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
