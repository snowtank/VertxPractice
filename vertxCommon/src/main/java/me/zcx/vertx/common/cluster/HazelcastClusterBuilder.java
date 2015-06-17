package me.zcx.vertx.common.cluster;

import com.hazelcast.config.Config;
import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.impl.hazelcast.HazelcastClusterManager;

import java.util.Objects;

/**
 * Vertx下Hazelcast集群辅助构造Options工具
 * 默认组播功能使用TCP连接（阿里云VPC网络环境不支持组播）
 * Created by zcx2001 on 2015-06-17.
 */
public final class HazelcastClusterBuilder {

    public static HazelcastClusterBuilder create() {
        return new HazelcastClusterBuilder();
    }

    private HazelcastClusterBuilder() {
        options = new VertxOptions();
        hazelcastConfig = new Config();

        /**
         * 默认设置Hazelcast下
         * 监听端口不自动增长
         * ReuseAddress为真
         */
        hazelcastConfig.getNetworkConfig()
                .setPortAutoIncrement(false)
                .setReuseAddress(true);

        /**
         * 关闭Hazelcast的组播功能
         */
        hazelcastConfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);

        /**
         * 打开Hazelcast的TCP/IP功能
         */
        hazelcastConfig.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
        /**
         * 设置Vertx的ClusterManager
         */
        options.setClusterManager(new HazelcastClusterManager(hazelcastConfig));
        /**
         * 设置Event的监听地址
         */
        options.setClusterHost("0.0.0.0");
    }

    public HazelcastClusterBuilder setClusterPublicAddress(String publicAddress) {
        Objects.requireNonNull(publicAddress, "publicAddress is null");
        /**
         *  设置Hazelcast下使用的发布地址
         */
        hazelcastConfig.getNetworkConfig().setPublicAddress(publicAddress);
        /**
         *  设置Vertx下EventBus使用的发布地址
         */
        System.setProperty("vertx.cluster.public.host", publicAddress);
        return this;
    }

    public HazelcastClusterBuilder setClusterPublicPort(int hazelcastPort, int eventBusPort) {
        if (hazelcastPort < 0 || hazelcastPort > 65535) {
            throw new IllegalArgumentException("hazelcastPort p must be in range 0 <= p <= 65535");
        }
        if (eventBusPort < 0 || eventBusPort > 65535) {
            throw new IllegalArgumentException("eventBusPort p must be in range 0 <= p <= 65535");
        }
        /**
         *  设置Hazelcast下使用的发布端口
         */
        hazelcastConfig.getNetworkConfig().setPort(hazelcastPort);
        /**
         *  设置Vertx下EventBus使用的发布端口
         */
        System.setProperty("vertx.cluster.public.port", String.valueOf(eventBusPort));
        options.setClusterPort(30000);
        return this;
    }

    public HazelcastClusterBuilder addMember(String member) {
        Objects.requireNonNull(member, "member is null");
        hazelcastConfig.getNetworkConfig().getJoin().getTcpIpConfig().addMember(member);
        return this;
    }

    public HazelcastClusterBuilder setRequiredMember(String requiredMember) {
        Objects.requireNonNull(requiredMember, "requiredMember is null");
        hazelcastConfig.getNetworkConfig().getJoin().getTcpIpConfig().setRequiredMember(requiredMember);
        return this;
    }

    public VertxOptions build() {
        return options;
    }

    private VertxOptions options;
    private Config hazelcastConfig;
}
