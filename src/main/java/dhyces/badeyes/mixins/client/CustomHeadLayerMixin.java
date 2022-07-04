package dhyces.badeyes.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dhyces.badeyes.BadEyes;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomHeadLayer.class)
public class CustomHeadLayerMixin {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/Entity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void badeyes$render(PoseStack par1, MultiBufferSource par2, int par3, Entity par4, float par5, float par6, float par7, float par8, float par9, float par10, CallbackInfo ci) {
        if (par4 instanceof AbstractClientPlayer player) {
            if (BadEyes.hasGlasses(player))
                ci.cancel();
        }
    }
}
