package cn.rt.server.netty;

import cn.hutool.json.JSONObject;
import cn.rt.common.common.Constants;
import cn.rt.server.util.SessionSocketHolder;
import cn.rt.server.util.SpringBeanFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.net.InetAddress;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author ruanting
 * @date 2019/11/19
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        JSONObject object = new JSONObject(msg);
        boolean exist = SessionSocketHolder.exist((NioSocketChannel) channel);
        if (!exist) {
            String pattern = Constants.REDIS_LOGIN_PREFIX + "|" + object.getStr("userId") + "*";
            RedisTemplate<String, String> redisTemplate = SpringBeanFactory.getBean(StringRedisTemplate.class);
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys.size() < 1) {
                //报文待定
                channel.writeAndFlush("fail");
                return;
            }
            String key = keys.toArray(new String[0])[0];
            String value = redisTemplate.opsForValue().get(key);
            String setAddr = key.split("|")[2].split(":")[0];
            String localAddr = InetAddress.getLocalHost().getHostAddress();
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
        String result = "{}";
        channel.writeAndFlush(result);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "login");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String userId = SessionSocketHolder.moveChannel((NioSocketChannel) channel);
        RedisTemplate<String, String> redisTemplate = SpringBeanFactory.getBean(StringRedisTemplate.class);
        String pattern = Constants.REDIS_LOGIN_PREFIX + "|" + userId + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.size() < 1) {
            return;
        }
        String key = keys.toArray(new String[0])[0];
        redisTemplate.expire(key, 60, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }
}
