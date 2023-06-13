package dhyces.badeyes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dhyces.badeyes.BadEyes;
import dhyces.badeyes.BadEyesClient;
import dhyces.badeyes.GlassesItem;
import dhyces.badeyes.client.model.GroupedModel;
import dhyces.badeyes.client.model.SpriteQuadTransformer;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.renderable.BakedModelRenderable;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.IRenderable;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class GlassesRenderLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {

    public static final RenderableCache CACHE = new RenderableCache();

    public GlassesRenderLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T livingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (BadEyesClient.shouldGlassesRender(livingEntity)) {
            ItemStack itemStack = BadEyes.getGlasses(livingEntity).stack();
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
            // get model
            //  if model doesn't exist, then check if the armor trim exists
            //   if armor trim exists, then create a new model based on the base simple_glasses model
            BakedModel glassesModel = getNormalModel(id);
            pPoseStack.pushPose();
            getParentModel().getHead().translateAndRotate(pPoseStack);
            pPoseStack.translate(0.3125, -0.21, -0.35);
            glassesModel = glassesModel.applyTransform(ItemDisplayContext.HEAD, pPoseStack, false);
            CustomHeadLayer.translateToHead(pPoseStack, livingEntity instanceof Villager);
            getRenderable(itemStack).render(pPoseStack, pBuffer, RenderType::entityTranslucentCull, pPackedLight, LivingEntityRenderer.getOverlayCoords(livingEntity, 0), pAgeInTicks, itemStack);
            pPoseStack.popPose();
        }
    }

    protected BakedModel getGlassesModel(ResourceLocation glasses, Optional<ArmorTrim> trim) {
        return trim.map(
                armorTrim -> Minecraft.getInstance().getModelManager().getModel(
                        glasses.withPrefix("armor/trimmed_glasses/")
                        .withSuffix("_" + armorTrim.pattern().get().assetId().getPath() + "_" + armorTrim.material().get().assetName())
                )
        ).orElse(getNormalModel(glasses));
    }

    protected TextureAtlasSprite getArmorSprite(ArmorTrim armorTrim, String substitutableId) {
        String materialName = armorTrim.material().get().assetName();
        String patternName = armorTrim.pattern().get().assetId().getPath();
        ResourceLocation substituted = new ResourceLocation(substitutableId.formatted(patternName, materialName));
        return Minecraft.getInstance().getTextureAtlas(Sheets.ARMOR_TRIMS_SHEET).apply(substituted);
    }

    protected TextureAtlasSprite getLensSprite(TrimPattern pattern, ItemStack stack) {
        String color = stack.getTagElement("LensDye") != null ? stack.getTagElement("LensDye").getAsString() : BadEyesClient.itemBasedSuffix(stack.getItem());
        ResourceLocation substituted = new ResourceLocation("badeyes:lens/shapes/lens_%s_%s".formatted(pattern.assetId().getPath(), color));
        return Minecraft.getInstance().getTextureAtlas(Sheets.ARMOR_TRIMS_SHEET).apply(substituted);
    }

    protected IRenderable<ItemStack> getRenderable(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        Optional<ArmorTrim> armorTrim = ArmorTrim.getTrim(Minecraft.getInstance().level.registryAccess(), stack);
        ResourceLocation modelId = armorTrim.map(trim -> itemId.withSuffix(trimSuffix(trim))).orElse(itemId);
        return CACHE.getOrCompute(modelId, id -> {
            BakedModel glassesModel = getNormalModel(itemId);
            if (armorTrim.isEmpty()) {
                BakedModelRenderable renderable = BakedModelRenderable.of(glassesModel);
                BakedModelRenderable.Context realContext = new BakedModelRenderable.Context(ModelData.EMPTY);
                return (poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, partialTick, context) -> {
                    renderable.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, partialTick, realContext);
                };
            } else {
                if (glassesModel instanceof GroupedModel.Baked baked) {
                    TextureAtlasSprite framesSprite = getArmorSprite(armorTrim.get(), "badeyes:trims/models/glasses/frames_%s_%s");
                    TextureAtlasSprite lensSprite = getLensSprite(armorTrim.get().pattern().get(), stack);
                    List<BakedQuad> framesQuads = transform(framesSprite, "frame", baked);
                    List<BakedQuad> lensQuads = transform(lensSprite, "lens", baked);
                    CompositeRenderable renderable = CompositeRenderable.builder()
                            .child("frames")
                            .addMesh(framesSprite.atlasLocation(), framesQuads).end()
                            .child("lens")
                            .addMesh(lensSprite.atlasLocation(), lensQuads).end()
                            .get();
                    return (poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, partialTick, context) -> {
                        renderable.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, partialTick, CompositeRenderable.Transforms.EMPTY);
                    };
                } else {
                    return (poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, partialTick, context) -> {};
                }
            }
        });
    }

    protected List<BakedQuad> transform(TextureAtlasSprite sprite, String name, GroupedModel.Baked bakedModel) {
        IQuadTransformer transformer = SpriteQuadTransformer.create(sprite);
        return bakedModel.getGroup(name).getQuads(null).stream().map(transformer::process).toList();
    }

    protected final String trimSuffix(ArmorTrim trim) {
        return "_" + trim.pattern().get().assetId().getPath() + "_" + trim.material().get().assetName();
    }

    protected BakedModel getNormalModel(ResourceLocation resourceLocation) {
        return Minecraft.getInstance().getModelManager().getModel(resourceLocation.withPrefix("armor/"));
    }

    public static final class RenderableCache extends SimplePreparableReloadListener<Unit> {
        private Map<ResourceLocation, IRenderable<ItemStack>> renderables = new HashMap<>();

        @Override
        protected Unit prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
            renderables.clear();
            return Unit.INSTANCE;
        }

        @Override
        protected void apply(Unit pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        }

        public boolean has(ResourceLocation modelId) {
            return renderables.containsKey(modelId);
        }

        @Nullable
        public IRenderable<ItemStack> getRenderable(ResourceLocation modelId) {
            return renderables.get(modelId);
        }

        public IRenderable<ItemStack> getOrCompute(ResourceLocation modelId, Function<ResourceLocation, IRenderable<ItemStack>> computation) {
            return renderables.computeIfAbsent(modelId, computation);
        }
    }
}
