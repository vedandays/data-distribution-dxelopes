package org.eltech.ddm.frotend.environment;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;
import org.eltech.ddm.backend.actors.ActorWorker;
import org.eltech.ddm.environment.ExecutionEnvironment;
import org.eltech.ddm.frotend.executors.ActorExecutorFactory;
import org.eltech.ddm.frotend.executors.ActorMiningExecutor;
import org.eltech.ddm.frotend.executors.ActorMiningExecutorAggregator;
import org.eltech.ddm.frotend.actors.ActorRouterClient;
import org.eltech.ddm.handlers.ParallelExecutionException;
import org.eltech.ddm.handlers.thread.ConcurrencyExecutorFactory;
import org.eltech.ddm.handlers.thread.ConcurrencyMiningExecutor;
import org.eltech.ddm.inputdata.file.common.CloneableStream;
import org.eltech.ddm.inputdata.file.csv.MiningCsvStream;
import org.eltech.ddm.miningcore.MiningErrorCode;
import org.eltech.ddm.miningcore.MiningException;
import org.eltech.ddm.miningcore.algorithms.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Actor execution environment provides environment for performing algorithms
 * in actor-like way. Initiate actor cluster system on the Test-Side machine
 * and a client {@link ActorRouterClient} in order to be able to connect
 * available nodes with {@link ActorWorker}
 *
 * @author etitkov
 */
public class ActorsExecutionEnvironment extends ExecutionEnvironment<ActorMiningExecutor, ActorExecutorFactory> {

    private static final String CLUSTER_NAME = "MiningSystem";
    private static final String CONFIG_PATH = "worker";
    private static final String ROUTER_CLIENT_NAME = "routerClient";

    private ActorsExecutionEnvironmentSettings settings;

    private ConcurrencyExecutorFactory singleThreadFactory;

    /**
     * Constructor for initiating env
     *
     * @param settings - env settings
     */
    public ActorsExecutionEnvironment(ActorsExecutionEnvironmentSettings settings) {
        this.settings = settings;
        initEnvironment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MiningExecutor createExecutorTree(MiningSequence sequence) throws MiningException {
        List<MiningExecutor> executors = createExecutors(sequence);
        MiningExecutor handler = executors.get(0);
        fullExecutor(handler.getBlock(), handler);
        return handler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initEnvironment() {
        ActorSystem system = ActorSystem.create(CLUSTER_NAME, ConfigFactory.load(CONFIG_PATH));
        ActorRef routerClient = system.actorOf(Props.create(ActorRouterClient.class), ROUTER_CLIENT_NAME);
        miningExecutorFactory = new ActorExecutorFactory(system, routerClient, settings.getDataDistribution());
        singleThreadFactory = new ConcurrencyExecutorFactory(1);
    }

    /**
     * {@inheritDoc}
     */
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

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    protected List<ActorMiningExecutor> createExecutors(MiningBlock block) {
//        List<ActorMiningExecutor> execs = new ArrayList<>();
//        for (String file : settings.getFileList()) {
//            ActorMiningExecutor executor = getMiningExecutorFactory().create(block, MiningCsvStream.createWithoutInit(file, false));
//            execs.add(executor);
//        }
//        return execs;
//    }


    @Override
    protected List<MiningExecutor> createExecutors(MiningBlock block) throws MiningException {
        List<MiningExecutor> execs = new ArrayList<>();
        if (block instanceof MiningLoopVectors) {
            for (String file : settings.getFileList()) {
                if (block instanceof MiningLoopVectors) {
                    ActorMiningExecutor executor = getMiningExecutorFactory().create(block, MiningCsvStream.createWithoutInit(file, false));
                    execs.add(executor);
                }
            }
        } else if (block instanceof MiningSequence) {
            execs.add(getNonActorExecutor(block));
        } else {
            MiningExecutor executor = null;
            if (block.isDataBlock())
                executor = getMiningExecutorFactory().create(block);
            else
                executor = getMiningExecutorFactory().create(block);
            execs.add(executor);
        }
        return execs;
    }

//    @Override
//    protected List<ActorMiningExecutor> createExecutors(MiningBlock block) throws MiningException {
//        List<ActorMiningExecutor> execs = new ArrayList<>();
//        for (String file : settings.getFileList()) {
//            if (block instanceof MiningLoopVectors) {
//                ActorMiningExecutor executor = getMiningExecutorFactory().create(block, MiningCsvStream.createWithoutInit(file, false));
//                execs.add(executor);
//
//            } else if () {
//
//            } else {
//                ActorMiningExecutor executor = null;
//                if (block.isDataBlock())
//                    executor = getMiningExecutorFactory().create(block, MiningCsvStream.createWithoutInit(file, false));
//                else
//                    executor = getMiningExecutorFactory().create(block);
//                execs.add(executor);
//            }
//        }
//        return execs;
//    }

    private ConcurrencyMiningExecutor getNonActorExecutor(MiningBlock block) {
        try {
            return singleThreadFactory.create(block);
        } catch (ParallelExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}

