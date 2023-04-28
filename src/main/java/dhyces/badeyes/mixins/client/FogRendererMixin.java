package dhyces.badeyes.mixins.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dhyces.badeyes.BadEyesClient;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FogRenderer.class, priority = 1050)
public class FogRendererMixin {
    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private static void badeyes_setupFog(Camera camera, FogRenderer.FogMode fogMode, float viewDistance, boolean thickFog, float partialTicks, CallbackInfo ci) {
        if (BadEyesClient.localHasBadEyes()) {
            RenderSystem.setShaderFogStart(-8.0f);
            RenderSystem.setShaderFogEnd(10);
            ci.cancel();
        }
    }
}
