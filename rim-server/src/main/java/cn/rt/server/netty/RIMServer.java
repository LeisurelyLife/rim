package cn.rt.server.netty;

import cn.hutool.json.JSONObject;
import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.Constants;
import cn.rt.common.common.RIMProtocol;
import cn.rt.server.util.SessionSocketHolder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author ruanting
 * @date 2019/11/19
 */
@Component
public class RIMServer {

    private static final Logger log = LoggerFactory.getLogger(RIMServer.class);

    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Value("${socket.port}")
    private Integer port;

    @PostConstruct
    public void init() throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ServerInitializer());

        // 绑定端口，开始接收进来的连接
        ChannelFuture f = b.bind(port).sync();
        if (f.isSuccess()) {
            log.info("SimpleChatServer 启动了");
        }
    }

    @PreDestroy
    public void destroy() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    public BaseResponse sendMsg(RIMProtocol.ServerSendMsg ssm) {
        BaseResponse response = new BaseResponse();
        NioSocketChannel userChannel = SessionSocketHolder.getUserChannel(ssm.getTargetUserId());
        if (userChannel == null) {
            log.info("channel is null");
            response.setCode(Constants.CODE_FAIL);
            return response;
        }
        JSONObject msg = new JSONObject();
        msg.put("type", ssm.getType());
        msg.put("userId", ssm.getUserId());
        msg.put("groupId", ssm.getTargetGroupId());
        msg.put("time", System.currentTimeMillis());
        msg.put("msg", ssm.getMsg());
        userChannel.writeAndFlush(msg.toString());
        if (true) {
            response.setState(Constants.RESP_SUCCESS);
            response.setCode(Constants.CODE_SUCCESS);
            return response;
        } else {
            log.info("future is fail");
            response.setCode(Constants.CODE_FAIL);
            return response;
        }
    }

}
