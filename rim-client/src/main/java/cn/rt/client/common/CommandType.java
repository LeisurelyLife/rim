package cn.rt.client.common;

/**
 * @author ruanting
 * @date 2021/6/22
 */
public enum CommandType {
    /**
     * 登录指令
     */
    login("login", "登录");
    private String value;
    private String description;

    CommandType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}
