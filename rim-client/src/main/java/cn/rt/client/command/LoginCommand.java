package cn.rt.client.command;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.rt.client.RimClient;
import cn.rt.client.netty.LoginReceiveHandler;
import cn.rt.client.netty.LoginRequestHandler;
import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.Constants;
import cn.rt.common.entity.UserAccount;
import cn.rt.common.netty.handler.PacketDecoder;
import cn.rt.common.netty.handler.PacketEncoder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ruanting
 * @date 2021/6/22
 */
public class LoginCommand implements Command {
    /**
     * 登录接口路径
     */
    private static final String LOGIN_PATH = "/login";

    private static final int MAX_RETRY = 5;

    boolean flag = true;

    private String userAccount;
    private String password;

    @Override
    public void doCommand() {
        Scanner sc = new Scanner(System.in);

        while (flag) {
            if (StringUtils.isEmpty(userAccount)) {
                System.out.println("请输入账号：");
                userAccount = sc.nextLine();
                continue;
            }
            if (StringUtils.isEmpty(password)) {
                System.out.println("请输入密码：");
                password = sc.nextLine();
                continue;
            }

            if (login()) {
                flag = false;
            } else {
                System.out.println("登录失败，请重新登录");
                userAccount = null;
                password = null;
            }
        }
    }

    private boolean login() {
        String requestUrl = RimClient.ROUTE_ADDR + LOGIN_PATH;
        UserAccount userAccount = new UserAccount();
        userAccount.setUserAccount(this.userAccount);
        userAccount.setUserPassword(password);
        HttpRequest post = HttpUtil.createPost(requestUrl);
        post.body(JSON.toJSONString(userAccount));
        HttpResponse execute = post.execute();
        String body = execute.body();
        BaseResponse baseResponse = JSON.parseObject(body, BaseResponse.class);
        System.out.println("baseResponse:" + baseResponse);
        if (Constants.RESP_FAIL.equals(baseResponse.getState())) {
            return false;
        }
        connectServer(baseResponse);
        return true;
    }

    private boolean connectServer(BaseResponse baseResponse) {
        JSONObject data = JSON.parseObject(JSON.toJSONString(baseResponse.getData()));
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                // 1.指定线程模型
                .group(workerGroup)
                // 2.指定 IO 类型为 NIO
                .channel(NioSocketChannel.class)
                // 3.IO 处理逻辑
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new LoginRequestHandler(data.getString("userId"), data.getString("token")));
                        ch.pipeline().addLast(new LoginReceiveHandler());
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });
        // 4.建立连接
        data.put("socketServer", "localhost");
        boolean connect = connect(bootstrap, data.getString("socketServer"), data.getIntValue("socketPort"), MAX_RETRY);
        if (connect) {
            RimClient.cachName = data.getString("userName");
        }
        return connect;
    }

    private static boolean connect(Bootstrap bootstrap, String host, int port, int retry) {
        AtomicBoolean connect = new AtomicBoolean(false);
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("连接服务器成功!");
                connect.set(true);
            } else if (retry == 0) {
                System.err.println("重试次数已用完，放弃连接！");
                connect.set(false);
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit
                        .SECONDS);
            }
        });
        return connect.get();
    }
}
