package dhyces.badeyes.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface SimplePacketHandler<T extends SimplePacketHandler<T>> {
    void encoder(FriendlyByteBuf friendlyByteBuf);

    void messageConsumer(Supplier<NetworkEvent.Context> contextSupplier);
}
