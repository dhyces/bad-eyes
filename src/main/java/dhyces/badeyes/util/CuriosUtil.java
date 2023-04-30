package dhyces.badeyes.util;

import dhyces.badeyes.BadEyes;
import dhyces.badeyes.BadEyesClient;
import dhyces.badeyes.client.ClientConfig;
import dhyces.badeyes.network.Networking;
import dhyces.badeyes.network.packets.DisableShaderPacket;
import dhyces.badeyes.network.packets.EnableShaderPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.event.CurioEquipEvent;
import top.theillusivec4.curios.api.event.CurioUnequipEvent;

import java.util.Optional;

public class CuriosUtil {
    public static Optional<SlotResult> getGlasses(LivingEntity entity) {
        return CuriosApi.getCuriosHelper().findFirstCurio(entity, stack -> stack.is(BadEyes.GLASSES));
    }

    public static boolean areGlassesVisible(LivingEntity entity) {
        return getGlasses(entity).map(slotResult -> slotResult.slotContext().visible()).orElse(false);
    }

    public static void onCuriosGlassesBreak(Player player, ItemStack glasses) {
        CuriosApi.getCuriosHelper().onBrokenCurio(CuriosApi.getCuriosHelper().findFirstCurio(player, stack -> stack == glasses).map(SlotResult::slotContext).orElseThrow());
    }

    public static void addListeners(IEventBus modBus, IEventBus forgeBus) {
        modBus.addListener(CuriosUtil::curiosIMCEvent);
        forgeBus.addListener(CuriosUtil::glassesCurioEquipped);
        forgeBus.addListener(CuriosUtil::glassesCurioUnequipped);
    }

    private static void curiosIMCEvent(final InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
    }

    private static boolean allowClientEquip = true;

    private static void glassesCurioEquipped(final CurioEquipEvent event) {
        if (event.getEntity() instanceof AbstractClientPlayer) {
            if (event.getStack().is(BadEyes.GLASSES) && allowClientEquip) {
                BadEyesClient.disableShader();
            }
            allowClientEquip = true;
        }
    }

    private static void glassesCurioUnequipped(final CurioUnequipEvent event) {
        if (event.getEntity() instanceof AbstractClientPlayer) {
            if (event.getStack().is(BadEyes.GLASSES)) {
                BadEyesClient.enableShader();
                allowClientEquip = false;
            }
        }
    }
}
