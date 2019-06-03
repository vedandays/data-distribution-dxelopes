package org.eltech.ddm.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import org.eltech.ddm.common.ExecuteJob;
import org.eltech.ddm.common.JobFailed;
import org.eltech.ddm.handlers.AgentMiningExecutor;

import java.io.IOException;
/**
 * Agent for task transfer between adapter and platform
 *
 * @author Derkach Petr
 */
public class AgentModerator extends Agent {

    private Object[] args;
    private AgentInfo agent;
    private ExecuteJob executeJob;
    private Object receivedMessage = null;
    private AgentMiningExecutor thisExecutor;

    public void setup() {
        System.out.println(this.getAID().getLocalName() + " is started.");

        args = getArguments();

        agent = (AgentInfo) args[0];
        thisExecutor = (AgentMiningExecutor) args[1];
        executeJob = (ExecuteJob) args[2];


        while (!thisExecutor.isExist()); //synchronization
        if(thisExecutor.getStateExist().isCreated()) addBehaviour(new SendingMsg());
        else {
            thisExecutor.setReceivedMessage(new JobFailed(
                    new Exception(agent.getName() +" is not created. Moderator deleted")));
            doDelete();
        }
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
                        System.out.println("\nMessage delivered to " + agent.getName() +"@"+ agent.getIp());
                    }

                    protected void handleInform(ACLMessage inform) {
                        System.out.println("\nReceived a INFORM message from "+inform.getSender().getName()+"\n");
                        setIntoExecutor(inform);
                    }

                    protected void handleFailure(ACLMessage failure) {
                        System.out.println("\nMessage not delivered to " + agent.getName() +"@"+ agent.getIp());
                        thisExecutor.setReceivedMessage(new JobFailed(
                                new Exception("Message not delivered to " + agent.getName() +"@"+ agent.getIp())));
                        doDelete();
                    }

                    protected void handleOutOfSequence(ACLMessage msg) {
                        System.out.println("\nReceived a error message from " + agent.getName() +"@"+ agent.getIp());
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
            doDelete();
        }
    }
}
