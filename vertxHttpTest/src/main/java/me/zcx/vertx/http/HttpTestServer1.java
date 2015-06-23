package me.zcx.vertx.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.*;
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
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zcx2001 on 2015-06-17.
 */
public class HttpTestServer1 extends AbstractVerticle {

    public static void main(String[] args) {
        JsonObject config = new JsonObject()
                .put("host", "192.168.13.171")
                .put("port", 33333)
                .put("maxPoolSize", 10)
                .put("username", "root")
                .put("password", "123456")
                .put("database", "filemanage")
                .put("showUrl", "http://192.168.13.31:8080/static/")
                .put("savePath", "D:/单位项目/vertxTest/VertxPractice/vertxHttpTest/target/classes");
        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        Vertx.vertx().deployVerticle("me.zcx.vertx.http.HttpTestServer1", options);
    }

    @Override
    public void start() throws Exception {
        System.setProperty("vertx.disableFileCaching", "true");

        JsonObject mySQLClientConfig = new JsonObject()
                .put("host", config().getString("host"))
                .put("port", config().getInteger("port"))
                .put("maxPoolSize", config().getInteger("maxPoolSize"))
                .put("username", config().getString("username"))
                .put("password", config().getString("password"))
                .put("database", config().getString("database"));

        AsyncSQLClient sqlClient = MySQLClient.createShared(vertx, mySQLClientConfig, "fileManageDB");

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route("/static/*").handler(StaticHandler.create());

        router.route("/UEFetchImage").blockingHandler(routingContext -> {
            HttpServerRequest req = routingContext.request();
            HttpServerResponse resp = routingContext.response();
            JsonObject ret = new JsonObject();
            ret.put("state", "SUCCESS");

            JsonArray list = new JsonArray();


            IterableQueueCommand<Map.Entry<String, String>> iqc =
                    new IterableQueueCommand<>(req.params().entries().stream().filter(w -> w.getKey().startsWith("source")).iterator());

            iqc.onAction(action -> {
                try {
                    String source = action.getValue().getValue();
                    URL sourceURL = new URL(source);
                    //URL u = getClassLoader().getResource("");
                    //System.out.println("url is " + u);
                    String originalFileName = FilenameUtils.getName(sourceURL.getPath());
                    String originalFileNameExt = FilenameUtils.getExtension(sourceURL.getPath());

                    String fileId = UUID.randomUUID().toString().replace("-", "");
                    String fileName = fileId + "." + originalFileNameExt;
                    String extPath = formatter.format(LocalDate.now());
                    //String savePath = URLDecoder.decode(u.getPath(), "utf-8") + "/webroot/" + extPath + "/";
                    String savePath = Paths.get(config().getString("savePath"), extPath).toString();
                    createDir(savePath);

                    HttpClientOptions options;
                    if ("http".equalsIgnoreCase(sourceURL.getProtocol())) {
                         options = new HttpClientOptions().setSsl(false);
                    } else {
                        options = new HttpClientOptions().setSsl(true);
                    }

                    HttpClient client = vertx.createHttpClient(options);

                    client.getNow(sourceURL.getPort() != -1 ? sourceURL.getPort() : sourceURL.getDefaultPort(),
                            sourceURL.getHost(),
                            sourceURL.getPath(),
                            resp1 -> {
                                //System.out.println(resp1.statusCode());

                                resp1.bodyHandler(a -> {
                                    try {
                                        System.out.println("save = " + savePath + "/" + fileName);
                                        AsyncFile output = vertx.fileSystem().openBlocking(savePath + "/" + fileName, new OpenOptions());
                                        output.write(a);
                                        output.close();

                                        client.close();

                                        list.add(new JsonObject()
                                                .put("state", "SUCCESS")
                                                .put("title", fileName)
                                                .put("source", source)
                                                .put("url", config().getString("showUrl") + extPath + "/" + fileName)
                                                .put("size", a.length()));

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
                                                            //System.out.println("sql insert = " + r.succeeded());
                                                            if (r.failed())
                                                                r.cause().printStackTrace();
                                                            connection.close(v -> {
                                                            });
                                                            action.next();
                                                        });
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).onEnd(end -> {
                ret.put("list", list);

                StringBuilder sb = new StringBuilder();
                sb.append(req.getParam("callback"))
                        .append("(")
                        .append(ret.toString())
                        .append(")");
                // System.out.println(sb.toString());
                req.response().end(sb.toString());
            }).start();
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
