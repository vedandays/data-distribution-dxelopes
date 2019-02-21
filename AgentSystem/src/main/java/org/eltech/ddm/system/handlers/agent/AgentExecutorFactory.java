package org.eltech.ddm.system.handlers.agent;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.eltech.ddm.handlers.MiningExecutorFactory;
import org.eltech.ddm.handlers.ParallelExecutionException;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.miningcore.algorithms.MiningBlock;
import system.agents.AgentInfo;

public class AgentExecutorFactory extends MiningExecutorFactory<AgentMiningExecutor> {

    private AgentExecutionEnvironmentSettings settings;
    private AgentInfo agentInfo;

    public AgentExecutorFactory(AgentExecutionEnvironmentSettings settings, AgentInfo agentInfo) {
        this.settings = settings;
        this.agentInfo = agentInfo;
    }

    @Override
    public AgentMiningExecutor create(MiningBlock block) throws ParallelExecutionException {

        /*
        * Создаётся агент, который отправляет только на 1 платформу запрос на создание 1 агента.
        * AgentCreator*/
        createRemoteAgent(block, null);

        return null;
    }

    @Override
    public AgentMiningExecutor create(MiningBlock block, MiningInputStream data) throws ParallelExecutionException {
        /* аналогично выше*/
        createRemoteAgent(block, data);
        return new AgentMiningExecutor(block);
    }

    private AgentMiningExecutor createRemoteAgent(MiningBlock block, MiningInputStream data){
        Object[] args = new Object[3];
        args[0] = agentInfo;
        args[1] = block;
        args[2] = data;

        try {
            AgentController ac = settings.getMainContainer().createNewAgent(agentInfo.getName()+"-Creator-" +
                    + agentInfo.getCount(), "system.agents.AgentCreator", args);

            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }


        return null;
    }
}
