package cn.rt.route.service;

import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.RIMProtocol;

/**
 * @author ruanting
 * @date 2019/11/21
 */
public interface MsgService {

    BaseResponse sendMsg(RIMProtocol.RouteSendMsg msg);

}
