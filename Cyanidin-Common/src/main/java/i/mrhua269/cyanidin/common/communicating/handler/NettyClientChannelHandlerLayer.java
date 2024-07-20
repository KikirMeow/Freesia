package i.mrhua269.cyanidin.common.communicating.handler;

import i.mrhua269.cyanidin.common.EntryPoint;
import i.mrhua269.cyanidin.common.communicating.NettySocketClient;
import i.mrhua269.cyanidin.common.communicating.message.IMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class NettyClientChannelHandlerLayer extends SimpleChannelInboundHandler<IMessage<NettyClientChannelHandlerLayer>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMessage<NettyClientChannelHandlerLayer> msg) {
        try {
            msg.process(this);
        }catch (Exception e){
            EntryPoint.LOGGER_INST.error("Failed to process packet! ", e);
        }
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) {
        this.getClient().onChannelInactive();
    }

    public abstract NettySocketClient getClient();

    public abstract void onMasterPlayerDataResponse(int traceId, byte[] content);

    public abstract CompletableFuture<String> dispatchCommand(String command);
}
