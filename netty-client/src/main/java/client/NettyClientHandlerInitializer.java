package client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NettyClientHandlerInitializer extends ChannelInitializer<Channel> {

    /**
     * 心跳超时时间
     */
    private static final Integer READ_TIMEOUT_SECONDS = 60;

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline()
                // 空闲检测
                .addLast(new IdleStateHandler(READ_TIMEOUT_SECONDS, 0, 0))
                .addLast(new ReadTimeoutHandler(3 * READ_TIMEOUT_SECONDS))
                // 编码器
                .addLast(new InvocationEncoder())
                // 解码器
                .addLast(new InvocationDecoder())
                // 消息分发器
                .addLast(new MessageDispatcher())
                // 客户端处理器
                .addLast(new NettyClientHandler());
    }
}
