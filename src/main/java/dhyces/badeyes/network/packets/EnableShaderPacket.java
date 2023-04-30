package dhyces.badeyes.network.packets;

import dhyces.badeyes.BadEyesClient;
import dhyces.badeyes.network.Networking;
import dhyces.badeyes.network.SimplePacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EnableShaderPacket implements SimplePacketHandler<EnableShaderPacket> {

    public EnableShaderPacket(FriendlyByteBuf friendlyByteBuf) {

    }

    public EnableShaderPacket() {
        this(Networking.unpooled());
    }

    @Override
    public void encoder(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public void messageConsumer(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(BadEyesClient::enableShader);
        contextSupplier.get().setPacketHandled(true);
    }
}
