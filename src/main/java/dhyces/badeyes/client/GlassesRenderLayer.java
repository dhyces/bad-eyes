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
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GlassesRenderLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {

    public GlassesRenderLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T livingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (BadEyesClient.shouldLocalGlassesRender()) {
            ItemStack itemStack = BadEyes.getGlasses(livingEntity).stack();
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
            BakedModel glassesModel = getGlassesModel(id, livingEntity);
            pPoseStack.pushPose();
            getParentModel().getHead().translateAndRotate(pPoseStack);
            pPoseStack.translate(0.3125, -0.21, -0.35);
            glassesModel = glassesModel.applyTransform(ItemDisplayContext.HEAD, pPoseStack, false);
            CustomHeadLayer.translateToHead(pPoseStack, false);
            PoseStack.Pose last = pPoseStack.last();
            for (RenderType renderType : glassesModel.getRenderTypes(itemStack, Minecraft.useShaderTransparency())) {
                VertexConsumer buffer = pBuffer.getBuffer(renderType);
                for (BakedQuad quad : glassesModel.getQuads(null, null, RandomSource.create(42L), ModelData.EMPTY, renderType)) {
                    Optional<ArmorTrim> trim = ArmorTrim.getTrim(Minecraft.getInstance().level.registryAccess(), itemStack);
//                    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(Sheets.ARMOR_TRIMS_SHEET).apply();
                    BakedQuad realQuad = new BakedQuad(quad.getVertices(), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade(), quad.hasAmbientOcclusion());
                    buffer.putBulkData(last, realQuad, 1.0F, 1.0F, 1.0F, pPackedLight, LivingEntityRenderer.getOverlayCoords(livingEntity, 0.0F));
                }
            }
            pPoseStack.popPose();
        }
    }

    protected BakedModel getGlassesModel(ResourceLocation glasses, LivingEntity livingEntity) {
        return Minecraft.getInstance().getModelManager().getModel(glasses.withPrefix("entity/"));
    }
}
