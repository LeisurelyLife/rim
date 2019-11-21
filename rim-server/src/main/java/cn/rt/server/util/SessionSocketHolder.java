package cn.rt.server.util;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ruanting
 * @date 2019/11/19
 */
public class SessionSocketHolder {

    private static final Map<String, NioSocketChannel> SESSION_MAP = new ConcurrentHashMap();

    public static void setUserChannel(String userId, NioSocketChannel channel) {
        SESSION_MAP.put(userId, channel);
    }

    public static NioSocketChannel getUserChannel(String userId) {
        NioSocketChannel channel = SESSION_MAP.get(userId);
        return channel;
    }

    public static void moveChannel(NioSocketChannel channel) {
        for (Map.Entry<String, NioSocketChannel> entry : SESSION_MAP.entrySet()) {
            if (entry.getValue() == channel) {
                SESSION_MAP.remove(entry.getKey());
            }
        }
    }

}
