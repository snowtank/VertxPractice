package me.zcx.vertx.cluster.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

/**
 * Created by zcx on 6/11/2015.
 */
public class ClusterServer extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        System.out.println("start cluster server");
        EventBus eventBus = vertx.eventBus();
        vertx.setPeriodic(5000, timer -> {
            System.out.println("send message");
            eventBus.send("message.test", "Yay! Someone kicked a ball");
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
