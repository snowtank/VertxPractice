package me.zcx.vertx.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Created by zcx2001 on 2015-06-17.
 */
public class HttpTestServer extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle("me.zcx.vertx.http.HttpTestServer");
    }

    @Override
    public void start() throws Exception {
        JsonObject mySQLClientConfig = new JsonObject()
                .put("host", "192.168.13.171")
                .put("port", 33333)
                .put("maxPoolSize", 10)
                .put("username", "root")
                .put("password", "123456")
                .put("database", "filemanage");

        AsyncSQLClient sqlClient = MySQLClient.createShared(vertx, mySQLClientConfig, "fileManageDB");

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
                            URL u = getClassLoader().getResource("");

                            String originalFileName = FilenameUtils.getName(sourceURL.getPath());
                            String originalFileNameExt = FilenameUtils.getExtension(sourceURL.getPath());

                            String fileId = UUID.randomUUID().toString().replace("-", "");
                            String fileName = fileId + "." + originalFileNameExt;
                            String extPath = formatter.format(LocalDate.now());
                            String savePath = URLDecoder.decode(u.getPath(), "utf-8") + "/webroot/" + extPath + "/";
                            createDir(savePath);

                            list.add(new JsonObject()
                                    .put("state", "SUCCESS")
                                    .put("title", fileName)
                                    .put("source", source)
                                    .put("url", "http://192.168.13.31:8080/static/" + extPath + "/" + fileName)
                                    .put("size", 0));

                            HttpClient client = vertx.createHttpClient();
                            client.getNow(sourceURL.getPort() != -1 ? sourceURL.getPort() : sourceURL.getDefaultPort(),
                                    sourceURL.getHost(),
                                    sourceURL.getPath(),
                                    resp1 -> resp1.bodyHandler(a -> {
                                        try {
                                            AsyncFile output = vertx.fileSystem().openBlocking(savePath + fileName, new OpenOptions());
                                            output.write(a);
                                            output.close();

                                            sqlClient.getConnection(c -> {
                                                if (c.succeeded()) {
                                                    SQLConnection connection = c.result();

                                                    JsonArray params = new JsonArray();
                                                    params.add(extPath)
                                                            .add(fileId)
                                                            .add(fileName)
                                                            .add(originalFileName);
                                                    connection.updateWithParams("INSERT INTO FileInfo (\n" +
                                                                    "\textPath,\n" +
                                                                    "\tfileId,\n" +
                                                                    "\tfileName,\n" +
                                                                    "\tfileState,\n" +
                                                                    "\tfileTempId,\n" +
                                                                    "\tfileType,\n" +
                                                                    "\toriginalFileName,\n" +
                                                                    "\tuploadDateTime,\n" +
                                                                    "\tversion\n" +
                                                                    ")\n" +
                                                                    "VALUES\n" +
                                                                    "\t(?,?,?,0,null,1,?,now(),0)", params,
                                                            r -> {
                                                                System.out.println("sql insert = " + r.succeeded());
                                                                if (r.failed())
                                                                    r.cause().printStackTrace();
                                                            });
                                                }
                                            });
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

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");

    private void createDir(String creaDir) {
        File directory = new File(creaDir);
        if (!directory.exists())
            directory.mkdirs();
    }

    private ClassLoader getClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        return cl;
    }
}
