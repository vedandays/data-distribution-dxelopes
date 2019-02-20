package org.eltech.ddm.frotend.executors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eltech.ddm.environment.DataDistribution;
import org.eltech.ddm.frotend.environment.ActorsExecutionEnvironment;
import org.eltech.ddm.frotend.executors.ActorMiningExecutor;
import org.eltech.ddm.handlers.MiningExecutorFactory;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.miningcore.algorithms.MiningBlock;

/**
 * Factory Object for generating MiningExecutor for actor environment
 *
 * @see ActorsExecutionEnvironment
 * @see org.eltech.ddm.miningcore.algorithms.MiningExecutor
 */
@RequiredArgsConstructor
public class ActorExecutorFactory extends MiningExecutorFactory<ActorMiningExecutor> {

    @NonNull
    private final ActorSystem clusterRef;
    @NonNull
    private final ActorRef clientRef;
    @NonNull
    private DataDistribution dist;

    /**
     * {@inheritDoc}
     */
    @Override
    public ActorMiningExecutor create(MiningBlock block) {
        return new ActorMiningExecutor(block, null, clusterRef, clientRef,dist);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActorMiningExecutor create(MiningBlock block, MiningInputStream data) {
        ActorMiningExecutor executor = new ActorMiningExecutor(block, data, clusterRef, clientRef,dist);
        executor.setData(data);
        return executor;
    }

}
