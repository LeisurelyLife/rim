package cn.rt.route.util;

import cn.hutool.core.util.RandomUtil;
import cn.rt.common.common.Constants;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

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

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String getRandomServer() {
        String[] servers = serverCash.values().toArray(new String[0]);
        if (servers.length < 1) {
            return null;
        }
        int i = RandomUtil.randomInt(servers.length);
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
        log.info("当前服务器列表为" + values);
        values.removeAll(serverList);
        ArrayList<String> clearServer = new ArrayList<>();
        clearServer.addAll(values);
        log.info("下线的server为" + clearServer);
        serverCash.clear();
        for (String server : serverList) {
            serverCash.put(server, server);
        }
        log.info("更新后服务器列表为" + serverCash.values());

        //因为route是水平扩展的，所以这个后期应该抽离出来单独做个服务来处理server宕机清除对应下线
        for (String serverAddr : clearServer) {
            clearServerRedis(serverAddr);
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

    public void clearServerRedis(String serverAddr) {
        String pattern = Constants.REDIS_LOGIN_PREFIX + "|*|" + serverAddr + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
