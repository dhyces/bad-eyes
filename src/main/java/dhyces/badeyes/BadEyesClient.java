package dhyces.badeyes;

import dhyces.badeyes.client.GlassesRenderLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class BadEyesClient {

    public static boolean localHasBadEyes() {
        var headItem = Minecraft.getInstance().player.getInventory().armor.get(EquipmentSlot.HEAD.getIndex());
        return headItem.isEmpty() || !headItem.is(BadEyes.GLASSES);
    }

    public static void init(IEventBus bus) {
        bus.addListener(BadEyesClient::clientSetup);
        bus.addListener(BadEyesClient::modelBake);
        bus.addListener(BadEyesClient::reloadSeparateModels);
        bus.addListener(BadEyesClient::entityRendererAddLayers);
    }

    static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
        });
    }

    static void modelBake(ModelRegistryEvent event) {
        addSpecialModels();
    }

    static void addSpecialModels() {
        ForgeModelBakery.addSpecialModel(new ResourceLocation(BadEyes.MODID, "entity/simple_glasses"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(BadEyes.MODID, "entity/netherite_glasses"));
    }

    private static void reloadSeparateModels(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((pPreparationBarrier, pResourceManager, pPreparationsProfiler, pReloadProfiler, pBackgroundExecutor, pGameExecutor) -> pPreparationBarrier.wait(Unit.INSTANCE).thenRunAsync(() -> addSpecialModels()));
    }

    static void entityRendererAddLayers(EntityRenderersEvent.AddLayers event) {
        event.getSkins().forEach(skin -> {
            LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer = event.getSkin(skin);
            renderer.addLayer(new GlassesRenderLayer(renderer));
        });
    }
}
