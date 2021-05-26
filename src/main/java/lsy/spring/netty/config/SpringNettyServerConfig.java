package lsy.spring.netty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "lsy.netty.server")
public class SpringNettyServerConfig {

    private boolean active;
    private String handlerClass;
    private int port;
    private String path;
}
