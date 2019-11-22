package cn.rt.server.service.impl;

import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.RIMProtocol;
import cn.rt.server.netty.RIMServer;
import cn.rt.server.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ruanting
 * @date 2019/11/21
 */
@Service
public class MsgServiceImpl implements MsgService {

    @Autowired
    private RIMServer rimServer;

    @Override
    public BaseResponse sendMsg(RIMProtocol.ServerSendMsg msg) {
        BaseResponse sendResp = rimServer.sendMsg(msg);
        return sendResp;
    }

}
