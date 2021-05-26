package lsy.spring.netty.server.bean;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HeartbeatClientPool {

    public static final String PARAM_CLIENT_ID = "clientId";
    public static final AttributeKey<String> ATTRIBUTE_CLIENT_ID = AttributeKey.valueOf(PARAM_CLIENT_ID);
    public static final ChannelGroup globalGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static final Map<String, ClientChannel> clientChannelMap = new ConcurrentHashMap<>();

    public static void globalAdd(Channel channel) {
        globalGroup.add(channel);
    }

    public static void remove(Channel channel) {
        globalGroup.remove(channel);
        String clientId = channel.attr(ATTRIBUTE_CLIENT_ID).get();
        if (StringUtils.isNotBlank(clientId)) {
            clientChannelMap.remove(clientId);
        }
    }

    public static void clientAddOrUpdate(String clientId, Channel channel) {
        if (clientChannelMap.containsKey(clientId)) {
            clientChannelMap.get(clientId).updateActiveTime();
        } else {
            globalGroup.add(channel);
            clientChannelMap.put(clientId, new ClientChannel(channel.id()));
        }
    }

    public static Channel getClientChannel(String clientId) {
        if (clientChannelMap.containsKey(clientId)) {
            return globalGroup.find(clientChannelMap.get(clientId).getChannelId());
        }
        return null;
    }

    @Data
    public static class ClientChannel {
        private ChannelId channelId;
        private Long lastActiveTime;

        public ClientChannel(ChannelId channelId) {
            this.channelId = channelId;
            this.lastActiveTime = System.currentTimeMillis();
        }

        public void updateActiveTime() {
            this.lastActiveTime = System.currentTimeMillis();
        }
    }
}
