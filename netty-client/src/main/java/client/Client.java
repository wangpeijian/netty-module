package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Client {

    private static final String serverHost = "127.0.0.1";
    private static final Integer serverPort = 8088;

    private static final Integer RECONNECT_SECONDS = 5;


    private static final EventLoopGroup eventGroup = new NioEventLoopGroup();

    private static final NettyClientHandlerInitializer nettyClientHandlerInitializer = new NettyClientHandlerInitializer();

    /**
     * Netty client.Client Channel
     */
    private static volatile Channel channel;

    public static void main(String[] args) {
        connect();
        sendLoop();
    }

    public static void sendLoop() {
        new Thread(new Runnable() {
            public void run() {
                while (true){
                    log.info("准备发送");

                    if(channel != null){
                        Message msg = new Message();
                        msg.setId(String.valueOf(System.currentTimeMillis()));
                        msg.setContent("测试");
                        send(msg);
                    }

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public static void connect() {
        // <2.1> 创建 Bootstrap 对象，用于 Netty client.Client 启动
        Bootstrap bootstrap = new Bootstrap();
        // <2.2>
        bootstrap.group(eventGroup) // <2.2.1> 设置一个 EventLoopGroup 对象
                .channel(NioSocketChannel.class)  // <2.2.2> 指定 Channel 为客户端 NioSocketChannel
                .remoteAddress(serverHost, serverPort) // <2.2.3> 指定连接服务器的地址
                .option(ChannelOption.SO_KEEPALIVE, true) // <2.2.4> TCP Keepalive 机制，实现 TCP 层级的心跳保活功能
                .option(ChannelOption.TCP_NODELAY, true) //<2.2.5>  允许较小的数据包的发送，降低延迟
                .handler(nettyClientHandlerInitializer);

        // <2.3> 连接服务器，并异步等待成功，即启动客户端
        bootstrap.connect().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                // 连接失败
                if (!future.isSuccess()) {
                    log.error("[start][Netty client.Client 连接服务器({}:{}) 失败]", serverHost, serverPort);
                    reconnect();
                    return;
                }
                // 连接成功
                channel = future.channel();
                log.info("[start][Netty client.Client 连接服务器({}:{}) 成功]", serverHost, serverPort);
            }
        });
    }

    /**
     * 发送消息
     *
     * @param msg 消息体
     */
    public static void send(Message msg) {
        if (channel == null) {
            log.error("[send][连接不存在]");
            return;
        }
        if (!channel.isActive()) {
            log.error("[send][连接({})未激活]", channel.id());
            return;
        }
        // 发送消息
        channel.writeAndFlush(msg);
        log.info("消息已发送:{}", msg);
    }

    public static void reconnect() {
        eventGroup.schedule(new Runnable() {
            public void run() {
                log.info("[reconnect][开始重连]");
                connect();
            }
        }, RECONNECT_SECONDS, TimeUnit.SECONDS);
        log.info("[reconnect][{} 秒后将发起重连]", RECONNECT_SECONDS);
    }
}
