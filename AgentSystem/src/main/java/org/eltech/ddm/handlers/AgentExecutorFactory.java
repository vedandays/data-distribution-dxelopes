package org.eltech.ddm.handlers;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.eltech.ddm.agents.AgentCreator;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.miningcore.algorithms.MiningBlock;
import org.eltech.ddm.agents.AgentInfo;

public class AgentExecutorFactory extends MiningExecutorFactory<AgentMiningExecutor> {

    private AgentExecutionEnvironmentSettings settings;
    //private boolean isCreated = false;

    public AgentExecutorFactory(AgentExecutionEnvironmentSettings settings) {
        this.settings = settings;
    }

    @Override
    public AgentMiningExecutor create(MiningBlock block) throws ParallelExecutionException {
        return null;
    }

    @Override
    public AgentMiningExecutor create(MiningBlock block, MiningInputStream data) throws ParallelExecutionException {
        return null;
    }

    public AgentMiningExecutor create(MiningBlock block, AgentInfo agentInfo) throws ParallelExecutionException {

        /*
        * Создаётся агент, который отправляет только на 1 платформу запрос на создание 1 агента.
        * AgentCreator*/

        Object[] args = {agentInfo};

        createRemoteAgent(args, agentInfo);

        return new AgentMiningExecutor(block, settings, agentInfo, settings.getDataDistribution(), null);
    }

    public AgentMiningExecutor create(MiningBlock block, MiningInputStream data, AgentInfo agentInfo) throws ParallelExecutionException {
        /* аналогично выше*/

        Object[] args = {agentInfo};

        createRemoteAgent(args, agentInfo);

        return new AgentMiningExecutor(block, settings, agentInfo, settings.getDataDistribution(), data);
    }

    private void createRemoteAgent(Object[] args, AgentInfo agentInfo){

        try {
            AgentController ac = settings.getMainContainer().createNewAgent(agentInfo.getName()+"-Creator-" +
                    + agentInfo.getId(), AgentCreator.class.getName(), args);

            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
        //this.isCreated = true;

    }

   /* public boolean isCreated() {
        return isCreated;
    }*/
}
