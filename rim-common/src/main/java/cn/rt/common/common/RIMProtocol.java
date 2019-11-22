package cn.rt.common.common;

import lombok.Data;

/**
 * @author ruanting
 * @date 2019/11/21
 */
public class RIMProtocol {

    @Data
    public static class ReqServerProtocol {
        private String userId;
    }

    @Data
    public static class RespServerProtocol {
        private int code;
        private String msg;
    }

    @Data
    public static class ServerSendMsg {
        private String type;
        private String userId;
        private String targetUserId;
        private String targetGroupId;
        private String msg;
    }

    @Data
    public static class RouteSendMsg {
        private String token;
        private String type;
        private String userId;
        private String targetId;
        private String msg;
    }

}
