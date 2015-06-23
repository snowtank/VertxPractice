package me.zcx.vertx.http;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

/**
 * Created by zcx2001 on 2015-06-22.
 */
public class HttpMain {
    public static void main(String[] args) {
        DeploymentOptions options = new DeploymentOptions().setInstances(30);
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle("me.zcx.vertx.http.HttpTestServer1");
    }
}
