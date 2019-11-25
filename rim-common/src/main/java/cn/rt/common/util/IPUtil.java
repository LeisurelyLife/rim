package cn.rt.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author ruanting
 * @date 2019/11/25
 */
public class IPUtil {

    private static final String GET_IP_URL = "http://httpbin.org/ip";

    private static String INTRANET_IP = "";

    private static String INTERNET_IP = "";

    public static String getIntranetIp() {
        try {
            if (StrUtil.isEmpty(INTRANET_IP)) {
                synchronized (INTRANET_IP) {
                    if (StrUtil.isEmpty(INTRANET_IP)) {
                        INTRANET_IP = InetAddress.getLocalHost().getHostAddress();
                    }
                }
            }
            return INTRANET_IP;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return INTRANET_IP;
        }
    }

    public static String getInternetIp() {
        if (StrUtil.isEmpty(INTERNET_IP)) {
            synchronized (INTERNET_IP) {
                if (StrUtil.isEmpty(INTERNET_IP)) {
                    String result = HttpUtil.get(GET_IP_URL);
                    JSONObject ro = new JSONObject(result);
                    String[] addrs = ro.getStr("origin").split(",");
                    INTERNET_IP = addrs[0].trim();
                }
            }
        }
        return INTERNET_IP;
    }

}
