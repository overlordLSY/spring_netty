package lsy.spring.netty.server.initializer;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import java.lang.reflect.Constructor;

public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final String handlerClass;
    private final String path;

    public NettyChannelInitializer(String handlerClass, String path) {
        this.handlerClass = handlerClass;
        this.path = path;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        Class<?> clazz = Class.forName(handlerClass);
        Constructor<?> constructor = clazz.getConstructor(String.class);
        Object handler = constructor.newInstance(path);
        if (handler instanceof ChannelHandlerAdapter) {
            ch.pipeline().addLast((ChannelHandlerAdapter) handler);
        }
    }
}
