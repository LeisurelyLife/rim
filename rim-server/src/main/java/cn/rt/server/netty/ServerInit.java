package cn.rt.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author ruanting
 * @date 2019/11/19
 */
@Component
public class ServerInit {

    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Value("${socket.port}")
    private Integer port;

    @PostConstruct
    public void init() throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerInitializer())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);


        // 绑定端口，开始接收进来的连接
        ChannelFuture f = b.bind(port).sync();
        if (f.isSuccess()) {
            System.out.println("SimpleChatServer 启动了");
        }
    }

    @PreDestroy
    public void destroy() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

}
