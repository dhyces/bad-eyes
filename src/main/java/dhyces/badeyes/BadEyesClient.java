package dhyces.badeyes;

import dhyces.badeyes.client.ClientConfig;
import dhyces.badeyes.client.GlassesRenderLayer;
import dhyces.badeyes.client.model.GroupedModel;
import dhyces.badeyes.util.CuriosUtil;
import dhyces.badeyes.util.GlassesSlot;
import dhyces.trimmed.api.TrimmedApi;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class BadEyesClient {

    public static final ClientTagKey ADDITIONAL_MODELS = ClientTagKey.of(BadEyes.id("additional_models"));
    public static final ClientTagKey LENS_SHAPES = ClientTagKey.of(BadEyes.id("lens_shapes"));
    public static final ClientMapKey LENS_COLORS = ClientMapKey.of(BadEyes.id("lens_colors"));
    public static final ClientRegistryMapKey<Item> ITEM_LENS_SUFFIXES = ClientRegistryMapKey.of(Registries.ITEM, BadEyes.id("item_lens_suffixes"));

    public static String itemBasedSuffix(Item item) {
        return TrimmedApi.MAP_API.getSafeRegistryClientMap(ITEM_LENS_SUFFIXES).map(itemStringMap -> itemStringMap.get(item)).orElse("simple");
    }

    public static boolean localHasBadEyes() {
        GlassesSlot glasses = BadEyes.getGlasses(Minecraft.getInstance().player);
        return glasses.stack().isEmpty() || !glasses.stack().is(BadEyes.GLASSES);
    }

    public static boolean shouldGlassesRender(LivingEntity livingEntity) {
        GlassesSlot glasses = BadEyes.getGlasses(livingEntity);
        return glasses.isCurio() ? CuriosUtil.areGlassesVisible(livingEntity) : !glasses.stack().isEmpty();
    }

    static void init(IEventBus modBus, IEventBus forgeBus) {
        modBus.addListener(BadEyesClient::registerAdditionalModels);
        modBus.addListener(BadEyesClient::entityRendererAddLayers);
        modBus.addListener(BadEyesClient::addToTabs);
        modBus.addListener(BadEyesClient::addReloadListener);
        modBus.addListener(BadEyesClient::addGeometryLoader);
        forgeBus.addListener(BadEyesClient::playerTick);

        ForgeConfigSpec spec = ClientConfig.build();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, spec);
    }

    private static void addToTabs(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.COMBAT) {
            event.accept(BadEyes.SIMPLE_GLASSES);
            event.accept(BadEyes.NETHERITE_GLASSES);
        }
    }

    private static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        TrimmedApi.TAG_API.getUncheckedTag(ADDITIONAL_MODELS).forEach(optionalId -> {
            event.register(optionalId.elementId());
        });
    }

    private static void addReloadListener(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(GlassesRenderLayer.CACHE);
    }

    private static void addGeometryLoader(final ModelEvent.RegisterGeometryLoaders event) {
        event.register("grouped", GroupedModel.Loader.INSTANCE);
    }

    private static boolean firstTick = true;
    static void playerTick(final TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() && event.phase == TickEvent.Phase.START && firstTick) {
            if (localHasBadEyes()) {
                enableShader();
            }
            firstTick = false;
        }
    }

    public static void enableShader() {
        Optional<ResourceLocation> shaderOptional = ClientConfig.getShader();
        if (shaderOptional.isPresent() && Minecraft.getInstance().gameRenderer.currentEffect() == null) {
            ResourceLocation shader = shaderOptional.map(resourceLocation -> resourceLocation.withPrefix("shaders/post/").withSuffix(".json")).get();
            Minecraft.getInstance().gameRenderer.loadEffect(shader);
//            PostChain postChain = Minecraft.getInstance().gameRenderer.currentEffect();
//            Optional<PostPass> postPassOptional = postChain.passes.stream().filter(pass -> pass.getName().equals("badeyes:box_blur")).findFirst();
//            postPassOptional.ifPresent(postPass -> {
//                postPass.getEffect().getUniform("Params").set(2, 100);
//            });
        }
    }

    public static void disableShader() {
        if (Minecraft.getInstance().gameRenderer.currentEffect() != null) {
            Minecraft.getInstance().gameRenderer.shutdownEffect();
        }
    }

    static void entityRendererAddLayers(EntityRenderersEvent.AddLayers event) {
        event.getSkins().forEach(skin -> {
            LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer = event.getSkin(skin);
            renderer.addLayer(new GlassesRenderLayer<>(renderer));
        });
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES.getValues()) {
            EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(type);
            if (renderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer && livingEntityRenderer.getModel() instanceof HeadedModel) {
                livingEntityRenderer.addLayer(cast(new GlassesRenderLayer<>(cast(livingEntityRenderer))));
            }
        }
    }

    private static <T> T cast(Object o) {
        return (T) o;
    }
}
