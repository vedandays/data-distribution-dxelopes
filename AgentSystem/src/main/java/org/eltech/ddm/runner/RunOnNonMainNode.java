package org.eltech.ddm.runner;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.eltech.ddm.distribution.fileAgent.FileHeaderReaderAgent;
import org.eltech.ddm.distribution.sqlAgent.SqlDatabaseReaderAgent;

public class RunOnNonMainNode {
    public static void main(String[] args) {
        String host = "192.168.0.105"; // Platform IP
        int port = 1098; // default-port 1099

        String MTP_hostIP = "192.168.0.105";
        String MTP_Port = "7778";

        Runtime runtime = Runtime.instance();

        Profile profile = new ProfileImpl(host, port, null, true);
        profile.setParameter(Profile.MTPS, "jade.mtp.http.MessageTransportProtocol(http://" + MTP_hostIP + ":" + MTP_Port + "/acc)");
//        profile.setParameter(Profile.CONTAINER_NAME, "Matrix");

        // create container
        AgentContainer container = runtime.createMainContainer(profile);

        try {
            AgentController fileHeaderReaderAgentController = container.createNewAgent("fileHeaderReaderAgent", FileHeaderReaderAgent.class.getName(), null);
            fileHeaderReaderAgentController.start();

            AgentController sqlDatabaseReaderAgentController = container.createNewAgent("sqlDatabaseReaderAgent", SqlDatabaseReaderAgent.class.getName(), null);
            sqlDatabaseReaderAgentController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
