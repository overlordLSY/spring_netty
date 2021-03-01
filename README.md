# Spring MVC 中使用 Netty

Netty服务端与Spring使用不同端口

Netty一个端口，支持WebSocket等多种协议分发处理

- Netty服务端启动器：NettyServer
- 多种协议分发：PortUnificationServerHandler
- WebSocket处理：WebSocketHandler
- 自定义协议处理，及@Autowired使用：TaskHandler
- 前端WebSocket使用：utils.js

更多使用技巧参考 [Netty官方文档及示例](https://netty.io/wiki/index.html)
