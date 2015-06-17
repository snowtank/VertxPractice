package me.zcx.vertx.common.cluster;

import io.vertx.core.VertxOptions;

import java.util.Objects;

/**
 * Created by zcx2001 on 2015-06-17.
 */
public class Test1 {
    public static void main(String[] args) {
        VertxOptions options = HazelcastClusterBuilder.create().build();
        Objects.requireNonNull(options);
    }
}
