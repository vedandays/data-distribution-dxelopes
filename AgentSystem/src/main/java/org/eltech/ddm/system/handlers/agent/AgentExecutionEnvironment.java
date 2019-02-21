package org.eltech.ddm.system.handlers.agent;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import org.eltech.ddm.environment.ExecutionEnvironment;
import org.eltech.ddm.handlers.ParallelExecutionException;
import org.eltech.ddm.inputdata.file.csv.MiningCsvStream;
import org.eltech.ddm.miningcore.MiningErrorCode;
import org.eltech.ddm.miningcore.MiningException;
import org.eltech.ddm.miningcore.algorithms.*;

import java.util.ArrayList;
import java.util.List;

public class AgentExecutionEnvironment  extends ExecutionEnvironment {

    private AgentExecutionEnvironmentSettings settings;
    private AgentContainer mainContainer;



    public AgentExecutionEnvironment(AgentExecutionEnvironmentSettings settings) throws ParallelExecutionException {
        this.settings = settings;
        initEnvironment();
    }

    @Override
    protected void initEnvironment() throws ParallelExecutionException {
        initJadePlatform();
        /* создание фабрики*/

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
//        if (block instanceof MiningLoopVectors) {
//            for (String file : settings.getFileList()) {
//                if (block instanceof MiningLoopVectors) {
//                    //????????????????????????????????? CAST?
//                    AgentMiningExecutor executor = (AgentMiningExecutor) getMiningExecutorFactory().create(block, MiningCsvStream.createWithoutInit(file, false));
//                    execs.add(executor);
//                }
//            }
//        } else if (block instanceof MiningSequence) {
//            //execs.add(getNonActorExecutor(block));
//        } else {
//            MiningExecutor executor = null;
//            if (block.isDataBlock())
//                executor = getMiningExecutorFactory().create(block);
//            else
//                executor = getMiningExecutorFactory().create(block);
//            execs.add(executor);
//        }
        return execs;
    }

    private void initJadePlatform(){
        Runtime rt = Runtime.instance();
        Profile mp = new ProfileImpl();
        mp.setParameter("profile.LOCAL_PORT","1099");
        //mp.setParameter("profile.DETECT_MAIN","False");
        //mp.setParameter("profile.CONTAINER_NAME","MAIN_CONT");
        this.settings.setMainContainer(rt.createMainContainer(mp));

    }
}
