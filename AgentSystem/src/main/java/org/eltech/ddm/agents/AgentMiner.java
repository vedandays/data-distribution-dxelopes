package org.eltech.ddm.agents;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;
import org.eltech.ddm.classification.ClassificationFunctionSettings;
import org.eltech.ddm.common.ExecuteJob;
import org.eltech.ddm.common.ExecuteResult;
import org.eltech.ddm.common.JobFailed;
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
import java.util.Set;

public class AgentMiner extends Agent {

    private ExecuteResult executeResult;
    private ExecuteJob executeJob;
    private JobFailed jobFailed;

    private ACLMessage answ;
    //private ACLMessage req;

    public void setup(){


        System.out.println("Agent " + this.getAID().getLocalName() + " is started.\n");

        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        addBehaviour(new AchieveREResponder(this,mt){

            @Override
            protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
                System.out.println("Agent " + getLocalName() + " received from " + request.getSender().getName() );

                answ = request.createReply();

                try {
                    executeJob = (ExecuteJob) request.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }


                addBehaviour(new Execute());

                ACLMessage agree = request.createReply();
                agree.setPerformative(ACLMessage.AGREE);
                return agree;
            }

            @Override
            protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
                return null;
            }
        });


    }

    class Execute extends OneShotBehaviour{

        private boolean finish = false;

        public void action(){

            System.out.println("Executing....");

            try {
                changeMiningSettings((MiningCsvStream) executeJob.getInputStream(), executeJob.getBlock().getFunctionSettings());
            } catch (MiningException e) { catchException(e); return; }

            EMiningModel model = null;
            try {
                model = executeJob.getMiningModel().getConstructor(EMiningFunctionSettings.class).
                        newInstance(executeJob.getBlock().getFunctionSettings());
            } catch (Exception e) { catchException(e); return; }

            try {
                model.initModel();
            } catch (MiningException e) { catchException(e); return; }

            if (model instanceof Distributable) {
                ((Distributable) model).setDistributionType(executeJob.getDataDistribution());
            }

            MiningBlock block;
            try {
                block = reinitBlock(executeJob.getInputStream(), executeJob.getBlock());
                model = block.run(model);
            } catch (MiningException e) { catchException(e); return; }

            executeResult = new ExecuteResult(model);
            //System.out.println(model);
            sendResult(executeResult, ACLMessage.INFORM);

            //addBehaviour(new SendResult());


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
                ((ClassificationFunctionSettings) settings).setLogicalData(logicalData);
                ((ClassificationFunctionSettings) settings).setTarget(((ClassificationFunctionSettings) settings).getTarget());
                ((ClassificationFunctionSettings) settings).verify();
            }
        }


        private void catchException(Exception e){
            System.out.println("fail");
            e.printStackTrace();
            //addBehaviour(new SendFailResult());
            jobFailed = new JobFailed(e);
            sendResult(jobFailed, ACLMessage.REFUSE);
        }

        private void sendResult(JobFailed jf, int performative) {

            answ.setPerformative(performative);

            try {
                answ.setContentObject(jf);
            } catch (IOException e) {
                e.printStackTrace();
            }

            send(answ);

            System.out.println(myAgent.getAID().getLocalName() + " sent a fail message.");
        }

        private void sendResult(ExecuteResult er, int performative) {

            answ.setPerformative(performative);

            try {
                answ.setContentObject(er);
                System.out.println("MINER: "+er.getModel());
            } catch (IOException e) {
                e.printStackTrace();
            }

            send(answ);

            System.out.println(myAgent.getAID().getLocalName() + " sent a message.");
        }


    }
}
