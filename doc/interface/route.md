#### 路由端的接口文档

##### code 定义
	00				操作成功
	01				操作失败			

##### 注册接口
	/register
	请求：{
		"userAccount":"str"				//账号
		"password":"str"				//密码
	}
	响应：{
		"state":"str"					//状态
		"msg":"str"						//消息
		"data":"str"					//数据
		"code":"str"					//状态码
	}
	
##### 登录接口
	/login
	请求：{
		"userAccount":"str"				//账号
		"password":"str"				//密码
	}
	响应：{
		"state":"str"					//状态
		"msg":"str"						//消息
		"code":"str"					//状态码
		"data":{
			"socketServer":"str"		//server服务器ip
			"socketPort":"num"			//server服务器端口
			"token":"str"				//令牌
			"userId":"str"				//登录用户ID
			"userName":"str"			//登录用户名
		}					
	}
	
##### 好友列表
	/friendList
	请求：{
		"token":"str"					//令牌
		"userId":"str"					//登录用户ID
	}
	响应：{
		"state":"str"					//状态
		"msg":"str"						//消息
		"code":"str"					//状态码
		"data":{
			"friendList":[
				{
					"userId":"str"			//好友ID
					"state":"str"			//状态00离线， 01在线
					"userName":"str"		//好友用户名
				}
			]
		}	
	}
	
##### 查询好友
	/searchNum
	请求: {
		"token":"str"				//令牌
		"userId":"str"				//登录用户ID
		"type":"str"				//类型 01用户 02群组
		"number":"str"				//对应的账号或者群号
	}
	响应：{
		"state":"str"					//状态
		"msg":"str"						//消息
		"code":"str"					//状态码
		"data":{
			"type":"str"				//类型 01用户 02群组
			"targetNum":"str"			//用户账号或者群号
			"targetName":"str"			//用户名或者群名
		}	
	}
	
##### 添加好友
	/addFriend
	请求：{
		"token":"str"				//令牌
		"userId":"str"				//登录用户ID
		"targetNum":"str"			//用户账号
	}
	响应：{
		"state":"str"					//状态
		"msg":"str"						//消息
		"data":"str"					//数据
		"code":"str"					//状态码
	}
	
	
##### 发送消息-route
	/sendMsg
	请求：{
		"type":"str"					//01 私聊 02群聊（未开放）	
		"token":"str"					//令牌
		"userId":"str"					//登录用户ID
		"targetId":"str"				//私聊则填用户ID 群聊填群id
		"msg":"str"						//消息
	}
	响应：{
		"state":"str"					//状态
		"msg":"str"						//消息
		"data":""	
		"code":"str"					//状态码
	}