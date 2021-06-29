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
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author ruanting
 * @date 2021/6/22
 */
@Slf4j
public class LoginCommand implements Command {
    /**
     * 登录接口路径
     */
    private static final String LOGIN_PATH = "/login";

    /**
     * 连接server重试最大次数
     */
    private static final int MAX_RETRY = 5;

    /**
     * 完成连接server尝试
     */
    private static boolean finishTryConnect = false;

    /**
     * 是否连接server成功
     */
    private static boolean connectSuccess = false;

    boolean flag = true;

    private String userAccount;
    private String password;

    @Override
    public void doCommand() {
        Scanner sc = new Scanner(System.in);
        if (RimClient.isLogin) {
            System.out.println("用户已登录！");
            return;
        }

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
                RimClient.cacheName = "";
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
        JSONObject data = JSON.parseObject(JSON.toJSONString(baseResponse.getData()));
        RimClient.cacheName = data.getString("userName");
        return connectServer(data);
    }

    private boolean connectServer(JSONObject data) {
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
        // 4.建立连接 todo
        data.put("socketServer", "localhost");
        connect(bootstrap, data.getString("socketServer"), data.getIntValue("socketPort"), MAX_RETRY);
        while (!finishTryConnect) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("线程睡眠出错：", e);
            }
        }
        return connectSuccess;
    }

    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                connectSuccess = true;
                finishTryConnect = true;
                System.out.println("连接服务器成功!");
            } else if (retry == 0) {
                System.err.println("重试次数已用完，放弃连接！");
                finishTryConnect = true;
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
    }
}
