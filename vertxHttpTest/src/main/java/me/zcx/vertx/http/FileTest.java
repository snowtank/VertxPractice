package me.zcx.vertx.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.impl.VertxInternal;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by zcx2001 on 2015-06-17.
 */
public class FileTest extends AbstractVerticle {

    @Override
    public void start() throws Exception {
//        AsyncFile output = vertx.fileSystem().openBlocking("/a.txt", new OpenOptions());
//        Buffer buff = Buffer.buffer("some string");
//        output.write(buff);
//        output.close();
        System.out.println("hello ");
        System.out.println(getClassLoader().getResource(""));
        URL u = getClassLoader().getResource("");
        System.out.println(URLDecoder.decode(u.getPath(), "utf-8"));
        AsyncFile output = vertx.fileSystem().openBlocking(URLDecoder.decode(u.getPath(), "utf-8") + "/a.txt", new OpenOptions());
        Buffer buff = Buffer.buffer("some string");
        output.write(buff);
        output.close();
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

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle("me.zcx.vertx.http.FileTest");
    }
}
