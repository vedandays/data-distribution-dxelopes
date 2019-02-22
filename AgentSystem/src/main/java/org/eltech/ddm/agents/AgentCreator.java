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


public class AgentCreator extends Agent {

    private AgentInfo newAgent;
    private Object[] args;

    public void setup() {
        args = getArguments();
        newAgent = (AgentInfo) args[0];

        addBehaviour(new Creation());

    }

    class Creation extends OneShotBehaviour {

        public void action() {
            SLCodec codec = new SLCodec();
            ContentManager cm = new ContentManager();

            cm.registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
            cm.registerOntology(FIPAManagementOntology.getInstance());
            cm.registerOntology(JADEManagementOntology.getInstance());

            CreateAgent ca = new CreateAgent();
            ca.setAgentName(newAgent.getName());
            ca.setClassName(newAgent.getClassName()); //full path to agentclass
            ca.addArguments(args);
            //ca.setContainer((ContainerID) here()); //Main-Container@192.168.31.192 - example

            ContainerID id = new ContainerID();
            id.setName("Main-Container"); //always Main-Container
            id.setAddress(newAgent.getHost());
            ca.setContainer(id); //Main-Container@192.168.31.192 - example

            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            AID r = new AID("ams@" + newAgent.getHost() + ":" + newAgent.getTcpPort() + "/JADE");
            r.addAddresses("http://" + newAgent.getHost() + ":" + newAgent.getHttpPort() +  "/acc");

            request.addReceiver(r);
            request.setOntology(JADEManagementOntology.getInstance().getName());
            request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);

            try {
                cm.fillContent(request, new Action(getAMS(), ca));
                //send(request);
                addBehaviour(new AchieveREInitiator(this.myAgent, request) {
                    protected void handleInform(ACLMessage inform) {
                        System.out.println("Agent successfully created");
                    }

                    protected void handleFailure(ACLMessage failure) {
                        System.out.println("Error creating agent.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            myAgent.doDelete(); //kill

        }
    }
}
