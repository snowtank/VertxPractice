package me.zcx.vertx.test1;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

/**
 * Created by zcx on 6/10/2015.
 */
public class VertxTest1 {
    public static void main(String[] args) {
        DeploymentOptions options = new DeploymentOptions().setInstances(30);
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle("me.zcx.vertx.test1.UEfetchImage", options, res -> {
            if (res.succeeded()) {
                System.out.println(res.result());
                String deploymentID = res.result();
                vertx.setTimer(10000, x -> {
                            System.out.println(deploymentID);
                            vertx.undeploy(deploymentID, aa -> {
                                System.out.println("undeploy = " + aa.succeeded());
                            });
                        }
                );
            }
        });
    }
}
