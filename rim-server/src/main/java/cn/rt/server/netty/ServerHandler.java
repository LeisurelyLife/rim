package cn.rt.server.netty;

import cn.hutool.json.JSONObject;
import cn.rt.common.common.Constants;
import cn.rt.common.util.IPUtil;
import cn.rt.server.util.SessionSocketHolder;
import cn.rt.server.util.SpringBeanFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author ruanting
 * @date 2019/11/19
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * 用于服务器端web套接字打开和关闭握手
     */
    private WebSocketServerHandshaker handshaker;

    private static final String WEB_SOCKET_URL = "/websocket";

    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("有新消息");
        //处理客户端向服务端发起http握手请求业务
        if (msg instanceof FullHttpRequest) {
            handHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {//处理websocket连接业务
            handWebsocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (!req.getDecoderResult().isSuccess() || !("websocket".equals(req.headers().get("Upgrade")))) {
            //处理不是握手请求
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(WEB_SOCKET_URL, null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 服务端向客户端响应消息
     *
     * @param ctx
     * @param req
     * @param res
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req,
                                  DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 处理客户端与服务端websocket业务
     *
     * @param ctx
     * @param frame
     */
    public void handWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //判断是否是关闭websocket的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            Channel channel = ctx.channel();
            String userId = SessionSocketHolder.moveChannel((NioSocketChannel) channel);
            RedisTemplate<String, String> redisTemplate = SpringBeanFactory.getBean("stringRedisTemplate", StringRedisTemplate.class);
            String pattern = Constants.REDIS_LOGIN_PREFIX + "|" + userId + "*";
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys.size() < 1) {
                return;
            }
            String key = keys.toArray(new String[0])[0];
            redisTemplate.delete(key);
        }

        //判断是否是ping消息
        if (frame instanceof PingWebSocketFrame) {
            System.out.println("ping消息");
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        }

        //判断是否是二进制消息，如果是直接抛出，先不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            System.out.println("目前不支持二进制消息");
            throw new RuntimeException("目前不支持二进制消息");
        }

        //返回应答消息
        //获取客户端向服务端发送的消息
        String msg = (((TextWebSocketFrame) frame).text()).replaceAll("[\\s*\\t\\n\\r]", "");
        connect(ctx, msg);
    }

    private void connect(ChannelHandlerContext ctx, String msg) {
        log.info("上送的消息: " + msg);
        try {
            Channel channel = ctx.channel();
            JSONObject object;
            try {
                object = new JSONObject(msg);
            } catch (Exception e) {
                log.error("handler解析上送报文失败：" + msg, e);
                channel.writeAndFlush("fail");
                return;
            }

            boolean exist = SessionSocketHolder.exist((NioSocketChannel) channel);
            if (!exist) {
                String pattern = Constants.REDIS_LOGIN_PREFIX + "|" + object.getStr("userId") + "*";
                RedisTemplate<String, String> redisTemplate = SpringBeanFactory.getBean("stringRedisTemplate", StringRedisTemplate.class);
                Set<String> keys = redisTemplate.keys(pattern);
                if (keys.size() < 1) {
                    //报文待定
                    channel.writeAndFlush("fail");
                    return;
                }
                String key = keys.toArray(new String[0])[0];
                String value = redisTemplate.opsForValue().get(key);
                String setAddr = key.split("\\|")[2].split(":")[0];
                String localAddr = IPUtil.getInternetIp();
                //登陆设定的服务器与当前服务器不同不允许
                if (!localAddr.equals(setAddr)) {
                    //报文待定
                    channel.writeAndFlush("fail");
                    return;
                }
                SessionSocketHolder.setUserChannel(object.getStr("userId"), (NioSocketChannel) channel);
                // 重设防止过期
                redisTemplate.opsForValue().set(key, value);
            }
            log.info("用户ID" + object.getStr("userId") + "登陆成功");
            JSONObject result = new JSONObject();
            result.put("code", Constants.ServerConntCode.SUCCESS.getCode());
            result.put("msg", "连接成功");
            channel.writeAndFlush(result.toString());
        } catch (Exception e) {
            log.error("用户注册失败", e);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String userId = SessionSocketHolder.moveChannel((NioSocketChannel) channel);
        RedisTemplate<String, String> redisTemplate = SpringBeanFactory.getBean("stringRedisTemplate", StringRedisTemplate.class);
        String pattern = Constants.REDIS_LOGIN_PREFIX + "|" + userId + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.size() < 1) {
            return;
        }
        String key = keys.toArray(new String[0])[0];
        redisTemplate.expire(key, 60, TimeUnit.SECONDS);
        log.info("用户ID " + userId + " 掉线");
        super.channelInactive(ctx);
    }

}
