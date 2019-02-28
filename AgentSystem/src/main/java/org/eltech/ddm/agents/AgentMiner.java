package org.eltech.ddm.agents;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.eltech.ddm.classification.ClassificationFunctionSettings;
import org.eltech.ddm.common.ExecuteJob;
import org.eltech.ddm.common.ExecuteResult;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.inputdata.file.csv.MiningCsvStream;
import org.eltech.ddm.miningcore.MiningException;
import org.eltech.ddm.miningcore.algorithms.*;
import org.eltech.ddm.miningcore.miningdata.ELogicalData;
import org.eltech.ddm.miningcore.miningfunctionsettings.EMiningFunctionSettings;
import org.eltech.ddm.miningcore.miningmodel.Distributable;
import org.eltech.ddm.miningcore.miningmodel.EMiningModel;
import org.omg.java.cwm.analysis.datamining.miningcore.miningfunctionsettings.MiningFunctionSettings;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class AgentMiner extends Agent {

    private MiningInputStream data = null;

    private ExecuteResult executeResult;
    private ExecuteJob executeJob;

    private ACLMessage answ;

    public void setup(){
        Object[] args = getArguments();

        System.out.println(args.length);


        System.out.println("Miner here");

        addBehaviour(new ReceiveModel());

    }

    class ReceiveModel extends SimpleBehaviour{

        private boolean finish = false;

        public  void action(){

            //TODO: Template

            ACLMessage msg = myAgent.receive();

            if(msg != null){
                answ = msg.createReply();
                try {
                        executeJob = (ExecuteJob) msg.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                finish = true;

                addBehaviour(new Execute());

            } else { block(); }

        }
        public  boolean done(){
            return finish;
        }
    }

    class Execute extends OneShotBehaviour{

        public void action(){

            try {
                changeMiningSettings((MiningCsvStream) executeJob.getInputStream(), executeJob.getBlock().getFunctionSettings());
            } catch (MiningException e) {
                e.printStackTrace();
            }
            EMiningModel model = null;
            try {
                model = executeJob.getMiningModel().getConstructor(EMiningFunctionSettings.class).
                        newInstance(executeJob.getBlock().getFunctionSettings());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                model.initModel();
            } catch (MiningException e) {
                e.printStackTrace();
            }
            if (model instanceof Distributable) {
                ((Distributable) model).setDistributionType(executeJob.getDataDistribution());
            }
            MiningBlock block = reinitBlock(executeJob.getInputStream(), executeJob.getBlock());
            try {
                model = block.run(model);
            } catch (MiningException e) {
                e.printStackTrace();
            }

            executeResult = new ExecuteResult(model);

            addBehaviour(new SendResult());

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

    class SendResult extends OneShotBehaviour{

        public void action(){
            try {
                answ.setContentObject(executeResult);
            } catch (IOException e) {
                e.printStackTrace();
            }

            send(answ);
        }
    }
}
