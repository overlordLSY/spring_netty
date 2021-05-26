# SpringBoot 中使用 Netty

服务端与Spring使用不同端口，支持WebSocket等多种协议分发处理

心跳客户端，支持断线重连，双向心跳

### 配置文件示例

```
lsy:
  netty:
    server:
      active: true #是否启动Netty Server，Junit时测试需改为false
      handlerClass: lsy.spring.netty.server.handler.PortUnificationServerHandler #协议分发Handler
      port: 8081 #Netty需要与Spring使用不同端口
      path: /ws #Websocket请求前缀
      
    heartbeat:
      active: true #是否启动心跳，Junit时测试需改为false
      server:
        host: 127.0.0.1 #服务端IP，不能用域名
        port: 8082
      client:
        poolSize: 2 #不填默认为虚拟机数量x2
        clientId: heartbeat-client-1 #客户端别名
        heartbeatPrefix: PING #心跳请求前缀
        reconnectDelay: 30 #尝试重连延时
        readTimeout: 60 #读超时时间，大于0开启双向心跳，即server有回传
        writeTimeout: 60 #写超时时间
```

更多使用技巧参考 [Netty官方文档及示例](https://netty.io/wiki/index.html)
