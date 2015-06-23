package me.zcx.vertx.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import org.apache.commons.io.FilenameUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by zcx2001 on 2015-06-17.
 */
public class HttpClientTest extends AbstractVerticle {
    public static void main(String[] args) throws Exception {
        //System.out.println(URLDecoder.decode("https%3A%2F%2Fimg.alicdn.com%2Fimgextra%2Fi3%2F356060330%2FTB2iW00cXXXXXaRXpXXXXXXXXXX-356060330.jpg","utf-8"));
        //URL sourceURL = new URL("https://img.alicdn.com/imgextra/i3/356060330/TB2iW00cXXXXXaRXpXXXXXXXXXX-356060330.jpg");
        URL sourceURL = new URL("http://img.alicdn.com/imgextra/i3/356060330/TB2iW00cXXXXXaRXpXXXXXXXXXX-356060330.jpg");
        //URL u = getClassLoader().getResource("");
        //System.out.println("url is " + u);
        System.out.println(sourceURL.getProtocol());
        System.out.println(sourceURL.getPort());
        System.out.println(sourceURL.getDefaultPort());
        System.out.println(FilenameUtils.getName(sourceURL.getPath()));
        System.out.println(FilenameUtils.getExtension(sourceURL.getPath()));

//        Vertx.vertx().deployVerticle("me.zcx.vertx.http.HttpClientTest");
    }

    @Override
    public void start() throws Exception {
        //
        HttpClientOptions options = new HttpClientOptions().setSsl(true);
        HttpClient client = vertx.createHttpClient(options);
//        client.getNow(443, "img.alicdn.com", "/imgextra/i3/356060330/TB2iW00cXXXXXaRXpXXXXXXXXXX-356060330.jpg", resp1 -> {
        client.getNow(80, "img01.taobaocdn.com", "/imgextra/i1/320868932/TB2vXMccVXXXXb3XXXXXXXXXXXX_%21%21320868932.jpg", resp1 -> {
            System.out.println("Received response with status code " + resp1.statusCode());
            resp1.bodyHandler(a -> {
                System.out.println(a.length());

                AsyncFile output = vertx.fileSystem().openBlocking("d:/a.jpg", new OpenOptions());
                output.write(a);
                output.close();
            });
        });
    }

    @Override
    public void stop() throws Exception {

    }
}
