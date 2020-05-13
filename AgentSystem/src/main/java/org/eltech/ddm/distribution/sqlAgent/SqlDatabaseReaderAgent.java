package org.eltech.ddm.distribution.sqlAgent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.eltech.ddm.distribution.common.HeadersMessage;
import org.eltech.ddm.distribution.common.IHeadersReader;
import org.eltech.ddm.distribution.settings.ConnectionSettings;

import java.io.IOException;
import java.util.Objects;

public class SqlDatabaseReaderAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("Agent " + this.getAID().getLocalName() + " is started");
        addBehaviour(new MyBehaviour(this));
    }

    class MyBehaviour extends CyclicBehaviour {

        public MyBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            receiveMessage();
        }

        private void receiveMessage() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = receive(mt);

            if (Objects.nonNull(msg)) {
                try {
                    ConnectionSettings connectionSettings = (ConnectionSettings) msg.getContentObject();
                    IHeadersReader reader = new SqlHeadersReader(connectionSettings);
                    HeadersMessage headersMessage = reader.readHeaders();
                    System.out.println(myAgent.getName() + ": headers:");
                    headersMessage.getHeaderNames().forEach(System.out::println);
                    reply(headersMessage, msg);
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
            }
        }

        private void reply(HeadersMessage content, ACLMessage msg) {
            AID receiverId = new AID(msg.getSender().getName(), AID.ISGUID);
            receiverId.addAddresses(msg.getSender().getAddressesArray()[0]);

            ACLMessage reply = new ACLMessage(ACLMessage.REQUEST);
            reply.addReceiver(receiverId);
            reply.setReplyWith(msg.getReplyWith());
            setContent(reply, content);

            send(reply);
        }

        private void setContent(ACLMessage msg, HeadersMessage content) {
            try {
                msg.setContentObject(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
