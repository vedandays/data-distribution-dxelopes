package org.eltech.ddm.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import org.eltech.ddm.common.ExecuteJob;
import org.eltech.ddm.handlers.AgentMiningExecutor;

import java.io.IOException;

public class AgentModerator extends Agent {

    private Object[] args;
    private AgentInfo agent;
    private ExecuteJob executeJob;
    private Object receivedMessage = null;
    private AgentMiningExecutor thisExecutor;

    public void setup() {

        System.out.println("\nAgent " + this.getAID().getLocalName() + " is started.\n");

        args = getArguments();

        agent = (AgentInfo) args[0];
        thisExecutor = (AgentMiningExecutor) args[1];
        executeJob = (ExecuteJob) args[2];


        //custom synchronize
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        addBehaviour(new SendingMsg());

    }

    class SendingMsg extends OneShotBehaviour{

        public void action() {

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            AID receiverAID = new AID(agent.getName() +"@"+ agent.getIp() + ":" + agent.getTcpPort() + "/JADE");
            receiverAID.addAddresses("http://" + agent.getHost() + ":" + agent.getHttpPort() +  "/acc");
            msg.addReceiver(receiverAID);
            msg.setContent(agent.getFilePath());

            try {
                msg.setContentObject(executeJob);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try
            {
                addBehaviour(new AchieveREInitiator(this.myAgent, msg) {

                    protected void handleAgree(ACLMessage agree){
                        System.out.println("\nMessage delivered");
                    }

                    protected void handleInform(ACLMessage inform) {
                        System.out.println("\nReceived a INFORM message from " + inform.getSender().getLocalName() + "\n");
                        setIntoExecutor(inform);
                    }

                    protected void handleFailure(ACLMessage failure) {
                        System.out.println("\nMessage not delivered");
                    }

                    protected void handleOutOfSequence(ACLMessage msg) {
                        System.out.println("\nReceived a error message from " + msg.getSender().getLocalName() + "\n");
                        setIntoExecutor(msg);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void setIntoExecutor(ACLMessage msg){

            try{
                receivedMessage = msg.getContentObject();
            } catch (UnreadableException e){ e.printStackTrace();}

            thisExecutor.setReceivedMessage(receivedMessage);
        }
    }

}
