package me.zcx.vertx.cluster.client;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.impl.hazelcast.HazelcastClusterManager;

/**
 * Created by zcx on 6/11/2015.
 */
public class VertxClusterMain {
    public static void main(String[] args) {

        System.setProperty("vertx.cluster.public.host","192.168.2.1");
        System.setProperty("vertx.cluster.public.port","30000");
//        VertxOptions options = new VertxOptions()
//                .setClustered(true)
//                .setClusterHost("192.168.1.2")
//                .setClusterPort(5701);

        Config hazelcastConfig = new Config();

        NetworkConfig network = hazelcastConfig.getNetworkConfig();
        network.setPort(5701)
                .setPortAutoIncrement(false)
                .setPublicAddress("192.168.2.1")
                //.addOutboundPortDefinition("30000")
                .setReuseAddress(true);

        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);

        join.getTcpIpConfig()
                .setRequiredMember("192.168.1.2")
                .setEnabled(true);

        ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);

        VertxOptions options = new VertxOptions()
                .setClusterManager(mgr)
                .setClusterHost("0.0.0.0")
                .setClusterPort(30000)
                .setClustered(true);

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                vertx.deployVerticle("me.zcx.vertx.cluster.client.ClusterClient");
            } else {
                System.out.println("Cluster client is failed");
            }
        });
    }
}
