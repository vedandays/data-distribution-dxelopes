package org.eltech.ddm.distribution.central;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.eltech.ddm.distribution.common.HeadersMessage;
import org.eltech.ddm.distribution.settings.ASettings;
import org.eltech.ddm.distribution.settings.ConnectionSettings;
import org.eltech.ddm.distribution.settings.FileSettings;
import org.eltech.ddm.environment.DataDistribution;
import org.eltech.ddm.runner.RunSystem;
import org.eltech.ddm.sup.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        String fileReceiverUrl = "http://";
        String postgreSqlMatchReplyWith = "postgreSql request";
        String fileMatchReplyWith = "file request";
        int state = 0;
        HeadersMessage postgreSqlHeaders = null;
        HeadersMessage fileHeaders = null;
        String splitBySymbol = ",";
        List<ASettings> agentsArray;

        public MetaDataBehaviour(Agent agent, List<ASettings> agentsArray) {
            super(agent);

            this.agentsArray = agentsArray;

            for (ASettings aSettings : agentsArray) {
                if (aSettings instanceof FileSettings) {
                    Parser.SettingsInfo info = Parser.SettingsInfo.getInfo(splitBySymbol, aSettings.getSettingsString());
                    fileReceiverAgentName = fileReceiverAgentName
                            .concat(info.getIp())
                            .concat(":")
                            .concat(info.getTcpPort())
                            .concat("/JADE");

                    fileReceiverUrl = fileReceiverUrl
                            .concat(info.getIp())
                            .concat(":")
                            .concat(info.getHttpPort())
                            .concat("/acc");
                }
            }
        }

        public void action() {
            switch (state) {
                case 0:
                    ConnectionSettings postgresqlSettings = (ConnectionSettings) agentsArray.get(1);
                    sendSqlQuery(postgresqlSettings);
                    state = 1;
                    break;
                case 1:
                    postgreSqlHeaders = receiveSqlMessage();
                    if (Objects.nonNull(postgreSqlHeaders)) {
                        state = 4;
                    }
                    break;
                case 2:
                    FileSettings csvSettings = (FileSettings) agentsArray.get(0); //todo ved instanceof
                    sendFileQuery(csvSettings);
                    state = 3;
                    break;
                case 3:
                    fileHeaders = receiveFileMessage();
                    if (Objects.nonNull(fileHeaders)) {
                        state = 4;
                    }
                    break;
                case 4:
//                    DataDistribution analyze = analyze(postgreSqlHeaders, fileHeaders);
                    DataDistribution analyze = analyze(postgreSqlHeaders);
                    System.out.println(analyze);

                    saveResult(analyze);
                    state = 5;
                    RunSystem.atomicBoolean.set(true);// синхронизация
                    break;
            }
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
        private HeadersMessage getMessage(ACLMessage msg) {
            try {
                return (HeadersMessage) msg.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Отправляет запрос агенту, который возвращает названия столбцов для SQL БД.
         */
        private void sendSqlQuery(ConnectionSettings postgresqlSettings) {
            String sqlReceiverAgentName = "sqlDatabaseReaderAgent@192.168.0.105:1098/JADE"; //todo ved должно формироваться автоматически
            String receiverUrl = "http://192.168.0.105:7778/acc"; //todo ved должно формироваться автоматически

            AID receiverId = new AID(sqlReceiverAgentName, AID.ISGUID);
            receiverId.addAddresses(receiverUrl);

            ACLMessage msg = createSqlMessage(receiverId, postgresqlSettings);

            send(msg);
        }

        private ACLMessage createSqlMessage(AID receiverId, ConnectionSettings postgresqlSettings) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(receiverId);
            msg.setReplyWith(postgreSqlMatchReplyWith);

            try {
                msg.setContentObject(postgresqlSettings);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return msg;
        }

        //        @Nullable
        private HeadersMessage receiveSqlMessage() {
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                    MessageTemplate.MatchReplyWith(postgreSqlMatchReplyWith));

            ACLMessage msg = receive(mt);
            if (Objects.nonNull(msg)) {
                HeadersMessage headersMessage = getMessage(msg);

                if (Objects.nonNull(headersMessage)) {
                    System.out.println("Headers from " + msg.getSender().getName() + ":");
                    headersMessage.getHeaderNames().forEach(System.out::println);
                    System.out.println();
                    return headersMessage;
                }

            }
            return null;
        }

        /**
         * Отправляет запрос агенту, который возвращает названия столбцов для файлов.
         */
        private void sendFileQuery(FileSettings fileSettings) {
            AID receiverId = new AID(fileReceiverAgentName, AID.ISGUID);
            receiverId.addAddresses(fileReceiverUrl);

            ACLMessage msg = createFileMessage(receiverId, fileSettings);
            send(msg);
        }

        private ACLMessage createFileMessage(AID receiverId, FileSettings fileSettings) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(receiverId);
            msg.setReplyWith(fileMatchReplyWith);

            try {
                msg.setContentObject(fileSettings);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return msg;
        }

        //        @Nullable
        private HeadersMessage receiveFileMessage() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

            ACLMessage msg = receive(mt);
            if (Objects.nonNull(msg)) {
                HeadersMessage headersMessage = getMessage(msg);
                if (Objects.nonNull(headersMessage)) {
                    System.out.println("Headers from " + msg.getSender().getName() + ":");
                    headersMessage.getHeaderNames().forEach(System.out::println);
                    System.out.println();
                    return headersMessage;
                }
            } /*else {
                block();
                return null;
            }*/
            return null;
        }

        @Override
        public boolean done() {
            return state == 6;
        }

        public DataDistribution analyze(HeadersMessage... messages) {
            return Analyzer.analyze(messages);
        }


    }
}
