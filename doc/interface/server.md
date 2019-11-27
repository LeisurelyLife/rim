#### 服务端接口文档

##### 发送消息-server
	/sendMsg
	请求：{
		"type":"str"					//01 私聊 02群聊（未开放）
		"userId":"str"					//发送用户ID
		"targetUserId":"str"			//目标用户ID
		"targetGroupId":"str"			//目标群聊ID
		"msg":"str"						//消息
	}
	响应：{
		"state":"str"					//状态
		"msg":"str"						//消息
		"data":""	
	}
	
##### 连接server
	请求：{
		"userId":"str"					//用户ID
	}
	响应：{
		"code":"str"					//状态码，00成功，其他见文档
		"msg":"str"						//
	}
	
##### 收到消息
	{
		"type":"str"					//01 私聊 02群聊（未开放）	
		"userId":"str"					//消息所属用户ID
		"groupId":"str"					//消息所属群聊ID
		"time":"num"					//时间戳
		"msg":"str"						//消息
	}