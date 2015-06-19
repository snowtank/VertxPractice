package me.zcx.vertx.db;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;

/**
 * Created by zcx2001 on 2015-06-19.
 */
public class DbTest extends AbstractVerticle {
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


        sqlClient.getConnection(conn -> {
            if (conn.failed())
                throw new RuntimeException(conn.cause());
            SQLCommand sqlCmd = new SQLCommand(conn.result());


        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
