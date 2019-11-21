package cn.rt.route.util;

import cn.hutool.core.util.RandomUtil;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author ruanting
 * @date 2019/11/20
 */
@Component
public class ZKModule {

    private static final Logger log = LoggerFactory.getLogger(ZKModule.class);

    private static final HashMap<String, String> serverCash = new HashMap();

    @Value("${app.zk.root}")
    private String zkRoot;

    @Autowired
    private ZkClient zkClient;

    public static String getRandomServer() {
        String[] servers = serverCash.values().toArray(new String[0]);
        int i = RandomUtil.randomInt(servers.length - 1);
        return servers[i];
    }

    public void subscribeEvent() {
        initCash();
        zkClient.subscribeChildChanges(zkRoot, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                log.info("清除/更新本地缓存 parentPath=【{}】,currentChilds=【{}】", parentPath, currentChilds.toString());
                //更新所有缓存/先删除 再新增
                updateCache(currentChilds);
            }
        });
    }

    public void updateCache(List<String> serverList) {
        Collection<String> values = serverCash.values();
        values.removeAll(serverList);
        ArrayList<String> clearServer = new ArrayList<>();
        clearServer.addAll(values);
        log.info("下线的server为" + clearServer);
        serverCash.clear();
        for (String server : serverList) {
            serverCash.put(server, server);
        }
    }

    public void initCash() {
        serverCash.clear();
        List<String> children = zkClient.getChildren(zkRoot);
        for (String child : children) {
            serverCash.put(child, child);
        }
        log.info("初始化server" + serverCash.values());
    }

}
