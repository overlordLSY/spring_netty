package lsy.spring_netty.netty;

import com.alibaba.fastjson.JSON;
import lsy.spring_netty.bean.WebSocketMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import lsy.spring_netty.utils.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import java.util.Map;

/**
 * WebSocket处理
 */
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final String path;

    public WebSocketHandler(String path) {
        this.path = path;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        WebSocketChannelPool.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);

        } else if (msg instanceof TextWebSocketFrame) {
            handlerWebSocketFrame(ctx, (TextWebSocketFrame) msg);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        String msg = frame.text();
        log.info("WebSocket Request: " + msg);
        try {
            // 消息处理，格式{"userId":123,"message":"消息内容"}
            WebSocketMessage messageDTO = JSON.parseObject(msg, WebSocketMessage.class);
            if (messageDTO.getUserId().equals(0L)) {
                WebSocketChannelPool.getGlobalGroup().writeAndFlush(new TextWebSocketFrame(msg));
            } else {
                Channel channel = WebSocketChannelPool.getUserChannel(messageDTO.getUserId());
                if (channel != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(msg));
                }
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            ctx.channel().writeAndFlush(new TextWebSocketFrame("消息格式错误: " + msg));
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        if (uri.startsWith(path)) {
            Map<String, String> param = RequestUtil.getParameterFromUrl(uri);
            String uid = param.get(WebSocketChannelPool.PARAM_USER_ID);
            if (StringUtils.isNumeric(uid)) {
                Long userId = Long.valueOf(uid);
                // 将用户ID存入Channel属性
                ctx.channel().attr(WebSocketChannelPool.ATTRIBUTE_USER_ID).setIfAbsent(userId);
                WebSocketChannelPool.userAdd(userId, ctx.channel());
            }
        }

        if (uri.contains("?")) {
            request.setUri(uri.substring(0, uri.indexOf("?")));
        }
    }
}
