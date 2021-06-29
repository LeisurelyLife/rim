package cn.rt.common.netty.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ruanting
 * @date 2021/6/28
 */
@Getter
@Setter
public abstract class ResponseMessage extends Message {
    public static final Integer RESP_SUCCESS = 1;
    public static final Integer RESP_FAILURE = 0;
    /**
     * 响应编码: 1: 表示成功；其他表示失败
     */
    private Integer respResult = RESP_SUCCESS;

    /**
     * 返回详细描述
     */
    private String respDesc;

    /**
     * 返回编码
     */
    private Integer respCode = ResponseMessageCode.SUCCESS;
}
