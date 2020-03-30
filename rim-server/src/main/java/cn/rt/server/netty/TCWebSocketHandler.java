package cn.rt.server.netty;

/**
 * 接收/处理/响应客户端websocket请求的核心业务处理类
 * 通过添加hanlder，我们可以监听Channel的各种动作以及状态的改变，包括连接，绑定，接收消息等。
 *
 *  Timely communication
 */
//public class TCWebSocketHandler extends SimpleChannelInboundHandler<Object> {
//
//
//    // 用于服务器端web套接字打开和关闭握手
//    private WebSocketServerHandshaker handshaker;
//
//    private static final String WEB_SOCKET_URL = "/websocket";
//
//
//    //客户端与服务端创建连接的时候调用
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        TCChannelManage.group.add(ctx.channel());
//        System.out.println("客户端与服务端连接开启，客户端remoteAddress：" + ctx.channel().remoteAddress());
//    }
//
//    //客户端与服务端断开连接的时候调用
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        TCChannelManage.group.remove(ctx.channel());
//        System.out.println("客户端与服务端连接关闭...");
//    }
//
//    //服务端接收客户端发送过来的数据结束之后调用
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.flush();
//    }
//
//    //工程出现异常的时候调用
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        cause.printStackTrace();
//        ctx.close();
//    }
//
//    //服务端处理客户端websocket请求的核心方法
//    protected void messageReceived(ChannelHandlerContext context, Object msg) throws Exception {
//
//        /* 传统的HTTP接入（采用http处理方式）
//         * 第一次握手请求消息由HTTP协议承载，所以它是一个HTTP消息，
//         * 握手成功后，数据就直接从 TCP 通道传输，与 HTTP 无关了。
//         * 执行handleHttpRequest方法来处理WebSocket握手请求。
//         */
//
//        // FullHttpRequest是完整的 HTTP请求，协议头和Form数据是在一起的，不用分开读
//        if (msg instanceof FullHttpRequest) {
//            handHttpRequest(context, (FullHttpRequest) msg);
//        }
//        /**
//         *  WebSocket接入（采用socket处理方式）
//         *  提交请求消息给服务端，
//         *  WebSocketServerHandler接收到的是已经解码后的WebSocketFrame消息。
//         */
//        else if (msg instanceof WebSocketFrame) {
//            handWebsocketFrame(context, (WebSocketFrame) msg);
//        }
//        /**
//         * Websocket的数据传输是frame形式传输的，比如会将一条消息分为几个frame，按照先后顺序传输出去。这样做会有几个好处：
//         *
//         * 1）大数据的传输可以分片传输，不用考虑到数据大小导致的长度标志位不足够的情况。
//         *
//         * 2）和http的chunk一样，可以边生成数据边传递消息，即提高传输效率。
//         */
//    }
//
//    /**
//     * 处理客户端与服务端之前的websocket业务
//     *
//     * @param ctx
//     * @param frame
//     */
//    private void handWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
//
//        //判断是否是关闭websocket的指令
//        if (frame instanceof CloseWebSocketFrame) {
//            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
//        }
//
//        //判断是否是ping消息
//        if (frame instanceof PingWebSocketFrame) {
//            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
//            return;
//        }
//
//        if (frame instanceof TextWebSocketFrame) {
//
//            // 返回应答消息
//            String requestMsg = ((TextWebSocketFrame) frame).text();
//            System.out.println("收到客户端" + ctx.channel().remoteAddress() + "的消息==》" + requestMsg);
//            String[] array = requestMsg.split(",");
//            // 先判断通道管理器中是否存在该通道，没有则添加进去
//            if (!TCChannelManage.hasChannel(array[0])) {
//                TCChannelManage.userIdAndChannelMap.put(array[0], ctx.channel());
//            }
//
//            if (array[0].length() != 0 && array[1].length() != 0) {
//                TCChannelManage.send(array[0], array[1], array[2], ctx.channel());
//            } else if (array[0].length() != 0 && array[1].length() == 0) {
//                //如果没有指定接收者表示群发array.length() = 2
//                System.out.println("用户" + array[0] + "群发了一条消息：" + array[2]);
//                TCChannelManage.group.writeAndFlush(new TextWebSocketFrame("用户" + array[0] + "群发了一条消息：" + array[2]));
//            } else {
//                //如果没有指定发送者与接收者表示向服务端发送array.length() = 1
//                System.out.println("服务端接收用户" + ctx.channel().remoteAddress() + "消息，不再发送出去");
//                ctx.writeAndFlush(new TextWebSocketFrame("你向服务端发送了消息==》" + array[2]));
//            }
//        }
//    }
//
//
//    /**
//     * 处理客户端向服务端发起http握手请求的业务
//     *
//     * @param ctx
//     * @param req
//     */
//    private void handHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
//        System.out.println("处理http请求，http方法==>>" + req.getMethod() + ",http地址==>>" + req.getUri());
//        Map<String, String> parmMap = new HashMap<>();
//        try {
//            parmMap = parse(req);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 如果不是WebSocket握手请求消息，那么就返回 HTTP 400 BAD REQUEST 响应给客户端。
//        if (!req.getDecoderResult().isSuccess()
//                || !("websocket".equals(req.headers().get("Upgrade")))) {
//            sendHttpResponse(ctx, req,
//                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
//            return;
//        }
//        //如果是握手请求，那么就进行握手
//        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
//                WEB_SOCKET_URL, null, false);
//        handshaker = wsFactory.newHandshaker(req);
//        if (handshaker == null) {
//            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
//        } else {
//            // 通过它构造握手响应消息返回给客户端，
//            // 同时将WebSocket相关的编码和解码类动态添加到ChannelPipeline中，用于WebSocket消息的编解码，
//            // 添加WebSocketEncoder和WebSocketDecoder之后，服务端就可以自动对WebSocket消息进行编解码了
//            handshaker.handshake(ctx.channel(), req);
//        }
//    }
//
//    /**
//     * 服务端向客户端响应消息
//     *
//     * @param ctx
//     * @param req
//     * @param res
//     */
//    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req,
//                                  DefaultFullHttpResponse res) {
//        // 返回应答给客户端
//        if (res.getStatus().code() != 200) {
//            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
//            res.content().writeBytes(buf);
//            buf.release();
//        }
//        // 如果是非Keep-Alive，关闭连接
//        ChannelFuture f = ctx.channel().writeAndFlush(res);
//        if (res.getStatus().code() != 200) {
//            f.addListener(ChannelFutureListener.CLOSE);
//        }
//    }
//
//    /**
//     * 解析GET、POST请求参数
//     * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
//     *
//     * @throws IOException
//     */
//    public Map<String, String> parse(FullHttpRequest fullReq) throws IOException {
//
//        HttpMethod method = fullReq.getMethod();
//
//        Map<String, String> parmMap = new HashMap<>();
//
//        if (HttpMethod.GET == method) {
//            // 是GET请求
//            QueryStringDecoder decoder = new QueryStringDecoder(fullReq.getUri());
//            decoder.parameters().entrySet().forEach( entry -> {
//                // entry.getValue()是一个List, 只取第一个元素
//                parmMap.put(entry.getKey(), entry.getValue().get(0));
//            });
//        } else if (HttpMethod.POST == method) {
//            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(
//                    new DefaultHttpDataFactory(false), fullReq);
//            List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();
//            for(InterfaceHttpData data:postData){
//                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
//                    MemoryAttribute attribute = (MemoryAttribute) data;
//                    parmMap.put(attribute.getName(), attribute.getValue());
//                }
//            }
//        } else {
//            // 不支持其它方法
//            System.out.println("不支持其他方法提交的参数");
//        }
//
//        return parmMap;
//    }
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext arg0, Object arg1)
//            throws Exception {
//        messageReceived(arg0,arg1);
//    }
//}