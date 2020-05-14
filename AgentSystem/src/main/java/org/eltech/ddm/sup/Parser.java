package org.eltech.ddm.sup;

import org.eltech.ddm.agents.AgentInfo;
import org.eltech.ddm.distribution.settings.ASettings;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static ArrayList<AgentInfo> parseArray(List<ASettings> arrayOfAgents){
        String splitBySymbol = ",";

        ArrayList<AgentInfo> agentInfoArrayList = new ArrayList<>();

        for (ASettings agent : arrayOfAgents) {
            agentInfoArrayList.add(setInAgentInfo(splitBySymbol, agent));
        }

        return  agentInfoArrayList;

    }

    public static AgentInfo setInAgentInfo(String splitBySymbol, ASettings agent) {
        String[] lines;
        lines = agent.getSettingsString().split(splitBySymbol);

        AgentInfo agentInfo = new AgentInfo();

        agentInfo.setName(lines[0]);
        agentInfo.setIp(lines[1]);
        agentInfo.setHost(lines[2]);
        agentInfo.setTcpPort(lines[3]);
        agentInfo.setHttpPort(lines[4]);
        agentInfo.setClassName(lines[5]);
        agentInfo.setFilePath(lines[6]);
        agentInfo.setConnectionSettings(agent);

        return agentInfo;
    }

    public static class SettingsInfo {
        private String name;        //name of agent
        private String ip;          //IP address of node
        private String host;        //Host is localhost (computer name in local net)-> http://<localhost>:<httPort>
        private String tcpPort;     //TCP port -> <AgentName>@<ip>:<tcpPort>/JADE
        private String httpPort;    //MTP/HTTP port -> http://<localhost>:<httPort>
        private String className;   //full path to classname -> <packages>.<className>
        private String filePath;    //path to data on node

        public static SettingsInfo getInfo(String splitBySymbol, String agent) {
            String[] lines;
            lines = agent.split(splitBySymbol);

            SettingsInfo info = new SettingsInfo();

            info.setName(lines[0]);
            info.setIp(lines[1]);
            info.setHost(lines[2]);
            info.setTcpPort(lines[3]);
            info.setHttpPort(lines[4]);
            info.setClassName(lines[5]);
            info.setFilePath(lines[6]);

            return info;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getTcpPort() {
            return tcpPort;
        }

        public void setTcpPort(String tcpPort) {
            this.tcpPort = tcpPort;
        }

        public String getHttpPort() {
            return httpPort;
        }

        public void setHttpPort(String httpPort) {
            this.httpPort = httpPort;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
