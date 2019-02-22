package org.eltech.ddm.handlers;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.miningcore.algorithms.MiningBlock;
import org.eltech.ddm.agents.AgentInfo;

public class AgentExecutorFactory extends MiningExecutorFactory<AgentMiningExecutor> {

    private AgentExecutionEnvironmentSettings settings;
    private AgentInfo agentInfo;
    private boolean isCreated = false;

    public AgentExecutorFactory(AgentExecutionEnvironmentSettings settings, AgentInfo agentInfo) {
        this.settings = settings;
        this.agentInfo = agentInfo;
    }

    @Override
    public AgentMiningExecutor create(MiningBlock block) throws ParallelExecutionException {

        /*
        * Создаётся агент, который отправляет только на 1 платформу запрос на создание 1 агента.
        * AgentCreator*/
        Object[] args = {agentInfo, block};

        createRemoteAgent(args);

        return null;
    }

    @Override
    public AgentMiningExecutor create(MiningBlock block, MiningInputStream data) throws ParallelExecutionException {
        /* аналогично выше*/

        Object[] args = {agentInfo, block, data};

        createRemoteAgent(args);

        return null;
    }

    private AgentMiningExecutor createRemoteAgent(Object[] args){

        try {
            AgentController ac = settings.getMainContainer().createNewAgent(agentInfo.getName()+"-Creator-" +
                    + agentInfo.getCount(), "org.eltech.ddm.agents.AgentCreator", args);

            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
        this.isCreated = true;


        return null;
    }

    public boolean isCreated() {
        return isCreated;
    }
}
