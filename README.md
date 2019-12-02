# rim

## 系统架构图
![avatar](https://github.com/LeisurelyLife/rim/blob/master/pic/jiagou.png?raw=true)

## 说明
### client
用户使用的客户端，与route和server交互。目前使用网页实现
### route
路由，client通过nginx与route交互，实现注册登录发送信息等功能的实现。  
route负责与数据库交互；监控zookeeper上server的注册信息；维护用户登录状态数据；  
将client发送的消息通过路由数据转发到目标用户连接的server并推送
### server
client与server建立长连接；server维护用户登录状态数据；server将消息推送到client上。
### redis
存储用户登录数据
### zookeeper
server注册自身服务

## 流程图
![avatar](https://github.com/LeisurelyLife/rim/blob/master/pic/%E6%B5%81%E7%A8%8B%E5%9B%BE.png?raw=true)

### 详细流程
1. server启动将自己的服务信息注册到zookeeper，route监听zookeeper的server列表  
2. 客户端登录，route为用户分配一个server，将登录信息存储到redis上并设置过期时间，放回数据给client
3. client通过分配的server信息与server建立长连接，并且取消redis登录信息的过期时间
4. client通过route发送消息，route通过redis查询用户所在server服务器并把消息转发，server接到消息推送到对应用户的client
至此，一次聊天过程完成
