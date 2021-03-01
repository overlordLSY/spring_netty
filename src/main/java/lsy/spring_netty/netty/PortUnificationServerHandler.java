package lsy.spring_netty.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import org.apache.commons.lang3.StringUtils;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 请求协议分发
 */
public class PortUnificationServerHandler extends ByteToMessageDecoder {

    private final String path;

    public PortUnificationServerHandler(String path) {
        this.path = path;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (isWebSocket(in)) {
            switchToWebSocket(ctx.pipeline());
        } else if (isTask(in)) {
            switchToTask(ctx.pipeline());
        } else {
            // 其他协议直接忽略
            in.clear();
            ctx.close();
        }
    }

    /**
     * 判断请求是否为WebSocket，判断依据：GET /ws
     */
    private boolean isWebSocket(ByteBuf in) {
        return checkSocketHead(in, "GET " + path);
    }

    private boolean checkSocketHead(ByteBuf byteBuf, String head) {
        if (byteBuf.readableBytes() < head.length()) {
            return false;
        }
        byteBuf.markReaderIndex();
        byte[] content = new byte[head.length()];
        byteBuf.readBytes(content);
        byteBuf.resetReaderIndex();
        return StringUtils.equals(head, new String(content, StandardCharsets.UTF_8));
    }

    /**
     * 判断请求是否为自定义调起定时任务协议
     */
    private boolean isTask(ByteBuf in) {
        return checkSocketHead(in, "TASK ");
    }

    /**
     * WebSocket协议处理
     */
    private void switchToWebSocket(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec()); //http解码
        pipeline.addLast(new HttpObjectAggregator(65536)); //单次请求大小限制
        pipeline.addLast(new WebSocketServerCompressionHandler()); //WebSocket数据压缩
        pipeline.addLast(new WebSocketHandler(path)); // 自定义处理
        pipeline.addLast(new WebSocketServerProtocolHandler(path, null, true)); // WebSocket协议升级
        pipeline.remove(this);
    }

    /**
     * 自定义协议处理，调起定时任务
     */
    private void switchToTask(ChannelPipeline pipeline) {
        pipeline.addLast(new TaskHandler());
        pipeline.remove(this);
    }

}
