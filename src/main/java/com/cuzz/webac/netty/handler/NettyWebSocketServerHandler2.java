package com.cuzz.webac.netty.handler;//package com.cuzz.netty.handler;
//
//import com.cuzz.webac.bot.bot.BotAPI;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import io.netty.channel.*;
//import io.netty.handler.codec.http.*;
//import io.netty.handler.codec.http.websocketx.*;
//
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class NettyWebSocketServerHandler2 extends SimpleChannelInboundHandler<Object> {
//
////    //通道缓存
////    private static final Map<String, Channel> otherClients = new ConcurrentHashMap<>();
////    private static final Map<String,Channel> qqClients = new ConcurrentHashMap<>();
////
////    private static final AtomicInteger recTask = new AtomicInteger(0);
////    private static final AtomicInteger sendTask = new AtomicInteger(0);
////    private final ConcurrentHashMap<UUID, CompletableFuture<JsonObject>> responseMap = new ConcurrentHashMap<>();
////    public Channel channel;
////
////
////
////    public void send(String str) {
////        if (qqClients.isEmpty()){
////            System.out.println("当前没有qq链接");
////            return ;
////        }
////        qqClients.keySet().forEach(
////                channel -> {
////                    channel.writeAndFlush(new TextWebSocketFrame(str));
////                }
////
////        );
////        return ;
////    }
////
////    public JsonObject send(JsonObject object, int timeout) {
////
//////        sendTask.incrementAndGet();
//////        try {
//////            return processMessageSend(object, timeout);
//////        } catch (Exception e) {
//////            e.printStackTrace();
//////        }
//////        sendTask.decrementAndGet();
////////        int sendTip = instance.getConfig().getInt("main.send_task_tip");
//////        if (sendTask.get() > 1000) {
//////            System.out.println("检测到发送信息最大任务数已超过" + 1000 + ",可能有插件处理发送事件过于缓慢...");
//////        }
////        return null;
////    }
////
////
////
////
////
////
////
////
////
////
////
////
////
////
////    private static final  Map<String, Channel> serverClients =new ConcurrentHashMap<>();
////
////    private WebSocketServerHandshaker handshaker;
////    // 允许的 WebSocket 握手 URI
////    private static final String WEBSOCKET_PATH_PREFIX = "/web";
////    static BotAPI botAPI=null;
////    public NettyWebSocketServerHandler2(){
////        botAPI=new BotAPI(this);
////    }
////
////    @Override
////    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
////
////
////        // 处理 HTTP 升级为 WebSocket 请求
////        if (msg instanceof FullHttpRequest) {
////            handleHttpRequest(ctx, (FullHttpRequest) msg);
////        }
////        // 处理 WebSocket 请求
////        else if (msg instanceof WebSocketFrame) {
////            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
////        }
////    }
////
////
////
////    // 处理 HTTP 请求，进行 WebSocket 升级
////    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
////        // 使用路径前缀匹配
////        if (!req.uri().startsWith(WEBSOCKET_PATH_PREFIX)) {
////            // 如果 URI 不匹配，返回 404 错误
////            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
////            return;
////        }
////
////        System.out.println(req.uri());
////        if (!req.decoderResult().isSuccess() || !"websocket".equals(req.headers().get("Upgrade"))) {
////            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
////            return;
////        }
////
////
////
////        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
////                "ws://" + req.headers().get(HttpHeaderNames.HOST) + req.uri(), null, true);
////        handshaker = wsFactory.newHandshaker(req);
////        if (handshaker == null) {
////            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
////        } else {
////            handshaker.handshake(ctx.channel(), req);
////        }
////
////
////        //客户端注册
////        if (req.uri().contains("client")){
////            Pattern pattern = Pattern.compile("(?<=client=)[^&]+");
////            Matcher matcher = pattern.matcher(req.uri());
////
////            if (matcher.find()) {
////                System.out.println("客户端注册成功: " + matcher.group());
////                otherClients.put(matcher.group(),ctx.channel());
////            }
////
////
////        }
////
////    }
////
////    // 处理 WebSocket 帧
////    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
////        // 关闭请求
////        if (frame instanceof CloseWebSocketFrame) {
////            handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
////            return;
////        }
////
////        // Ping 请求
////        if (frame instanceof PingWebSocketFrame) {
////            ctx.write(new PongWebSocketFrame(frame.content().retain()));
////            System.out.println("发来一个ping");
////            return;
////        }
////
////        // 文本消息
////        if (!(frame instanceof TextWebSocketFrame)) {
////            throw new UnsupportedOperationException("Unsupported frame type: " + frame.getClass().getName());
////        }
////        if ("")
////
////
////        // 广播消息给所有客户端
////        String request = ((TextWebSocketFrame) frame).text();
////        if (request.contains("lifecycle")){
////            return;
////        }
////
////        System.out.println(request);
////
////    }
////
////    @Override
////    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
////        // 将新加入的客户端连接添加到客户端集合中
////        otherClients.put(ctx.channel().id().asShortText(), ctx.channel());
////
////        System.out.println(otherClients.size());
////
////    }
////
////    @Override
////    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
////        // 当连接断开时，移除客户端
////        otherClients.remove(ctx.channel().id().asShortText());
////        qqClients.remove(ctx.channel());
////    }
////
////    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
////        if (res.status().code() != 200) {
////            res.content().writeBytes(res.status().toString().getBytes());
////            HttpUtil.setContentLength(res, res.content().readableBytes());
////        }
////        ChannelFuture f = ctx.channel().writeAndFlush(res);
////        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
////            f.addListener(ChannelFutureListener.CLOSE);
////        }
////    }
//
//
//
//}
