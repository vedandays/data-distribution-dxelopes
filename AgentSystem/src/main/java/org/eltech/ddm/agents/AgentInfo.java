package org.eltech.ddm.agents;

import org.eltech.ddm.distribution.settings.ASettings;

import java.io.Serializable;

/**
 * Information about agent
 *
 * @author Derkach Petr
 */
public class AgentInfo implements Serializable {
    /* using for unique name of agent on platform */
    private static long count = 0;
    private long id;
    /* ----------------------------------------- */
    private String name;        //name of agent
    private String ip;          //IP address of node
    private String host;        //Host is localhost (computer name in local net)-> http://<localhost>:<httPort>
    private String tcpPort;     //TCP port -> <AgentName>@<ip>:<tcpPort>/JADE
    private String httpPort;    //MTP/HTTP port -> http://<localhost>:<httPort>
    private String className;   //full path to classname -> <packages>.<className>
    private String filePath;    //path to data on node
    private ASettings connectionSettings;

    public AgentInfo() {
        count++;
        this.id = count;
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

    public void setIp(String ip) {
        this.ip = ip;
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

    public String getIp() {
        return ip;
    }

    public String getHttpPort() {
        return httpPort;
    }

    public String getClassName() {
        return className;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public long getId() {
        return id;
    }

    public ASettings getConnectionSettings() {
        return connectionSettings;
    }

    public void setConnectionSettings(ASettings connectionSettings) {
        this.connectionSettings = connectionSettings;
    }

    @Override
    public String toString() {
        return "AgentInfo{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", tcpPort='" + tcpPort + '\'' +
                ", httpPort='" + httpPort + '\'' +
                ", className='" + className + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
