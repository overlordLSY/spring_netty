package lsy.spring_netty.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易WebSocket连接池，仅用于测试
 */
public class WebSocketChannelPool {

    public static final String PARAM_USER_ID = "userId";
    public static final AttributeKey<Long> ATTRIBUTE_USER_ID = AttributeKey.valueOf(PARAM_USER_ID);

    @Getter
    private static final ChannelGroup globalGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Getter
    private static final Map<Long, ChannelId> userChannelMap = new ConcurrentHashMap<>();

    public static void globalAdd(Channel channel) {
        globalGroup.add(channel);
    }

    public static void remove(Channel channel) {
        globalGroup.remove(channel);
        Long userId = channel.attr(ATTRIBUTE_USER_ID).get();
        if (userId != null) {
            userChannelMap.remove(userId);
        }
    }

    public static void userAdd(Long userId, Channel channel) {
        globalGroup.add(channel);
        userChannelMap.put(userId, channel.id());
    }

    public static Channel getUserChannel(Long userId) {
        if (userChannelMap.containsKey(userId)) {
            return globalGroup.find(userChannelMap.get(userId));
        }
        return null;
    }

}
