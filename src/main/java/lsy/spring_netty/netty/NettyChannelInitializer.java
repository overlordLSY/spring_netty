package lsy.spring_netty.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final String path;

    public NettyChannelInitializer(String path) {
        this.path = path;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new PortUnificationServerHandler(path));
    }
}
