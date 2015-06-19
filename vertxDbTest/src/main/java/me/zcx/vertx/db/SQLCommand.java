package me.zcx.vertx.db;

import io.vertx.ext.sql.SQLConnection;

/**
 * Created by zcx2001 on 2015-06-19.
 */
public class SQLCommand {
    public SQLCommand(SQLConnection conn) {
        this.conn = conn;
    }

    private SQLConnection conn;
}
