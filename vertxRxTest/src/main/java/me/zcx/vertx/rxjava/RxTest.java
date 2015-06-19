package me.zcx.vertx.rxjava;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.rx.java.RxHelper;
import rx.Observable;

/**
 * Created by zcx2001 on 2015-06-19.
 */
public class RxTest extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle("me.zcx.vertx.rxjava.RxTest");
    }

    @Override
    public void start() throws Exception {



    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
