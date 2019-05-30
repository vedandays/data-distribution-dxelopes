package org.eltech.ddm.handlers;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.eltech.ddm.agents.AgentCreator;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.miningcore.algorithms.MiningBlock;
import org.eltech.ddm.agents.AgentInfo;

public class AgentExecutorFactory extends MiningExecutorFactory<AgentMiningExecutor> {

    private AgentExecutionEnvironmentSettings settings;
    //private boolean isExist = false;

    public AgentExecutorFactory(AgentExecutionEnvironmentSettings settings) {
        this.settings = settings;
    }

    @Override
    public AgentMiningExecutor create(MiningBlock block) {
        return null;
    }

    @Override
    public AgentMiningExecutor create(MiningBlock block, MiningInputStream data) {
        return null;
    }

    public AgentMiningExecutor create(MiningBlock block, AgentInfo agentInfo) {
        AgentMiningExecutor executor =
                new AgentMiningExecutor(block, settings, agentInfo, settings.getDataDistribution(), null);

        Object[] args = {agentInfo, executor};

        createRemoteAgent(args, agentInfo);

        return executor;
    }

    public AgentMiningExecutor create(MiningBlock block, MiningInputStream data, AgentInfo agentInfo) {
        AgentMiningExecutor executor =
                new AgentMiningExecutor(block, settings, agentInfo, settings.getDataDistribution(), data);

        Object[] args = {agentInfo, executor};

        createRemoteAgent(args, agentInfo);

        return executor;
    }

    private void createRemoteAgent(Object[] args, AgentInfo agentInfo){

        try {
            AgentController ac = settings.getMainContainer().createNewAgent(agentInfo.getName()+"-Creator-" +
                    + agentInfo.getId(), AgentCreator.class.getName(), args);

            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }
}
