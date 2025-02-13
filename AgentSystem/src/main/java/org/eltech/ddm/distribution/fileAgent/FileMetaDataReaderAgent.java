package org.eltech.ddm.distribution.fileAgent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.eltech.ddm.distribution.common.MetaDataMessage;
import org.eltech.ddm.distribution.common.IMetaDataReader;
import org.eltech.ddm.distribution.settings.FileSettings;

import java.io.IOException;
import java.util.Objects;

public class FileMetaDataReaderAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("Agent " + this.getAID().getLocalName() + " is started");
        addBehaviour(new MyBehaviour(this));
    }

    class MyBehaviour extends CyclicBehaviour {
        String receiverAgentName = "centralAgent@192.168.0.104:1098/JADE";
        String receiverUrl = "http://192.168.0.104:7778/acc";

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

            FileSettings fileSettings;
            if (Objects.nonNull(msg)) {
                try {
                    fileSettings = (FileSettings) msg.getContentObject();
                } catch (UnreadableException e) {
                    throw new RuntimeException(e);
                }

                IMetaDataReader reader = null;
                switch (fileSettings.getFileFormat()) {
                    case CSV:
                        reader = new CsvMetaDataReader(fileSettings.getFilePath());
                        break;
                    case CSVH:
                        reader = new CsvMetaDataReader(fileSettings.getFilePath());
                        break;
                    case JSON:
                        // todo reader for json
                        break;
                }

                if (Objects.nonNull(reader)) {
                    MetaDataMessage metaDataMessage = reader.readHeaders();
                    reply(metaDataMessage, msg);
                }
            }
        }

        private void reply(MetaDataMessage content, ACLMessage msg) { // todo ved вынести в общий класс
            ACLMessage reply = new ACLMessage(ACLMessage.REQUEST);

            AID receiverId = new AID(msg.getSender().getName(), AID.ISGUID);
            receiverId.addAddresses(msg.getSender().getAddressesArray()[0]);

            reply.addReceiver(receiverId);
            reply.setReplyWith(msg.getReplyWith());
            setContent(reply, content);
//            System.out.println("reply to " + msg.getSender().getName() + " " + );

            send(reply);
        }

        private void setContent(ACLMessage msg, MetaDataMessage content) { // todo ved вынести в общий класс
            try {
                msg.setContentObject(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
