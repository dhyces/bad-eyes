package dhyces.badeyes;

import dhyces.badeyes.client.GlassesRenderLayer;
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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;

public class BadEyesClient {

    public static boolean localHasBadEyes() {
        ItemStack headItem = Minecraft.getInstance().player.getInventory().armor.get(EquipmentSlot.HEAD.getIndex());
        return headItem.isEmpty() || !headItem.is(BadEyes.GLASSES);
    }

    public static void init(IEventBus bus) {
        bus.addListener(BadEyesClient::registerAdditionalModels);
        bus.addListener(BadEyesClient::entityRendererAddLayers);
        bus.addListener(BadEyesClient::addToTabs);
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
