package server;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import static server.NettyServerHandlerInitializer.channelManager;


@Slf4j
public class MessageDispatcher extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        log.info(JSON.toJSONString(message));

        Message msg = new Message();
        msg.setId(String.valueOf(System.currentTimeMillis()));
        msg.setContent("已收到");

        channelManager.send(channelHandlerContext.channel(), msg);
    }
}
