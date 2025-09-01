package com.cuzz.webac.netty.handler;

import com.cuzz.webac.bot.bot.BotAPI;
import com.cuzz.webac.bot.objects.message.GeneralMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    //通道缓存
    private static final Map<String, Channel> allClients = new ConcurrentHashMap<>();

    private static final AtomicInteger recTask = new AtomicInteger(0);
    private static final AtomicInteger sendTask = new AtomicInteger(0);
    private final ConcurrentHashMap<UUID, CompletableFuture<JsonObject>> responseMap = new ConcurrentHashMap<>();
    public Channel channel;
    public  String clientName;

    //快速
//    public  void processMessageRequest(String message){
//
//        Gson gson = new Gson();
//        JsonObject object = gson.fromJson(message, JsonObject.class);
//        //路由
//        if (object.has("type")){
//            JsonElement type= object.get("type");
//
//            if (type.getAsString().equalsIgnoreCase("airequest")){
//                System.out.println("airequest");
//                Channel aiserver = serverClients.get("aiserver");
//                aiserver.writeAndFlush(object);
//                return;
//            }
//            if (type.getAsString().equalsIgnoreCase("airesponse")){
//                System.out.println("airesponse");
//
//                botAPI.sendGroupMsg(object.get("groupId").getAsLong(),object.get("content").getAsString());
//                return;
//            }
//        }
//
//
//    }


//    public  void forkBranch(String message){
//        new Thread(()->{
//
//            //处理消息是否为响应
//            processMessageRec(message);
//
//
//        }).start();
//
//        new Thread(()->{
//
//            //查看消息是否为请求
//            processMessageRequest(message);
//
//
//        }).start();
//
//        Gson gson = new Gson();
//        JsonObject rawMessage = gson.fromJson(message, JsonObject.class);
//        if (rawMessage.has("raw_message")){
//            System.out.println(rawMessage);
//            JsonElement jsonElement = rawMessage.get("raw_message");
//            System.out.println(jsonElement.toString());
//            if (jsonElement.toString().contains("[CQ:at,qq=3882996485,name=")){
//                Thread thread1 = new Thread(()->{
//                    GeneralMessage generalMessage = new GeneralMessage(JsonObject);
//                    Pattern pattern = Pattern.compile("(?<=name=)[^,]]+");
//                    Matcher matcher = pattern.matcher(jsonElement.getAsString());
//
//                    if (matcher.find()) {
//                        aiMessage.setRole(matcher.group());
//                    }
//                    JsonArray plainMsgList = rawMessage.getAsJsonArray("message");
//                    StringBuilder stringBuilder = new StringBuilder();
//
//                    aiMessage.setGroupId(rawMessage.get("group_id").getAsLong());
//                    aiMessage.setUserId(rawMessage.get("user_id").getAsLong());
//                    plainMsgList.forEach(
//                            str->{
//                                if (str.toString().contains("\"type\":\"text\",\"data\"")){
//                                    // 解析 JSON
//                                    JsonObject jsonObject = gson.fromJson(str, JsonObject.class);
//
//                                    // 获取 data 对象
//                                    JsonObject dataObject = jsonObject.getAsJsonObject("data");
//
//                                    // 提取 text 字段的值
//                                    String text = dataObject.get("text").getAsString();
//                                    stringBuilder.append(text);
//                                }
//
//                            }
//
//                    );
//                    aiMessage.setContent(stringBuilder.toString());
//                    System.out.println("aidebug");
//                    Channel aiserver = serverClients.get("aiserver");
//                    aiserver.writeAndFlush(new TextWebSocketFrame(gson.toJson(aiMessage)));
//                });
//                thread1.start();
//
//            }
//        }
//    }
//
//    public void deliverMessage(JsonObject object){
//        JsonElement type= object.get("type");
//        //代表消息要转发给 ai服务
//        if (type.getAsString().equalsIgnoreCase("airequest")){
//            System.out.println("airequest");
//            Channel aiserver = serverClients.get("aiserver");
//            aiserver.writeAndFlush(object);
//            return;
//        }
//        //代表消息要转发到对应的请求发起者
//        if (type.getAsString().equalsIgnoreCase("airesponse")){
//            System.out.println("airesponse");
//            String reciver = object.get("reciver").getAsString();
//
//            //发到qq
//            if(reciver.contains("@")){
//                String[] split = reciver.split("@");
//                botAPI.sendGroupMsg(Long.valueOf(split[0]),object.get("content").getAsString());
//                return;
//            }
//            //发到其他后端
//            if (reciver.contains("#")){
//                //TO DO
//            }
//
//
//            return;
//        }
//
//
//    }

