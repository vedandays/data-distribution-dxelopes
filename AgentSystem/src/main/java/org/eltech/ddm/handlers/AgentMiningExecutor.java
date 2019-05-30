package org.eltech.ddm.handlers;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.eltech.ddm.agents.AgentModerator;
import org.eltech.ddm.common.ExecuteJob;
import org.eltech.ddm.common.ExecuteResult;
import org.eltech.ddm.common.JobFailed;
import org.eltech.ddm.common.StateExist;
import org.eltech.ddm.environment.DataDistribution;
import org.eltech.ddm.inputdata.MiningInputStream;
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
    private StateExist stateExist = null;


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
    public void start(EMiningModel model){
        executeJob = new ExecuteJob(block, model.getSettings(), model.getClass(), data, dist);

        Object[] args = {agentInfo, this, executeJob};

        startAgentModerator(args);

    }

    @Override
    public EMiningModel getModel(){
        while (!checkReceivedMessage());

        if(receivedMessage instanceof ExecuteResult) return ((ExecuteResult) receivedMessage).getModel();

        if(receivedMessage instanceof JobFailed) System.out.println((agentInfo.getName() + " have " +
                ((JobFailed)receivedMessage).getException()));

        return null;
    }


    public void setReceivedMessage(Object receivedMessage) {
        this.receivedMessage = receivedMessage;
    }

    private void startAgentModerator(Object[] args){
        try {
            AgentController ac = settings.getMainContainer().createNewAgent(agentInfo.getName() +
                    "-Moderator-" + agentInfo.getCount(), AgentModerator.class.getName(), args);

            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public boolean isCreated() {
        if (stateExist != null) return true;
        return false;
    }

    private boolean checkReceivedMessage(){
        if(receivedMessage != null) return true;
        return false;
    }

    public StateExist getStateExist() {
        return stateExist;
    }

    public void setStateExist(StateExist stateExist) {
        this.stateExist = stateExist;
    }
}
