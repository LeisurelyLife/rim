package cn.rt.server;

import cn.rt.common.util.IPUtil;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;

/**
 * @author ruanting
 * @date 2019/11/20
 */
@Component
public class ZKRegister {

    private static final Logger log = LoggerFactory.getLogger(ZKRegister.class);

    @Autowired
    private ZkClient zkClient;

    @Value("${app.zk.root}")
    private String zkRoot;

    @Value("${app.zk.addr}")
    private String zkAddr;

    @Value("${socket.port}")
    private String cimServerPort;

    @Value("${server.port}")
    private String httpPort;

    private String ip;

    public ZKRegister() throws UnknownHostException {
        this.ip = IPUtil.getInternetIp();
    }

    public void regist() {
        //创建父节点
        this.createRootNode();

        String path = zkRoot + "/" + ip + ":" + httpPort + ":" + cimServerPort;
        this.createNode(path);
        log.info("注册 zookeeper 成功，msg=[{}]", path);
    }

    public void createRootNode() {
        boolean exists = zkClient.exists(zkRoot);
        if (exists) {
            return;
        }
        zkClient.createPersistent(zkRoot);
    }

    public void createNode(String path) {
        zkClient.createEphemeral(path);
    }

}
