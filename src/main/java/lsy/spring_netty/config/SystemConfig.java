package lsy.spring_netty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "system")
public class SystemConfig {

    private final Netty netty = new Netty();

    @Data
    public static class Netty {
        private int port;
        private String path;
    }

}
