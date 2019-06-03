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

/**
 * Agent who creates agents executors on target node
 *
 * @author Derkach Petr
 */
public class AgentCreator extends Agent {

    private AgentInfo newAgent; // information about the agent you want to create
    private Object[] args;
    private AgentMiningExecutor executor; // reference to the executor object for setting the state

    public void setup() {
        System.out.println(this.getAID().getLocalName() + " is started.");

        args = getArguments();
        newAgent = (AgentInfo) args[0];
        executor = (AgentMiningExecutor) args[1];

        addBehaviour(new Create()); //create and start behaviour
    }

    class Create extends OneShotBehaviour {
        public void action(){

            SLCodec codec = new SLCodec();
            ContentManager cm = new ContentManager();

            //setting base ontologies and languages
            cm.registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
            cm.registerOntology(FIPAManagementOntology.getInstance());
            cm.registerOntology(JADEManagementOntology.getInstance());

            //setting name and class of new agent
            CreateAgent ca = new CreateAgent();
            ca.setAgentName(newAgent.getName());
            ca.setClassName(newAgent.getClassName());

            //setting the name of the container and its address where the agent will be created
            ContainerID id = new ContainerID();
            id.setName("Main-Container"); //always main container
            id.setAddress(newAgent.getIp());
            ca.setContainer(id); //Main-Container@192.168.31.192 - example

            //creating message to base agent AMS for creating agent
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            AID r = new AID("ams@" + newAgent.getIp() + ":" + newAgent.getTcpPort() + "/JADE");
            r.addAddresses("http://" + newAgent.getHost() + ":" + newAgent.getHttpPort() +  "/acc");

            //setting same ontologies and languages
            request.addReceiver(r);
            request.setOntology(JADEManagementOntology.getInstance().getName());
            request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);

            //sending message to AMS for creating agent, receiving result and setting state of create
            try
            {
                cm.fillContent(request, new Action(getAMS(), ca));
                addBehaviour(new AchieveREInitiator(this.myAgent, request) {
                    protected void handleInform(ACLMessage inform) {
                        System.out.println("Agent " + newAgent.getName() +" successfully created");
                        executor.setStateExist(new StateExist(true)); //create state
                        doDelete();
                    }

                    protected void handleFailure(ACLMessage failure) {
                        System.out.println("Error creating agent " + newAgent.getName());
                        executor.setStateExist(new StateExist(false)); //creates state
                        doDelete();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
