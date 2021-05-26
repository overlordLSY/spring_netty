package lsy.spring.netty.client.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lsy.spring.netty.client.HeartbeatClient;
import lsy.spring.netty.client.handler.ReadTimeoutHandler;
import lsy.spring.netty.config.SpringNettyHeartbeatConfig.Client;
import java.nio.charset.StandardCharsets;

public class ReadChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final HeartbeatClient client;
    private final Client clientConfig;

    public ReadChannelInitializer(HeartbeatClient client, Client clientConfig) {
        this.client = client;
        this.clientConfig = clientConfig;
    }

    @Override
    protected void initChannel(SocketChannel sc) {
        sc.pipeline().addLast(new IdleStateHandler(clientConfig.getReadTimeout(), 0, 0))
                .addLast(new StringDecoder(StandardCharsets.UTF_8))
                .addLast(new ReadTimeoutHandler(client, clientConfig))
                .addLast(new StringEncoder(StandardCharsets.UTF_8));
    }
}
