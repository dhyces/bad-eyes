package dhyces.badeyes.network;

import dhyces.badeyes.BadEyes;
import dhyces.badeyes.network.packets.DisableShaderPacket;
import dhyces.badeyes.network.packets.EnableShaderPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Networking {
    private static final String VERSION = "1";
    private static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(BadEyes.MODID, "main"), () -> "1", VERSION::equals, VERSION::equals);
    private static final AtomicInteger PACKET_ID = new AtomicInteger();

    public static void register() {
        registerMessage(EnableShaderPacket.class, EnableShaderPacket::new);
        registerMessage(DisableShaderPacket.class, DisableShaderPacket::new);
    }

    public static <T extends SimplePacketHandler<T>> void sendMessageToPlayer(ServerPlayer serverPlayer, T packetHandler) {
        NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packetHandler);
    }

    public static FriendlyByteBuf unpooled() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    private static <T extends SimplePacketHandler<T>> void registerMessage(Class<T> simplePacketHandler, Function<FriendlyByteBuf, T> factory) {
        NETWORK_CHANNEL.registerMessage(PACKET_ID.getAndIncrement(), simplePacketHandler, SimplePacketHandler::encoder, factory::apply, SimplePacketHandler::messageConsumer);
    }
}
