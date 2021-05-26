package lsy.spring.netty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "lsy.netty.heartbeat")
public class SpringNettyHeartbeatConfig {

    private boolean active;
    private final Server server = new Server();
    private final Client client = new Client();

    @Data
    public static class Server {
        private String host;
        private int port;
    }

    @Data
    public static class Client {
        private int poolSize = 0;
        private String clientId;
        private String heartbeatPrefix = "PING";
        private int reconnectDelay = 30;
        private int readTimeout = 0;
        private int writeTimeout = 60;
    }
}
