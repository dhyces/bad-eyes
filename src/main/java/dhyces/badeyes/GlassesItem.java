package dhyces.badeyes;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class GlassesItem extends Item implements Wearable {

    final Supplier<TagKey<Item>> repairMaterials;

    public GlassesItem(Properties pProperties, Supplier<TagKey<Item>> repairTag) {
        super(pProperties);
        this.repairMaterials = repairTag;
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        var item = pPlayer.getItemInHand(pUsedHand);
        var slot = Mob.getEquipmentSlotForItem(item);
        var slotItem = pPlayer.getItemBySlot(slot);
        if (slotItem.isEmpty()) {
            pPlayer.setItemSlot(slot, item.copy());
            if (!pLevel.isClientSide)
                pPlayer.awardStat(Stats.ITEM_USED.get(this));
            item.shrink(1);
            return InteractionResultHolder.sidedSuccess(item, pLevel.isClientSide);
        }
        return InteractionResultHolder.fail(item);
    }

    @Override
    public boolean isValidRepairItem(ItemStack pStack, ItemStack pRepairCandidate) {
        if (pStack.getItem() instanceof GlassesItem item) {
            return pRepairCandidate.is(item.repairMaterials.get());
        }
        return false;
    }

    @Nullable
    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_CHAIN;
    }

    @Override
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }
}
