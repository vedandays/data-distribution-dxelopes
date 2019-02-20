package org.eltech.ddm.frotend.executors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import org.eltech.ddm.common.ExecuteJob;
import org.eltech.ddm.common.ExecuteResult;
import org.eltech.ddm.common.JobFailed;
import org.eltech.ddm.environment.DataDistribution;
import org.eltech.ddm.frotend.actors.ActorRouterClient;
import org.eltech.ddm.inputdata.MiningInputStream;
import org.eltech.ddm.miningcore.algorithms.MiningBlock;
import org.eltech.ddm.miningcore.algorithms.MiningExecutor;
import org.eltech.ddm.miningcore.miningmodel.EMiningModel;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * MiningExecutor for actor environment implementation. Sends message
 * to {@link ActorRouterClient} in order to route it to an appropriate
 * Cluster Node from the configuration file
 *
 * @author etitkov
 */
public class ActorMiningExecutor extends MiningExecutor {

    private static final Logger LOGGER = Logger.getLogger(ActorMiningExecutor.class.getName());
    private static final FiniteDuration DEFAULT_TIMEOUT = FiniteDuration.apply(60, TimeUnit.MINUTES);

    /*
     * Inbox-object in order to receive a message inside non-actor object
     */
    private final Inbox inbox;
    /*
     * Test-Side route link
     */
    private ActorRef routerClientRef;
    /*
     * Current Actor System
     */
    private ActorSystem system;
    private DataDistribution dist;

    ActorMiningExecutor(MiningBlock block,
                        MiningInputStream shallowData,
                        ActorSystem system,
                        ActorRef routerClientRef,
                        DataDistribution dist
    ) {
        super(block);
        this.dist = dist;
        this.system = system;
        this.data = shallowData;
        inbox = Inbox.create(system);
        this.routerClientRef = routerClientRef;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(EMiningModel model) {
        system.scheduler()
                .scheduleOnce(Duration.create(5, TimeUnit.SECONDS), routerClientRef, new ExecuteJob(block, model.getSettings(), model.getClass(), data, dist),
                        system.dispatcher(), inbox.getRef());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EMiningModel getModel() {
        Object message = null;
        try {
            message = inbox.receive(DEFAULT_TIMEOUT);
        } catch (TimeoutException e) {
            LOGGER.severe("Timeout for receiving a message reached, no message was provided");
        }
        if (message instanceof ExecuteResult) {
            return ((ExecuteResult) message).getModel();
        } else if (message instanceof JobFailed) {
            LOGGER.severe("Remote exception is occurred. Result for this actor is not available" + ((JobFailed) message).getException());
        } else {
            LOGGER.severe("Unknown state exception. Can't get the result from the actor");
        }
        return null;

    }
}

