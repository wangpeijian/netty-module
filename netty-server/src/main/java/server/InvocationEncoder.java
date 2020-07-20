package server;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvocationEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message msg, ByteBuf byteBuf) throws Exception {
        // <2.1> 将 Invocation 转换成 byte[] 数组
        byte[] content = JSON.toJSONBytes(msg);
        // <2.2> 写入 length
        byteBuf.writeInt(content.length);
        // <2.3> 写入内容
        byteBuf.writeBytes(content);
    }
}
