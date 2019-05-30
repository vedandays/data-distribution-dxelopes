package org.eltech.ddm.agents;

import jade.content.ContentManager;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import org.eltech.ddm.common.StateExist;
import org.eltech.ddm.handlers.AgentMiningExecutor;


public class AgentCreator extends Agent {

    private AgentInfo newAgent;
    private Object[] args;
    private AgentMiningExecutor executor;

    public void setup() {
        System.out.println("\nAgent " + this.getAID().getLocalName() + " is started.");

        args = getArguments();
        newAgent = (AgentInfo) args[0];
        executor = (AgentMiningExecutor) args[1];

        //SendMessages a = new SendMessages();
        Check a = new Check();
        addBehaviour(a);
    }


    class Check extends OneShotBehaviour {
        public void action(){

            SLCodec codec = new SLCodec();
            ContentManager cm = new ContentManager();

            cm.registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
            cm.registerOntology(FIPAManagementOntology.getInstance());
            cm.registerOntology(JADEManagementOntology.getInstance());

            CreateAgent ca = new CreateAgent();
            ca.setAgentName(newAgent.getName());
            ca.setClassName(newAgent.getClassName()); //full path to agentclass
            //ca.setClassName("jade.tools.DummyAgent.DummyAgent");
            //ca.addArguments(args);
            //ca.setContainer((ContainerID) here()); //Main-Container@192.168.31.192 - example

            ContainerID id = new ContainerID();
            id.setName("Main-Container"); //always Main-Container
            id.setAddress(newAgent.getIp());
            ca.setContainer(id); //Main-Container@192.168.31.192 - example

            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            AID r = new AID("ams@" + newAgent.getIp() + ":" + newAgent.getTcpPort() + "/JADE");
            r.addAddresses("http://" + newAgent.getHost() + ":" + newAgent.getHttpPort() +  "/acc");

            request.addReceiver(r);
            request.setOntology(JADEManagementOntology.getInstance().getName());
            request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);


            try
            {
                cm.fillContent(request, new Action(getAMS(), ca));
                //send(request);
                addBehaviour(new AchieveREInitiator(this.myAgent, request) {
                    protected void handleInform(ACLMessage inform) {
                        System.out.println("Agent " + newAgent.getName() +" successfully created");
                        executor.setStateExist(new StateExist(true));
                    }

                    protected void handleFailure(ACLMessage failure) {
                        System.out.println("Error creating agent " + newAgent.getName());
                        executor.setStateExist(new StateExist(false));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

}
