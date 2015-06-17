package me.zcx.vertx.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpClient;

/**
 * Created by zcx2001 on 2015-06-17.
 */
public class HttpClientTest extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        HttpClient client = vertx.createHttpClient();
        client.getNow(80, "img01.taobaocdn.com", "/imgextra/i1/320868932/TB2vXMccVXXXXb3XXXXXXXXXXXX_%21%21320868932.jpg", resp1 -> {
            System.out.println("Received response with status code " + resp1.statusCode());
            resp1.bodyHandler(a -> {
                System.out.println(a.length());

                AsyncFile output = vertx.fileSystem().openBlocking("/a.jpg", new OpenOptions());
                output.write(a);
                output.close();
            });
        });
    }

    @Override
    public void stop() throws Exception {

    }
}
