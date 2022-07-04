package dhyces.badeyes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dhyces.badeyes.BadEyes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class GlassesRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public GlassesRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, AbstractClientPlayer player, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (BadEyes.hasGlasses(player)) {
            var location = Registry.ITEM.getKey(player.getInventory().armor.get(EquipmentSlot.HEAD.getIndex()).getItem());
            var glassesModel = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(location.getNamespace(), "entity/" + location.getPath()));
            pPoseStack.pushPose();
            getParentModel().getHead().translateAndRotate(pPoseStack);
            pPoseStack.translate(0.3125, -0.25, -0.35);
            glassesModel = glassesModel.handlePerspective(ItemTransforms.TransformType.HEAD, pPoseStack);
            CustomHeadLayer.translateToHead(pPoseStack, false);
            var consumer = pBuffer.getBuffer(RenderType.itemEntityTranslucentCull(TextureAtlas.LOCATION_BLOCKS));
            var last = pPoseStack.last();
            for (BakedQuad quad : glassesModel.getQuads(null, null, null, null)) {
                consumer.putBulkData(last, quad, 1.0F, 1.0F, 1.0F, pPackedLight, 0);
            }
            pPoseStack.popPose();
            pPoseStack.popPose();
        }
    }
}
