package org.eltech.ddm.backend.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.FromConfig;
import org.eltech.ddm.common.ExecuteJob;

/**
 * Router for routees on the backend side. Routes a message to an available node
 * in the cluster system and also initiates {@link ActorWorker} instance by request
 *
 * @author etitkov
 */
public class ActorRouterService extends AbstractActor {
    private static final String ACTOR_NAME = "workerRouter";
    private ActorRef workerRouter = getContext().actorOf(
            FromConfig.getInstance().props(Props.create(ActorWorker.class)), ACTOR_NAME);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ExecuteJob.class, (job) -> workerRouter.tell(job, sender()))
                .build();
    }
}

