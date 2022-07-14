package dhyces.badeyes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dhyces.badeyes.BadEyes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class GlassesRenderLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {

    public GlassesRenderLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T livingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (BadEyes.hasGlasses(livingEntity)) {
            var itemStack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
            var location = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
            var glassesModel = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(location.getNamespace(), "entity/" + location.getPath()));
            pPoseStack.pushPose();
            getParentModel().getHead().translateAndRotate(pPoseStack);
            pPoseStack.translate(0.3125, -0.25, -0.35);
            glassesModel = glassesModel.applyTransform(ItemTransforms.TransformType.HEAD, pPoseStack, false);
            CustomHeadLayer.translateToHead(pPoseStack, false);
            var last = pPoseStack.last();
            for (RenderType renderType : glassesModel.getRenderTypes(itemStack, Minecraft.useShaderTransparency())) {
                for (BakedQuad quad : glassesModel.getQuads(null, null, RandomSource.create(42L), ModelData.EMPTY, renderType)) {
                    pBuffer.getBuffer(renderType).putBulkData(last, quad, 1.0F, 1.0F, 1.0F, pPackedLight, 0);
                }
            }
            pPoseStack.popPose();
        }
    }
}
