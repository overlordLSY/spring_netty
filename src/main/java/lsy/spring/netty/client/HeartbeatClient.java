package lsy.spring.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import lsy.spring.netty.client.initializer.ReadChannelInitializer;
import lsy.spring.netty.client.initializer.WriteChannelInitializer;
import lsy.spring.netty.config.SpringNettyHeartbeatConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "lsy.netty.heartbeat.active", havingValue = "true")
public class HeartbeatClient implements CommandLineRunner, AutoCloseable {

    public static Channel channel;
    private static EventLoopGroup group;
    private static Bootstrap bs;
    @Autowired
    private SpringNettyHeartbeatConfig config;

    @Override
    public void run(String... args) {
        group = new NioEventLoopGroup(config.getClient().getPoolSize());
        bs = new Bootstrap().group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(config.getServer().getHost(), config.getServer().getPort());
        if (config.getClient().getReadTimeout() > 0) {
            bs.handler(new ReadChannelInitializer(this, config.getClient()));
        } else {
            bs.handler(new WriteChannelInitializer(this, config.getClient()));
        }
        connect();
    }

    public void connect() {
        ChannelFuture future = bs.connect().addListener((ChannelFutureListener) listener -> {
            if (listener.cause() != null) {
                log.error("[Server Down] 连接Heartbeat Server失败: " + listener.cause());
            }
        });
        channel = future.channel();
    }

    @Override
    public void close() throws Exception {
        group.shutdownGracefully().sync();
        log.info("-----------------------------------------Shutdown Heartbeat-----------------------------------------");
    }
}
