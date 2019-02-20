package org.eltech.ddm.frotend.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import org.eltech.ddm.common.ExecuteJob;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Test-Side route client. Routes a message to a available node
 * in order to process it.
 *
 * @author etitkov
 */
public class ActorRouterClient extends AbstractActor {
    private static final String ACTOR_SERVICE = "/user/routerService";
    private static final String NODE_ROLE = "compute";

    /*
     * Set of available cluster nodes
     */
    private final Set<Address> nodes = new HashSet<>();
    /*
     * Cluster Object to get info from
     */
    private final Cluster cluster = Cluster.get(getContext().system());


    @Override
    public void preStart() {
        cluster.subscribe(self(), ClusterEvent.MemberEvent.class, ClusterEvent.ReachabilityEvent.class);
    }

    @Override
    public void postStop() {
        cluster.unsubscribe(self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ExecuteJob.class, (j) -> {
                    List<Address> addresses = new ArrayList<>(nodes);
                    Address address = addresses.get(ThreadLocalRandom.current().nextInt(addresses.size()));
                    nodes.remove(address);
                    ActorSelection selection = getContext().actorSelection(address + ACTOR_SERVICE);
                    selection.tell(j, sender());
                })
                .match(ClusterEvent.CurrentClusterState.class, state -> {
                    nodes.clear();
                    for (Member member : state.getMembers()) {
                        if (member.hasRole(NODE_ROLE) && member.status().equals(MemberStatus.up())) {
                            nodes.add(member.address());
                        }
                    }
                })
                .match(ClusterEvent.MemberUp.class, mUp -> {
                    if (mUp.member().hasRole(NODE_ROLE))
                        nodes.add(mUp.member().address());
                })
                .match(ClusterEvent.MemberEvent.class, other -> nodes.remove(other.member().address()))
                .match(ClusterEvent.UnreachableMember.class, unreachable -> nodes.remove(unreachable.member().address()))
                .match(ClusterEvent.ReachableMember.class, reachable -> {
                    if (reachable.member().hasRole(NODE_ROLE))
                        nodes.add(reachable.member().address());
                })

                .build();
    }
}
