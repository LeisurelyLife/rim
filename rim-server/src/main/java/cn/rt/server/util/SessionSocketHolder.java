package cn.rt.server.util;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ruanting
 * @date 2019/11/19
 */
public class SessionSocketHolder {

    private static final Map<String, Channel> SESSION_MAP = new ConcurrentHashMap();

    public static void setUserChannel(String userId, Channel channel) {
        SESSION_MAP.put(userId, channel);
    }

    public static Channel getUserChannel(String userId) {
        Channel channel = SESSION_MAP.get(userId);
        return channel;
    }

    public static String moveChannel(NioSocketChannel channel) {
        String userId = "";
        for (Map.Entry<String, Channel> entry : SESSION_MAP.entrySet()) {
            if (entry.getValue() == channel) {
                userId = entry.getKey();
                SESSION_MAP.remove(entry.getKey());
            }
        }
        return userId;
    }

    public static boolean exist(NioSocketChannel channel) {
        return SESSION_MAP.values().contains(channel);
    }

}
