package org.eltech.ddm.distribution.central;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.eltech.ddm.distribution.common.MetaDataMessage;
import org.eltech.ddm.distribution.settings.ASettings;
import org.eltech.ddm.distribution.settings.ConnectionSettings;
import org.eltech.ddm.distribution.settings.FileSettings;
import org.eltech.ddm.environment.DataDistribution;
import org.eltech.ddm.runner.RunSystem;
import org.eltech.ddm.sup.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CentralAgent extends Agent {
    public void setup() {
        List<ASettings> agentsArray = RunSystem.AGENTS_ARRAY;
        System.out.println("Agent " + this.getAID().getLocalName() + " is started.");
        addBehaviour(new MetaDataBehaviour(this, agentsArray));
    }

    class MetaDataBehaviour extends SimpleBehaviour {
        String fileReceiverAgentName = "fileHeaderReaderAgent@";
        String sqlReceiverAgentName = "sqlDatabaseReaderAgent@";
        String splitBySymbol = ",";
        List<ASettings> agentsArray;
        boolean finished = false;

        public MetaDataBehaviour(Agent agent, List<ASettings> agentsArray) {
            super(agent);
            this.agentsArray = agentsArray;
        }

        public void action() {
            List<MetaDataMessage> headers = new ArrayList<>();
            for (ASettings aSettings : agentsArray) {
                if (aSettings instanceof ConnectionSettings) {
                    String receiverAgentName = createReceiverAgentName(aSettings, sqlReceiverAgentName);
                    String receiverUrl = createReceiverUrl(aSettings);
                    sendSqlQuery((ConnectionSettings) aSettings, receiverAgentName, receiverUrl);
                    ACLMessage msg = blockingReceive();
                    addHeader(headers, msg);
                } else {
                    String receiverAgentName = createReceiverAgentName(aSettings, fileReceiverAgentName);
                    String receiverUrl = createReceiverUrl(aSettings);
                    sendFileQuery((FileSettings) aSettings, receiverAgentName, receiverUrl);
                    ACLMessage msg = blockingReceive();
                    addHeader(headers, msg);
                }
                System.out.println("111");
            }

            DataDistribution analyze = analyze(headers.toArray(new MetaDataMessage[headers.size()]));
            saveResult(analyze);
            finished = true;
            RunSystem.atomicBoolean.set(true);// синхронизация
        }

        private String createReceiverUrl(ASettings aSettings) {
            Parser.SettingsInfo info = Parser.SettingsInfo.getInfo(splitBySymbol, aSettings.getSettingsString());
            return "http://"
                    .concat(info.getIp())
                    .concat(":")
                    .concat(info.getHttpPort())
                    .concat("/acc");
        }

        private String createReceiverAgentName(ASettings aSettings, String agentName) {
            Parser.SettingsInfo info = Parser.SettingsInfo.getInfo(splitBySymbol, aSettings.getSettingsString());
            return agentName
                    .concat(info.getIp())
                    .concat(":")
                    .concat(info.getTcpPort())
                    .concat("/JADE");
        }

        private void saveResult(DataDistribution analyze) {
            try {
                File myObj = new File("E:\\Programming\\bayes-dxdevelops-agents-impl\\AgentSystem\\src\\main\\java\\org\\eltech\\ddm\\distribution\\res\\res.txt"); //todo перенести в ресурсы
                FileWriter myWriter = new FileWriter(myObj);
                myWriter.write(analyze.toString());
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //        @Nullable
        private MetaDataMessage getMessage(ACLMessage msg) {
            try {
                return (MetaDataMessage) msg.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Отправляет запрос агенту, который возвращает названия столбцов для SQL БД.
         */
        private void sendSqlQuery(ConnectionSettings postgresqlSettings, String agentName, String url) {
            AID receiverId = new AID(agentName, AID.ISGUID);
            receiverId.addAddresses(url);

            ACLMessage msg = createSqlMessage(receiverId, postgresqlSettings);
            send(msg);
        }

        private ACLMessage createSqlMessage(AID receiverId, ConnectionSettings postgresqlSettings) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(receiverId);

            try {
                msg.setContentObject(postgresqlSettings);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return msg;
        }

        /**
         * Отправляет запрос агенту, который возвращает названия столбцов для файлов.
         */
        private void sendFileQuery(FileSettings fileSettings, String agentName, String url) {
            AID receiverId = new AID(agentName, AID.ISGUID);
            receiverId.addAddresses(url);

            ACLMessage msg = createFileMessage(receiverId, fileSettings);
            send(msg);
        }

        private ACLMessage createFileMessage(AID receiverId, FileSettings fileSettings) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(receiverId);

            try {
                msg.setContentObject(fileSettings);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return msg;
        }

        private MetaDataMessage receiveMessage(String replyWith) {
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                    MessageTemplate.MatchReplyWith(replyWith));

            ACLMessage msg = receive(mt);
            if (Objects.nonNull(msg)) {
                MetaDataMessage metaDataMessage = getMessage(msg);
                if (Objects.nonNull(metaDataMessage)) {
                    System.out.println("Headers from " + msg.getSender().getName() + ":");
                    metaDataMessage.getHeaderNames().forEach(System.out::println);
                    System.out.println();
                    return metaDataMessage;
                }
            }
            return null;
        }

        @Override
        public boolean done() {
            return finished;
        }

        public DataDistribution analyze(MetaDataMessage... messages) {
            return Analyzer.analyze(messages);
        }
    }

    private void addHeader(List<MetaDataMessage> headers, ACLMessage msg) {
        try {
            MetaDataMessage header = (MetaDataMessage) msg.getContentObject();
            headers.add(header);
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }
}
