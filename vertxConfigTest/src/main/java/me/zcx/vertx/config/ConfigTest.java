package me.zcx.vertx.config;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by zcx2001 on 2015-06-23.
 */
public class ConfigTest extends AbstractVerticle {
    public static void main(String[] args) {
        JsonObject config = new JsonObject().put("name", "zcx2001");
        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        Vertx.vertx().deployVerticle("me.zcx.vertx.config.ConfigTest", options);
    }

    @Override
    public void start() throws Exception {
        System.out.println("name = " + config().getString("name"));
    }

    @Override
    public void stop() throws Exception {

    }
}
