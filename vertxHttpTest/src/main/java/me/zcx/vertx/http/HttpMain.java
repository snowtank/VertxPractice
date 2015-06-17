package me.zcx.vertx.http;

import io.vertx.core.Vertx;

/**
 * Created by zcx2001 on 2015-06-17.
 */
public class HttpMain {
    public static void main(String[] args) {
        //Vertx.vertx().deployVerticle("me.zcx.vertx.http.HttpClientTest");
        //Vertx.vertx().deployVerticle("me.zcx.vertx.http.FileTest");
        Vertx.vertx().deployVerticle("me.zcx.vertx.http.HttpTestServer");
    }
}
