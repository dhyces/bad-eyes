package dhyces.badeyes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dhyces.badeyes.BadEyes;
import dhyces.badeyes.BadEyesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class GlassesRenderLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {

    public GlassesRenderLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T livingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (BadEyes.hasGlasses(livingEntity) && BadEyesClient.shouldLocalGlassesRender()) {
            ItemStack itemStack = BadEyes.getGlasses(livingEntity).stack();
            ResourceLocation location = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
            BakedModel glassesModel = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(location.getNamespace(), "entity/" + location.getPath()));
            pPoseStack.pushPose();
            getParentModel().getHead().translateAndRotate(pPoseStack);
            pPoseStack.translate(0.3125, -0.25, -0.35);
            glassesModel = glassesModel.applyTransform(ItemDisplayContext.HEAD, pPoseStack, false);
            CustomHeadLayer.translateToHead(pPoseStack, false);
            PoseStack.Pose last = pPoseStack.last();
            for (RenderType renderType : glassesModel.getRenderTypes(itemStack, Minecraft.useShaderTransparency())) {
                VertexConsumer buffer = pBuffer.getBuffer(renderType);
                for (BakedQuad quad : glassesModel.getQuads(null, null, RandomSource.create(42L), ModelData.EMPTY, renderType)) {
                    buffer.putBulkData(last, quad, 1.0F, 1.0F, 1.0F, pPackedLight, LivingEntityRenderer.getOverlayCoords(livingEntity, 0.0F));
                }
            }
            pPoseStack.popPose();
        }
    }
}
