package org.eltech.ddm.agents;


/* Agents info */

public class AgentInfo {


    /* using for unique name agent on platform */
    private static long count = 0;

    private String name;
    private String host;
    private String tcpPort; //<AgentName>@<ip>:<tcpPort>/JADE
    private String httpPort; //httPport of MTP address -> http://<ip>:<httPort>
    private String className;
    private String filePath;

    public AgentInfo() {
        count++;
    }

    public void setTcpPort(String tcpPort) {
        this.tcpPort = tcpPort;
    }

    public String getTcpPort() {
        return tcpPort;
    }

    public static long getCount() {
        return count;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setHttpPort(String httPport) {
        this.httpPort = httPport;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public String getHttpPort() {
        return httpPort;
    }

    public String getClassName() {
        return className;
    }


}
