package cn.rt.common.common;

/**
 * @author ruanting
 * @date 2019/10/11
 */
public class Constants {

    //应答对象的状态
    public static final String RESP_SUCCESS = "success";
    public static final String RESP_FAIL = "fail";

    //redis存储登陆信息前缀
    public static final String REDIS_LOGIN_PREFIX = "rim-login";

    //消息类型-私聊
    public static final String MSG_PERSONAL = "01";
    //消息类型-群聊
    public static final String MSG_GROUP = "02";

    //route调用server的头信息中权限密钥的字段名
    public static final String HEAN_AUTH = "authorization";

    public enum User {
        //用户在线状态码
        USER_LOGIN_STATE_OFF("00"),
        USER_LOGIN_STATE_ON("01");

        private String value;

        User(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum ServerConntCode {
        //成功
        SUCCESS("00");

        private String code;

        ServerConntCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

}
