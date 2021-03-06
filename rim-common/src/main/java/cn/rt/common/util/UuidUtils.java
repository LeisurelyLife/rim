package cn.rt.common.util;

import java.util.UUID;

/**
 * About:UUID生成工具类
 * Other:
 * Created: jyhuang on 2016/6/17 14:31.
 * Editored:
 */
public class UuidUtils {
    /**
     * 获取随机的UUID字符串
     * @param
     * @return uuid
     * @throws
     */
    public static String getRandomUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().toLowerCase();
    }

    /**
     * 从UUID字符串中去除“-”分隔符
     * @param uuid
     * @return 不含"-"的uuid
     * @throws
     */
    public static String removeSeparatorFromUuid(String uuid) {
        uuid = uuid.replaceAll("-","");
        return uuid;
    }

    /**
     * 获取不包含“-”分隔符的uuid字符串
     * @param
     * @return 不含"-"的uuid
     * @throws
     */
    public static String getRandomUuidWithoutSeparator() {
        String uuid = getRandomUuid();
        return removeSeparatorFromUuid(uuid);
    }

}
