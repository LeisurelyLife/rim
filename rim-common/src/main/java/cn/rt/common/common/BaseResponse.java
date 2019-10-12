package cn.rt.common.common;

/**
 * @author ruanting
 * @date 2019/10/11
 */
public class BaseResponse {

    /**
     * 返回应答码 成功："success"，失败："fail"，已定义常量
     */
    private String state = Constants.RESP_FAIL;

    /**
     * 返回应答信息
     */
    private String msg;

    /**
     * 返回数据域
     */
    private Object data;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
