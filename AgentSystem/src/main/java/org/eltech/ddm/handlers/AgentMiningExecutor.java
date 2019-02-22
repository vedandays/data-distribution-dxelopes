package org.eltech.ddm.handlers;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.eltech.ddm.miningcore.MiningException;
import org.eltech.ddm.miningcore.algorithms.MiningBlock;
import org.eltech.ddm.miningcore.algorithms.MiningExecutor;
import org.eltech.ddm.miningcore.miningmodel.EMiningModel;
import org.eltech.ddm.agents.AgentInfo;

public class AgentMiningExecutor extends MiningExecutor {

    private EMiningModel model;
    private AgentExecutionEnvironmentSettings settings;
    private AgentInfo agentInfo;

    protected AgentMiningExecutor(MiningBlock block, AgentExecutionEnvironmentSettings settings, AgentInfo agentInfo) {
        super(block);
        this.settings = settings;
        this.agentInfo = agentInfo;
    }

    @Override
    public void start(EMiningModel model) throws MiningException {

        /* Создание агента, который отправляет запрос на выполнение алгоритма и получает ответ, записывает его в
         Emining model. Созданный агент умирает?

         AgentModerator */

        Object[] args = {agentInfo, model};

        startRemoteAgent(args);

    }

    @Override
    public EMiningModel getModel() throws ParallelExecutionException {

        /* ??????????????
        * Ожидание пока model будет не null и потом return. */
        return model;
    }

    public void setModel(EMiningModel model) {
        this.model = model;
    }

    private AgentMiningExecutor startRemoteAgent(Object[] args){

        try {
            AgentController ac = settings.getMainContainer().createNewAgent(agentInfo.getName()+"-Moderator-" +
                    + agentInfo.getCount(), "org.eltech.ddm.agents.AgentModerator", args);

            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }


        return null;
    }
}
