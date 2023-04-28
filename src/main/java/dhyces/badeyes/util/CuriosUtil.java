package dhyces.badeyes.util;

import dhyces.badeyes.BadEyes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

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
}