//    public void  sendHeartBeat(){
//        String heartbeatMessage = "{\"type\":\"heartbeat\"}";
//        // 启动心跳定时器，定期发送心跳消息
//        Timer heartbeatTimer = new Timer(true);
//        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (isOpen()) {
//                    sendHeartbeat();  // 发送心跳消息
//                }
//            }
//        }, 0, 50000);  // 每 5 秒发送一次心跳
//
//        serverClients.values().forEach(channel -> {
//            channel.writeAndFlush(heartbeatMessage);
//        });
//        System.out.println("💓 发送心跳消息");
//    }
//    public void processMessageRec(String msg) {
//        Gson gson = new Gson();
//        JsonObject object = gson.fromJson(msg, JsonObject.class);
//        if (true) {
//            System.out.println("§a(DEBUG): 收到信息: " + object);
//        }
//
//        if (object.has("post_type")) {
//            System.out.println("@@@@@@@@@has post_type");
//        }
//        if (object.has("echo")) {
//            UUID uuid = UUID.fromString(object.get("echo").getAsString());
//            CompletableFuture<JsonObject> response = responseMap.get(uuid);
//            if (response != null) {
//                response.complete(object);
//            }
//        }
//    }


    public JsonObject processMessageSend(JsonObject object, int timeout) {


        UUID uuid = UUID.randomUUID();
        //打上 echo标记
        object.addProperty("echo", uuid.toString());
        CompletableFuture<JsonObject> response = new CompletableFuture<>();
        responseMap.put(uuid, response);
        String msg = object.toString();
        this.send(msg);
        if (true) {
//            int debugLength = instance.getConfig().getInt("debug_message_max_length");
            if (msg.length() > 100) {
                msg = msg.substring(0, 100) + "......(省略" + msg.length() + "字符)";
            }
//            System.out.println("§a(DEBUG): 发送信息: " + msg);
        }
        //调用事件完成
        try {
            return response.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            responseMap.remove(uuid);
        }
        return null;
    }


    //发送到QQ
    public void send(String str) {
        if (allClients.isEmpty()){
            System.out.println("当前没有qq链接");
            return ;
        }
        Channel qqClient = allClients.get("*qq");
        qqClient.writeAndFlush(new TextWebSocketFrame(str));
        return ;
    }

    public JsonObject sendWithResponse(JsonObject object, int timeout,String clientName) throws ExecutionException, InterruptedException, TimeoutException {


        UUID uuid = UUID.randomUUID();
        //打上 echo标记
        object.addProperty("echo", uuid.toString());
        CompletableFuture<JsonObject> completableFuture = new CompletableFuture<>();
        responseMap.put(uuid, completableFuture);

        Channel target = allClients.get(clientName);
        target.writeAndFlush(new TextWebSocketFrame(object.getAsString()));
        JsonObject jsonObject = completableFuture.get(timeout, TimeUnit.SECONDS);
        return jsonObject;
    }


    public void sendOneWay(JsonObject object,String clientName){
        String jsonString = new Gson().toJson(object);

        Channel target = allClients.get(clientName);
        target.writeAndFlush(new TextWebSocketFrame(jsonString));
    }
    public void sendOneWay(String string,String clientName){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(string, JsonObject.class);
        sendOneWay(jsonObject,clientName);
    }

    public JsonObject send(JsonObject object, int timeout) {

        sendTask.incrementAndGet();
        try {
            return processMessageSend(object, timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            sendTask.decrementAndGet();
        }

//        int sendTip = instance.getConfig().getInt("main.send_task_tip");
        if (sendTask.get() > 1000) {
            System.out.println("检测到发送信息最大任务数已超过" + 1000 + ",可能有插件处理发送事件过于缓慢...");
        }
        return null;
    }














    private static final  Map<String, Channel> serverClients =new ConcurrentHashMap<>();

    private WebSocketServerHandshaker handshaker;
    // 允许的 WebSocket 握手 URI
    private static final String WEBSOCKET_PATH_PREFIX = "/web";
    static BotAPI botAPI=null;
    public NettyWebSocketServerHandler(){
        botAPI=new BotAPI(this);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {


        // 处理 HTTP 升级为 WebSocket 请求
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
        // 处理 WebSocket 请求
        else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }



    // 处理 HTTP 请求，进行 WebSocket 升级
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 使用路径前缀匹配
        if (!req.uri().startsWith(WEBSOCKET_PATH_PREFIX)) {
            // 如果 URI 不匹配，返回 404 错误
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
            return;
        }


        if (!req.decoderResult().isSuccess() || !"websocket".equals(req.headers().get("Upgrade"))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //密钥检测
        if (!req.uri().contains("botpwd")){
            return;
        }


        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://" + req.headers().get(HttpHeaderNames.HOST) + req.uri(), null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }

        //客户端注册
        if (req.uri().contains("client")){
            Pattern pattern = Pattern.compile("(?<=client=)[^&]+");
            Matcher matcher = pattern.matcher(req.uri());

            if (matcher.find()) {
                String name = matcher.group();
                System.out.println("客户端注册成功: " + name);
                clientName=name;
                allClients.put(clientName, ctx.channel());
//                ctx.channel().writeAndFlush(new TextWebSocketFrame("你好"));
            }


        }

    }

    // 处理 WebSocket 帧
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 关闭请求
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
            return;
        }

        // Ping 请求
        if (frame instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
//            System.out.println("响应pong");
            return;
        }

        // 文本消息
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException("Unsupported frame type: " + frame.getClass().getName());
        }




        // 广播消息给所有客户端
        String request = ((TextWebSocketFrame) frame).text();
        //人工 心跳处理
