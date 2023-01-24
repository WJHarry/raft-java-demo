package tech.wjharry.Common.Netty;

import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import tech.wjharry.Common.Singleton;

import java.lang.invoke.WrongMethodTypeException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class NettyServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        System.out.println("class:" + msg.getClass().getName());
        System.out.println(msg.uri());
        QueryStringDecoder queryDecoder = new QueryStringDecoder(msg.uri(), StandardCharsets.UTF_8);

        HttpResponseStatus responseStatus = HttpResponseStatus.OK;
        String responseContent = "";
        switch (queryDecoder.path()) {
            case "/get":
                if (!msg.method().equals(HttpMethod.GET)) {
                    throw new WrongMethodTypeException();
                }
                System.out.println("get:" + queryDecoder.parameters().get("key"));
                Optional<String> keyOptional = queryDecoder.parameters().get("key").stream().findFirst();
                if (keyOptional.isEmpty()) {
                    responseStatus = HttpResponseStatus.BAD_REQUEST;
                    responseContent = "no 'key' in params";
                    break;
                }
                responseContent = Singleton.databaseService.get(keyOptional.get());
                break;
            case "/put":
                if (!msg.method().equals(HttpMethod.PUT)) {
                    throw new WrongMethodTypeException();
                }
                String requestContentType = msg.headers().get("Content-Type").split(";")[0];
                if(requestContentType.equals("application/json")) {
                    String jsonBody = msg.content().toString(StandardCharsets.UTF_8);
                    Map<String, String> requestMap = Singleton.jsonObjectMapper.convertValue(jsonBody, new TypeReference<>(){});
                    System.out.println("request: " + jsonBody);

                    String key = requestMap.keySet().stream().findFirst().orElseThrow();
                    Singleton.databaseService.put(key, requestMap.get(key));
                    break;
                } else {
                    responseStatus = HttpResponseStatus.BAD_REQUEST;
                }
            default:
                responseStatus = HttpResponseStatus.NOT_FOUND;
        }
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                responseStatus,
                Unpooled.wrappedBuffer(responseContent.getBytes()));

        HttpHeaders heads = response.headers();
        heads.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + "; charset=UTF-8");
        heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        ctx.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught");
        if(null != cause) cause.printStackTrace();
        if(null != ctx) ctx.close();
    }
}
