package org.eltech.ddm.system.handlers.agent;

import org.eltech.ddm.handlers.ParallelExecutionException;
import org.eltech.ddm.miningcore.MiningException;
import org.eltech.ddm.miningcore.algorithms.MiningBlock;
import org.eltech.ddm.miningcore.algorithms.MiningExecutor;
import org.eltech.ddm.miningcore.miningmodel.EMiningModel;

public class AgentMiningExecutor extends MiningExecutor {

    String nameAgent;
    String address;
    String port;

    protected AgentMiningExecutor(MiningBlock block) {
        super(block);
    }

    @Override
    public void start(EMiningModel model) throws MiningException {

        /* Создание агента, который отправляет запрос на выполнение алгоритма и получает ответ, записывает его в
         Emining model. Созданный агент умирает?

         AgentModerator */
    }

    @Override
    public EMiningModel getModel() throws ParallelExecutionException {

        /* ??????????????
        * Ожидание пока model будет не null и потом return. */
        return null;
    }
}
