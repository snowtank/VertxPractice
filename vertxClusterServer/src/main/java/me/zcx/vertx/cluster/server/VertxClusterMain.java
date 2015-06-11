package me.zcx.vertx.cluster.server;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * Created by zcx on 6/11/2015.
 */
public class VertxClusterMain {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions().setClustered(true);

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                vertx.deployVerticle("me.zcx.vertx.cluster.server.ClusterServer");
            } else {
                System.out.println("Cluster server is failed");
            }
        });
    }
}
