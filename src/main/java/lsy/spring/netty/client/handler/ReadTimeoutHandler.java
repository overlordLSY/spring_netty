package lsy.spring.netty.client.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import lsy.spring.netty.client.HeartbeatClient;
import lsy.spring.netty.config.SpringNettyHeartbeatConfig.Client;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Sharable
public class ReadTimeoutHandler extends SimpleChannelInboundHandler<Object> {

    private final HeartbeatClient client;
    private final String heartbeatMsg;
    private final int reconnectDelay;
    public long startTime;
    private static AtomicInteger errorCount;

    public ReadTimeoutHandler(HeartbeatClient client, Client clientConfig) {
        this.client = client;
        this.heartbeatMsg = clientConfig.getHeartbeatPrefix() + " clientId=" + clientConfig.getClientId();
        this.reconnectDelay = clientConfig.getReconnectDelay();
        this.startTime = -1L;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        errorCount = new AtomicInteger(-1);
        startTime = System.currentTimeMillis();
        printLog("建立Heartbeat连接: " + ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        // 需要利用超时机制来定时发送心跳信息，所以计数从-1开始，大于0才是真超时
        errorCount = new AtomicInteger(-1);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            int count = errorCount.incrementAndGet();
            if (count > 0) {
                IdleStateEvent e = (IdleStateEvent) evt;
                printLog("心跳超时，State=" + e.state() + "，次数:" + count);
            }
            if (count < 3) {
                printLog("发送心跳：" + heartbeatMsg);
                ctx.channel().writeAndFlush(heartbeatMsg);
            } else {
                printLog("连接超时断开");
                ctx.close();
            }
        }
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        startTime = -1L;
        printLog("Heartbeat连接断开: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) {
        ctx.channel().eventLoop().schedule(() -> {
            printLog("Heartbeat重连" + ctx.channel().remoteAddress());
            client.connect();
        }, reconnectDelay, TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void printLog(String msg) {
        if (startTime < 0) {
            log.error("[Server Down] " + msg);
        } else {
            log.info("[Server Connect] " + msg);
        }
    }
}
