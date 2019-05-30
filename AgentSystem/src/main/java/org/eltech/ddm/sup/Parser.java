package org.eltech.ddm.sup;

import org.eltech.ddm.agents.AgentInfo;

import java.util.ArrayList;

public class Parser {
    public static ArrayList<AgentInfo> parseArray(String[] arrayOfAgents){
        String splitBySymbol = ",";

        ArrayList<AgentInfo> agentInfoArrayList = new ArrayList<>();


        for (String agent: arrayOfAgents) {
            agentInfoArrayList.add(setInAgentInfo(splitBySymbol, agent));
        }

        return  agentInfoArrayList;

    }

    public static AgentInfo setInAgentInfo(String splitBySymbol, String agent) {
        String[] lines;
        lines = agent.split(splitBySymbol);

        AgentInfo agentInfo = new AgentInfo();

        agentInfo.setName(lines[0]);
        agentInfo.setIp(lines[1]);
        agentInfo.setHost(lines[2]);
        agentInfo.setTcpPort(lines[3]);
        agentInfo.setHttpPort(lines[4]);
        agentInfo.setClassName(lines[5]);
        agentInfo.setFilePath(lines[6]);

        return agentInfo;
    }
}
