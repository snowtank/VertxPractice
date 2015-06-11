package me.zcx.vertx.cluster.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

/**
 * Created by zcx on 6/11/2015.
 */
public class ClusterClient extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        System.out.println("start cluster client");
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("message.test", message -> {
            System.out.println("recv " + message.body());
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
