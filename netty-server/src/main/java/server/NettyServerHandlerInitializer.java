package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyServerHandlerInitializer extends ChannelInitializer<Channel> {

    public static final NettyChannelManager channelManager = new NettyChannelManager();
    /**
     * 心跳超时时间
     */
    private static final Integer READ_TIMEOUT_SECONDS = 3 * 60;


    @Override
    protected void initChannel(Channel channel) throws Exception {
        // <1> 获得 Channel 对应的 ChannelPipeline
        ChannelPipeline channelPipeline = channel.pipeline();
        // <2> 添加一堆 NettyServerHandler 到 ChannelPipeline 中
        channelPipeline
                // 空闲检测
                .addLast(new ReadTimeoutHandler(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                // 编码器
                .addLast(new InvocationEncoder())
                // 解码器
                .addLast(new InvocationDecoder())
                // 消息分发器
                .addLast(new MessageDispatcher())
                // 服务端处理器
                .addLast(new NettyServerHandler());
    }
}