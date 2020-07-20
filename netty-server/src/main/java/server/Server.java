package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import server.NettyServerHandlerInitializer;

import java.net.InetSocketAddress;

@Slf4j
public class Server {


    private static final Integer PORT = 8088;
    private static final Integer ACCEPT_SIZE = 1024;
    /**
     * boss 线程组，用于服务端接受客户端的连接
     */
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
    /**
     * worker 线程组，用于服务端接受客户端的数据读写
     */
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
    /**
     * Netty server.Server Channel
     */
    private static Channel channel;

    private static final NettyServerHandlerInitializer nettyServerHandlerInitializer = new NettyServerHandlerInitializer();

    public static void main(String[] args) throws InterruptedException {
        // <2.1> 创建 ServerBootstrap 对象，用于 Netty server.Server 启动
        ServerBootstrap bootstrap = new ServerBootstrap();
        // <2.2> 设置 ServerBootstrap 的各种属性
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(PORT))
                .option(ChannelOption.SO_BACKLOG, ACCEPT_SIZE)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(nettyServerHandlerInitializer);
        // <2> 绑定端口，并同步等待成功，即启动服务端
        ChannelFuture future = bootstrap.bind().sync();
        if (future.isSuccess()) {
            channel = future.channel();
            log.info("[start][Netty server.Server 启动在 {} 端口]", PORT);
        }
    }

}
