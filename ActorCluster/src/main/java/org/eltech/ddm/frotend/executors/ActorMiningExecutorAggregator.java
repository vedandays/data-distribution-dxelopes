package org.eltech.ddm.frotend.executors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eltech.ddm.miningcore.MiningException;
import org.eltech.ddm.miningcore.algorithms.MiningExecutor;
import org.eltech.ddm.miningcore.miningmodel.Distributable;
import org.eltech.ddm.miningcore.miningmodel.EMiningModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Actor executor aggregator - aggregates all mining executors.
 *
 * @author etitkov
 */
@RequiredArgsConstructor
public class ActorMiningExecutorAggregator extends MiningExecutor {
    @NonNull
    private List<ActorMiningExecutor> executors;

    private EMiningModel modelToJoin;

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(EMiningModel model) {
        this.modelToJoin = model;
        executors.forEach(miningExecutor -> miningExecutor.start(model));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EMiningModel getModel() {
        try {
            //this is a dirty hack
            long startTime = System.currentTimeMillis();
            List<EMiningModel> collect = executors.stream().map(ActorMiningExecutor::getModel).collect(Collectors.toList());
            if (modelToJoin instanceof Distributable) {
                if (modelToJoin instanceof Distributable) {
                    ((Distributable) modelToJoin).setDistributionType((((Distributable) collect.get(0)).getDistributionType()));
                }
            }
            modelToJoin.join(collect);
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Time Elapsed: " + elapsedTime);
            return modelToJoin;
        } catch (MiningException e) {
            return null;
        }


    }

}
