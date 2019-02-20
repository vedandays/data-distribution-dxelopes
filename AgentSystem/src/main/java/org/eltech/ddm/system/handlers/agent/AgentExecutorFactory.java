package org.eltech.ddm.system.handlers.agent;

import org.eltech.ddm.handlers.MiningExecutorFactory;
import org.eltech.ddm.handlers.ParallelExecutionException;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.miningcore.algorithms.MiningBlock;

public class AgentExecutorFactory extends MiningExecutorFactory<AgentMiningExecutor> {

    @Override
    public AgentMiningExecutor create(MiningBlock block) throws ParallelExecutionException {

        /*
        * Создаётся агент, который отправляет только на 1 платформу запрос на создание 1 агента.
        * AgentCreator*/

        return new AgentMiningExecutor(block);
    }

    @Override
    public AgentMiningExecutor create(MiningBlock block, MiningInputStream data) throws ParallelExecutionException {
        /* аналогично выше*/
        return new AgentMiningExecutor(block);
    }
}
