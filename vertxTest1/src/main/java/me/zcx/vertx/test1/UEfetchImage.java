package me.zcx.vertx.test1;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by zcx2001 on 2015-06-16.
 */
public class UEfetchImage extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        vertx.createHttpServer().requestHandler(req -> {
            JsonObject ret = new JsonObject();
            ret.put("state", "SUCCESS");
            JsonArray list = new JsonArray();
            req.params().entries().stream().filter(w -> w.getKey().startsWith("source"))
                    .forEach(a -> {
                        list.add(new JsonObject()
                                .put("state", "SUCCESS")
                                .put("title", "1434439616231032388.jpg")
                                .put("source", a.getValue())
                                .put("url", "http://common.culihui.com/extImage/upload/201505/723b034c61914ce8814a0e8268c48f56_400_400.png")
                                .put("size", "100847"));
                    });
            ret.put("list", list);
            StringBuilder sb = new StringBuilder();
            sb.append(req.getParam("callback"))
                    .append("(")
                    .append(ret.toString())
                    .append(")");
            req.response().end(sb.toString());
        }).listen(8080);
    }

    @Override
    public void stop() throws Exception {

    }
}
