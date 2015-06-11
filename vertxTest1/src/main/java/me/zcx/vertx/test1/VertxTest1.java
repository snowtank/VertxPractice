package me.zcx.vertx.test1;

import io.vertx.core.Vertx;

/**
 * Created by zcx on 6/10/2015.
 */
public class VertxTest1 {
    public static void main(String[] args) {
        Vertx.vertx().createHttpServer().requestHandler(req -> req.response().end("Hello World!")).listen(8080);
    }
}