//        if(request.contains("heartbeat")){
//            ctx.write(new PongWebSocketFrame(frame.content().retain()));
//            return;
//        }

        messageHandler(request);


    }
    public void processQQMessageRec(String msg) {
        Gson gson = new Gson();
        JsonObject object = gson.fromJson(msg, JsonObject.class);

        if (true) {
//            System.out.println("§a(DEBUG): 收到信息: " + object);
        }

        if (object.has("post_type")) {
            System.out.println("post_type");
        }
        //响应消息
        if (object.has("echo")) {
            UUID uuid = UUID.fromString(object.get("echo").getAsString());
            CompletableFuture<JsonObject> response = responseMap.get(uuid);
            if (response != null) {
                response.complete(object);
            }
        }
//        if (object.has("message_type")){
//            String messageType = object.get("message_type").getAsString();
//            if (!messageType.equalsIgnoreCase("group")){
//                return;
//            }
//        }
        if (object.has("raw_message")){
            GeneralMessage generalMessage = new GeneralMessage(object);
            if (generalMessage.getType().equalsIgnoreCase("airequest")){
                Channel aiClient = allClients.get("*ai");
                String json = gson.toJson(generalMessage);
                System.out.println(json);
                aiClient.writeAndFlush(new TextWebSocketFrame(json));
                System.out.println("发送请求到 ai端"+aiClient);
            }

        }
    }



    public void processAiMessageRec(String msg){
        Gson gson = new Gson();
        GeneralMessage generalMessage  = gson.fromJson(msg, GeneralMessage.class);
        //不解析直接发到mc服务端;
        if (generalMessage.getReciver().contains("*mc")){

            String reciver = generalMessage.getReciver();
            String[] reciverFull = reciver.split("#");
            System.out.println(reciver);
            Channel target = allClients.get(reciverFull[0]);

            target.writeAndFlush(msg);
//            System.out.println("发送"+reciver+msg);
            return;
        }
        //解析再转发 发到qq客户端;
        if (generalMessage.getReciver().contains("@")){
            String reciver = generalMessage.getReciver();
            String[] reciverFull = reciver.split("@");
            System.out.println(reciver+ generalMessage.getContent());

            botAPI.sendGroupMsg(Long.valueOf(reciverFull[0]),generalMessage.getContent());
            return;
        }








    }
    public void messageHandler(String msg){
        //消息来自QQ客户端 说明要解析或直接再转发到其他客户端(例如ai)
        if (this.clientName.contains("*qq")){
            recTask.incrementAndGet();
            new Thread(()->{
                try {
//                    System.out.println("这是一条来自QQ的消息");
                    processQQMessageRec(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    recTask.decrementAndGet();
                }

            }).start();

            return;
        }

        //消息来自ai模型客户端  说明这是一条响应消息需要解析或直接再转发到其他客户端(例如qq 或者bukkit服务端)
        if (this.clientName.contains("*ai")){
//            System.out.println("这是一条来自ai服务的消息: "+msg);
            new Thread(()->{
                processAiMessageRec(msg);



            }).start();

            return;
        }

        //消息来自 mc游戏端 说明这是一条需要解析转发消息
        if (this.clientName.contains("*mc")){
//            System.out.println("这是一条来自mc的消息");
            return;

        }





    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("我是！！！！！");
//        allClients.put(this.clientName,ctx.channel());
        channel=ctx.channel();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当连接断开时，移除客户端
        channel=null;
        allClients.remove(ctx.channel().id().asShortText());
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        if (res.status().code() != 200) {
            res.content().writeBytes(res.status().toString().getBytes());
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }



}
