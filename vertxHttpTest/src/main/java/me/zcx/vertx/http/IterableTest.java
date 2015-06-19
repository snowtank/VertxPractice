package me.zcx.vertx.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zcx2001 on 2015-06-19.
 */
public class IterableTest extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle("me.zcx.vertx.http.IterableTest");
    }

    @Override
    public void start() throws Exception {
        List<String> https = new ArrayList<>();
        https.add("http://img.alicdn.com/imgextra/i2/1579166199/TB2q.AycXXXXXcyXpXXXXXXXXXX_!!1579166199.jpg");
        https.add("http://img.alicdn.com/imgextra/i4/1579166199/TB2v23LcXXXXXXpXXXXXXXXXXXX_!!1579166199.jpg");

        JsonArray list = new JsonArray();
        @SuppressWarnings("unchecked")
        IterableQueueCommand<String> iqc = new IterableQueueCommand(https.iterator());
        iqc.onAction(action -> {
            list.add(action.getValue());
            action.next();
        }).onEnd(end -> {
            System.out.println(list.toString());
        }).start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
