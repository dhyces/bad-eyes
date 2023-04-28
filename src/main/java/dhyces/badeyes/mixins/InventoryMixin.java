package dhyces.badeyes.mixins;

import dhyces.badeyes.BadEyes;
import dhyces.badeyes.util.CuriosUtil;
import dhyces.badeyes.util.GlassesSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Final
    @Shadow
    private Player player;

    @Inject(method = "hurtArmor", at = @At(value = "TAIL"))
    private void badeyes_hurtArmor(DamageSource pSource, float pDamage, int[] pArmorPieces, CallbackInfo ci) {
        GlassesSlot glassesSlot = BadEyes.getGlasses(player);
        if (!glassesSlot.stack().isEmpty()) {
            glassesSlot.stack().hurtAndBreak((int)pDamage, player, player1 -> {
                if (glassesSlot.isCurio()) {
                    CuriosUtil.onCuriosGlassesBreak(player1, glassesSlot.stack());
                } else {
                    player1.broadcastBreakEvent(EquipmentSlot.HEAD);
                }
            });
        }
    }
}
