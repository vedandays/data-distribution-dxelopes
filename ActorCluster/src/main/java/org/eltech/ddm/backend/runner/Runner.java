package org.eltech.ddm.backend.runner;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.eltech.ddm.backend.actors.ActorRouterService;

import java.util.Scanner;

/**
 * Starts 2 cluster-system instance on the machine
 *
 * @author etitkov
 */
public class Runner {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        startup();
    }

    private static void startup() {
        System.out.println("Enter number of cluster system you went to create on this node: ");
        final int clusterNodes = SCANNER.nextInt();
        final int[] inputPorts = new int[clusterNodes];
        System.out.println("Enter current host IP address: ");
        final String inputHostIp = SCANNER.next();
        for (int i = 0; i < clusterNodes; i++) {
            System.out.println(String.format("[Cluster System %d]- PORT:", i));
            inputPorts[i] = SCANNER.nextInt();
        }

        for (int port : inputPorts) {
            // Override the configuration of the port
            Config config =
                    ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port + "\n" + "akka.remote.artery.canonical.port=" + port)
                            .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname=" + inputHostIp + "\n" + "akka.remote.artery.canonical.hostname=" + inputHostIp))
                            .withFallback(ConfigFactory.parseString("akka.cluster.roles = [compute]"))
                            .withFallback(ConfigFactory.load("worker"));
            ActorSystem system = ActorSystem.create("MiningSystem", config);
            system.actorOf(Props.create(ActorRouterService.class), "routerService");
        }

    }
}
