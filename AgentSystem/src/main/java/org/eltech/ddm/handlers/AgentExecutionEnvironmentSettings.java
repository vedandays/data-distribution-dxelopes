package org.eltech.ddm.handlers;

import jade.wrapper.AgentContainer;
import org.eltech.ddm.environment.DataDistribution;
import org.eltech.ddm.sup.ConfigReader;
import org.eltech.ddm.agents.AgentInfo;

import java.io.Serializable;
import java.util.ArrayList;

public class AgentExecutionEnvironmentSettings implements Serializable, Cloneable {

    private static final String AGENTS_INFO_PATH = "resources/agents_info.csv";

    private final DataDistribution dataDistribution;
    //private final List<String> fileList = new ArrayList<>();
    private ArrayList<AgentInfo> agentInfoArrayList;
    private AgentContainer mainContainer = null;

    public void setMainContainer(AgentContainer mainContainer) {
        this.mainContainer = mainContainer;
    }

    public AgentContainer getMainContainer() {
        return mainContainer;
    }

    public AgentExecutionEnvironmentSettings(DataDistribution dataDistribution) {
        this.dataDistribution = dataDistribution;
        agentInfoArrayList = ConfigReader.readFile(AGENTS_INFO_PATH);
    }

    public DataDistribution getDataDistribution() {
        return dataDistribution;
    }

    public ArrayList<AgentInfo> getAgentInfoArrayList() {
        return agentInfoArrayList;
    }

//    /**
//     * Builder-like method to provide data files
//     * @param data - file relative or absolute path
//     * @return - current instance
//     */
//    public AgentExecutionEnvironmentSettings provideDatafile(String... data) {
//        fileList.addAll(Arrays.asList(data));
//        return this;
//    }

//    public AgentExecutionEnvironmentSettings provideDatafile(){
//        return this;
//    }




//    public List<String> getFileList() {
//        return fileList;
//    }


}
