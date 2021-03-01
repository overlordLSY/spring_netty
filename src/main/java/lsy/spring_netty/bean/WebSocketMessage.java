package lsy.spring_netty.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebSocketMessage {

    private Long userId;
    private String message;
    private String method;
    private Object data;

    public WebSocketMessage(String method, Object data) {
        this.userId = 0L;
        this.method = method;
        this.data = data;
    }
}
