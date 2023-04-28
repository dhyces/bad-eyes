package dhyces.badeyes.mixins;

import dhyces.badeyes.BadEyes;
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
    public NonNullList<ItemStack> armor;

    @Final
    @Shadow
    private Player player;

    @Inject(method = "hurtArmor", at = @At(value = "TAIL"))
    private void badeyes_hurtArmor(DamageSource pSource, float pDamage, int[] pArmorPieces, CallbackInfo ci) {
        for (int i : pArmorPieces) {
            if (i == EquipmentSlot.HEAD.getIndex()) {
                ItemStack item = armor.get(EquipmentSlot.HEAD.getIndex());
                if (item.is(BadEyes.GLASSES)) {
                    item.hurtAndBreak((int)pDamage, player, player1 -> player1.broadcastBreakEvent(EquipmentSlot.HEAD));
                }
            }
        }
    }
}
