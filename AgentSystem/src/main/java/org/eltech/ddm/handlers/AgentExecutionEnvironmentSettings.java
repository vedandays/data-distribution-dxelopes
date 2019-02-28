package org.eltech.ddm.handlers;

import jade.wrapper.AgentContainer;
import org.eltech.ddm.environment.DataDistribution;
import org.eltech.ddm.sup.ConfigReader;
import org.eltech.ddm.agents.AgentInfo;

import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AgentExecutionEnvironmentSettings implements Serializable, Cloneable {

    URL res = getClass().getClassLoader().getResource("agents_info.csv");
    File file;

    {
        try {
            file = Paths.get(res.toURI()).toFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    String AGENTS_INFO_PATH = file.getAbsolutePath();

    //private static final String AGENTS_INFO_PATH = "agents_info.csv";

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
        System.out.println(AGENTS_INFO_PATH);
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
