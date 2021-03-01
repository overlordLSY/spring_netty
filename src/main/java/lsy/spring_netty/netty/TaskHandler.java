package lsy.spring_netty.netty;

import com.alibaba.fastjson.JSON;
import lsy.spring_netty.bean.WebSocketMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import lsy.spring_netty.service.TestService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;

/**
 * 自定义协议处理
 */
@Slf4j
@Component
public class TaskHandler extends SimpleChannelInboundHandler<Object> {

    private static TaskHandler taskHandler;

    @Autowired
    private TestService testService;

    @PostConstruct
    public void init() {
        taskHandler = this;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) {
        try {
            String msg = ((ByteBuf) o).toString(StandardCharsets.UTF_8);
            msg = msg.substring(4);
            WebSocketMessage message = JSON.parseObject(msg, WebSocketMessage.class);
            if ("PRINT_TEXT".equals(message.getMethod())) {
                taskHandler.testService.printText(message.getMessage());
            } else {
                log.error("TASK无法识别: " + msg);
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
