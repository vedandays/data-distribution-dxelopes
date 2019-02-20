package org.eltech.ddm.backend.actors;

import akka.actor.AbstractActor;
import lombok.NoArgsConstructor;
import org.eltech.ddm.classification.ClassificationFunctionSettings;
import org.eltech.ddm.common.ExecuteJob;
import org.eltech.ddm.common.ExecuteResult;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.inputdata.file.csv.MiningCsvStream;
import org.eltech.ddm.miningcore.MiningException;
import org.eltech.ddm.miningcore.algorithms.*;
import org.eltech.ddm.miningcore.miningdata.ELogicalAttribute;
import org.eltech.ddm.miningcore.miningdata.ELogicalData;
import org.eltech.ddm.miningcore.miningfunctionsettings.EMiningAlgorithmSettings;
import org.eltech.ddm.miningcore.miningfunctionsettings.EMiningFunctionSettings;
import org.eltech.ddm.miningcore.miningmodel.Distributable;
import org.eltech.ddm.miningcore.miningmodel.EMiningModel;
import org.omg.java.cwm.analysis.datamining.miningcore.miningfunctionsettings.MiningFunctionSettings;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.eltech.ddm.miningcore.NamedObject.algorithmSettings;

/**
 * Main Actor worker object which executes a block on the seed node side
 *
 * @author etitkov
 */
@NoArgsConstructor
public class ActorWorker extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ExecuteJob.class, (msg) -> {
                    changeMiningSettings((MiningCsvStream) msg.getInputStream(), msg.getBlock().getFunctionSettings());
                    EMiningModel model = msg.getMiningModel().getConstructor(EMiningFunctionSettings.class).newInstance(msg.getBlock().getFunctionSettings());
                    model.initModel();
                    if (model instanceof Distributable) {
                        ((Distributable) model).setDistributionType(msg.getDataDistribution());
                    }
                    MiningBlock block = reinitBlock(msg.getInputStream(), msg.getBlock());
                    model = block.run(model);
                    sender().tell(new ExecuteResult(model), self());
                })
                .build();
    }


    /**
     * Rebuilds serialized sequence by providing input stream, which initiates
     * only after deserialization. Re-init all blocks and return it back as a sequence
     *
     * @param data - input stream
     * @return - Rebuild sequence
     */
    private MiningBlock reinitBlock(MiningInputStream data, MiningBlock block) {
        if (block instanceof DataMiningBlock) {
            ((DataMiningBlock) block).setData(data);
        } else if (block instanceof MiningLoopVectors) {
            ((MiningLoopVectors) block).setStartPositon(0);
            try {
                ((MiningLoopVectors) block).setCountElement(data.getVectorsNumber());
            } catch (MiningException e) {
                e.printStackTrace();
            }
//            Optional<MiningBlock> block2 = getChildrenMiningBlock(block, new HashSet<>()).stream()
//                    .filter(block1 -> block1 instanceof MiningLoopElement)
//                    .findFirst();
        }
        return block;
    }

    /**
     * Recursively goes through all blocks and put it to the Set collection
     *
     * @param block - block to get child from
     * @param acc   - accumulator collection
     * @return - accumulated objects
     */
    private Set<MiningBlock> getChildrenMiningBlock(MiningBlock block, Set<MiningBlock> acc) {
        if (block instanceof MiningSequence) {
            ((MiningSequence) block).getSequence().forEach(b -> {
                acc.add(b);
                acc.addAll(getChildrenMiningBlock(b, acc));
            });
            return acc;
        } else if (block instanceof MiningLoop) {
            MiningLoop loop = (MiningLoop) block;
            if (loop.getIteration() != null) {
                loop.getIteration().getSequence().forEach(b -> {
                    acc.add(b);
                    acc.addAll(getChildrenMiningBlock(b, acc));
                });
            }
        }
        return acc;
    }


    private void changeMiningSettings(MiningCsvStream stream, MiningFunctionSettings settings) throws MiningException {
        stream.recognize();
        ELogicalData logicalData = stream.getLogicalData();
        if (settings instanceof ClassificationFunctionSettings) {
            ClassificationFunctionSettings cast = (ClassificationFunctionSettings) settings;
            ((ClassificationFunctionSettings) settings).setLogicalData(logicalData);
            ((ClassificationFunctionSettings) settings).setTarget(((ClassificationFunctionSettings) settings).getTarget());
            ((ClassificationFunctionSettings) settings).verify();
        }
    }
}


