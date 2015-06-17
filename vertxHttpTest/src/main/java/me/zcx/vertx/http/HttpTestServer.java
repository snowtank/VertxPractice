package me.zcx.vertx.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.apache.commons.io.FilenameUtils;

import java.net.URL;
import java.net.URLDecoder;
import java.util.UUID;

/**
 * Created by zcx2001 on 2015-06-17.
 */
public class HttpTestServer extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route("/static/*").handler(StaticHandler.create());

        router.route("/api/UEFetchImage").blockingHandler(routingContext -> {
            HttpServerRequest req = routingContext.request();
            HttpServerResponse resp = routingContext.response();
            JsonObject ret = new JsonObject();
            ret.put("state", "SUCCESS");

            JsonArray list = new JsonArray();
            req.params().entries().stream().filter(w -> w.getKey().startsWith("source"))
                    .forEach(urlInfo -> {
                        try {
                            String source = urlInfo.getValue();
                            URL sourceURL = new URL(source);
                            String ext = FilenameUtils.getExtension(sourceURL.getPath());
                            URL u = getClassLoader().getResource("");
                            String fileName = URLDecoder.decode(u.getPath(), "utf-8")
                                    + "/webroot/" + UUID.randomUUID().toString().replace("-", "") + "." + ext;
                            HttpClient client = vertx.createHttpClient();
                            client.getNow(sourceURL.getPort() != -1 ? sourceURL.getPort() : sourceURL.getDefaultPort(),
                                    sourceURL.getHost(),
                                    sourceURL.getPath(),
                                    resp1 -> resp1.bodyHandler(a -> {
                                        try {
                                            AsyncFile output = vertx.fileSystem().openBlocking(fileName, new OpenOptions());
                                            output.write(a);
                                            output.close();

                                            list.add(new JsonObject()
                                                    .put("state", "SUCCESS")
                                                    .put("title", FilenameUtils.getName(fileName))
                                                    .put("source", source)
                                                    .put("url", "http://192.168.13.31:8080/static/" + FilenameUtils.getName(fileName))
                                                    .put("size", a.length()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
            ret.put("list", list);

            StringBuilder sb = new StringBuilder();
            sb.append(req.getParam("callback"))
                    .append("(")
                    .append(ret.toString())
                    .append(")");
            System.out.println(sb.toString());
            req.response().end(sb.toString());
        });

        router.route().handler(routingContext -> {
            // This handler will be called for every request
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/plain");

            // Write to the response and end it
            response.end("Hello World from Vert.x-Web!");
        });

        server.requestHandler(router::accept).listen(8080);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    private ClassLoader getClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        return cl;
    }
}
