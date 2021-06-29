package cn.rt.common.netty.message;

/**
 * @author ruanting
 * @date 2021/6/28
 */
public interface ResponseMessageCode {
    /**
     * 成功
     */
    Integer SUCCESS = 1001;
    /**
     * token错误
     */
    Integer TOKEN_MISTAKE = 1002;
    /**
     * 服务器分配IP校验错误
     */
    Integer SERVER_IP_MISTAKE = 1003;
    /**
     * 用户未登录
     */
    Integer NOT_LOGIN = 1004;
}
