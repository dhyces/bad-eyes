package dhyces.badeyes;

import dhyces.badeyes.client.ClientConfig;
import dhyces.badeyes.client.GlassesRenderLayer;
import dhyces.badeyes.util.CuriosUtil;
import dhyces.badeyes.util.GlassesSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class BadEyesClient {

    public static boolean localHasBadEyes() {
        GlassesSlot glasses = BadEyes.getGlasses(Minecraft.getInstance().player);
        return glasses.stack().isEmpty() || !glasses.stack().is(BadEyes.GLASSES);
    }

    public static boolean shouldLocalGlassesRender() {
        GlassesSlot glasses = BadEyes.getGlasses(Minecraft.getInstance().player);
        return glasses.isCurio() ? CuriosUtil.areGlassesVisible(Minecraft.getInstance().player) : !glasses.stack().isEmpty();
    }

    static void init(IEventBus modBus, IEventBus forgeBus) {
        modBus.addListener(BadEyesClient::registerAdditionalModels);
        modBus.addListener(BadEyesClient::entityRendererAddLayers);
        modBus.addListener(BadEyesClient::addToTabs);
        forgeBus.addListener(BadEyesClient::playerTick);

        ForgeConfigSpec spec = ClientConfig.build();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, spec);
    }

    static void addToTabs(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab().equals(CreativeModeTabs.COMBAT)) {
            event.accept(BadEyes.SIMPLE_GLASSES);
            event.accept(BadEyes.NETHERITE_GLASSES);
        }
    }

    static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(new ResourceLocation(BadEyes.MODID, "entity/simple_glasses"));
        event.register(new ResourceLocation(BadEyes.MODID, "entity/netherite_glasses"));
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
