package client;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class InvocationDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // <2.1> 标记当前读取位置
        byteBuf.markReaderIndex();
        // <2.2> 判断是否能够读取 length 长度
        if (byteBuf.readableBytes() <= 4) {
            return;
        }
        // <2.3> 读取长度
        int length = byteBuf.readInt();
        if (length < 0) {
            throw new CorruptedFrameException("negative length: " + length);
        }
        // <3.1> 如果 message 不够可读，则退回到原读取位置
        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
            return;
        }
        // <3.2> 读取内容
        byte[] content = new byte[length];
        byteBuf.readBytes(content);
        // <3.3> 解析成 Invocation
        Message invocation = JSON.parseObject(content, Message.class);
        list.add(invocation);


        log.info("[decode][连接({}), 解析到一条消息({})]", channelHandlerContext.channel().id(), invocation.toString());
    }
}
