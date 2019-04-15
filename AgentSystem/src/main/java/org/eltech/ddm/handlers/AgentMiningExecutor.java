package org.eltech.ddm.handlers;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.eltech.ddm.agents.AgentModerator;
import org.eltech.ddm.common.ExecuteJob;
import org.eltech.ddm.common.ExecuteResult;
import org.eltech.ddm.common.JobFailed;
import org.eltech.ddm.environment.DataDistribution;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.miningcore.MiningException;
import org.eltech.ddm.miningcore.algorithms.MiningBlock;
import org.eltech.ddm.miningcore.algorithms.MiningExecutor;
import org.eltech.ddm.miningcore.miningmodel.EMiningModel;
import org.eltech.ddm.agents.AgentInfo;

public class AgentMiningExecutor extends MiningExecutor {

    private Object receivedMessage = null;
    private AgentExecutionEnvironmentSettings settings;
    private AgentInfo agentInfo;
    private DataDistribution dist;
    private ExecuteJob executeJob;


    protected AgentMiningExecutor(MiningBlock block,
                                  AgentExecutionEnvironmentSettings settings,
                                  AgentInfo agentInfo,
                                  DataDistribution dist,
                                  MiningInputStream data) {
        super(block);
        this.settings = settings;
        this.agentInfo = agentInfo;
        this.dist = dist;
        this.data = data;
    }

    @Override
    public void start(EMiningModel model) throws MiningException {

        /* Создание агента, который отправляет запрос на выполнение алгоритма и получает ответ, записывает его в
         Emining model. Созданный агент умирает?

         AgentModerator */
        executeJob = new ExecuteJob(block, model.getSettings(), model.getClass(), data, dist);

        Object[] args = {agentInfo, this, executeJob};

        startRemoteAgent(args);

    }

    @Override
    public EMiningModel getModel() throws ParallelExecutionException {

        //synchronize
        while (receivedMessage == null){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(receivedMessage instanceof ExecuteResult) return ((ExecuteResult) receivedMessage).getModel();

        if(receivedMessage instanceof JobFailed) System.out.println((agentInfo.getName() + " have " +
                ((JobFailed)receivedMessage).getException()));


        return null;
    }


    public void setReceivedMessage(Object receivedMessage) {
        this.receivedMessage = receivedMessage;
    }

    private void startRemoteAgent(Object[] args){

        try {
            AgentController ac = settings.getMainContainer().createNewAgent(agentInfo.getName()+"-Moderator-" +
                    + agentInfo.getCount(), AgentModerator.class.getName(), args);

            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
