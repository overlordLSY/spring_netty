package lsy.spring.netty.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import lsy.spring.netty.server.bean.HeartbeatClientPool;

@Slf4j
public class HeartbeatHandler extends SimpleChannelInboundHandler<String> {

    private static final String HEARTBEAT_RESPONSE = "PONG";

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        HeartbeatClientPool.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        int index = msg.indexOf(HeartbeatClientPool.PARAM_CLIENT_ID);
        if (index >= 0) {
            String clientId = msg.substring(index + 9);
            ctx.channel().attr(HeartbeatClientPool.ATTRIBUTE_CLIENT_ID).setIfAbsent(clientId);
            HeartbeatClientPool.clientAddOrUpdate(clientId, ctx.channel());
            // 返回心跳响应
            ctx.channel().writeAndFlush(HEARTBEAT_RESPONSE);
        }
    }
}
