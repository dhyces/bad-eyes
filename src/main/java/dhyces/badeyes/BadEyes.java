package dhyces.badeyes;

import dhyces.badeyes.datagen.BadEyesLanguageProvider;
import dhyces.badeyes.datagen.BadEyesModelProviders;
import dhyces.badeyes.datagen.BadEyesRecipeProvider;
import dhyces.badeyes.datagen.BadEyesTagProviders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(BadEyes.MODID)
public class BadEyes {

    public static final String MODID = "badeyes";
    public static final TagKey<Item> GLASSES = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation(MODID, "glasses"));
    public static final TagKey<Item> GLASSES_REPAIR_MATERIALS = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation(MODID, "glasses_repair_materials"));
    static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.Keys.ITEMS, BadEyes.MODID);
    public static final RegistryObject<Item> SIMPLE_GLASSES = ITEM_REGISTER.register("simple_glasses", () -> new GlassesItem(new Item.Properties().durability(100).tab(CreativeModeTab.TAB_COMBAT), () -> GLASSES_REPAIR_MATERIALS));
    public static final RegistryObject<Item> NETHERITE_GLASSES = ITEM_REGISTER.register("netherite_glasses", () -> new GlassesItem(new Item.Properties().durability(1000).fireResistant().tab(CreativeModeTab.TAB_COMBAT), () -> GLASSES_REPAIR_MATERIALS));

    public BadEyes() {
        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEM_REGISTER.register(modbus);
        if (FMLLoader.getDist().equals(Dist.CLIENT)) {
            BadEyesClient.init(modbus);
        }

        if (FMLLoader.getLaunchHandler().isData()) {
            modbus.addListener(this::datagen);
        }
    }

    public static boolean hasBadEyes(Player player) {
        var headItem = player.getInventory().armor.get(EquipmentSlot.HEAD.getIndex());
        return !headItem.is(BadEyes.GLASSES) || headItem.isEmpty();
    }

    public static boolean hasGlasses(Player player) {
        return player.getInventory().armor.get(EquipmentSlot.HEAD.getIndex()).is(BadEyes.GLASSES);
    }

    private void datagen(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeClient(), new BadEyesModelProviders.BadEyesItemModelProvider(event.getGenerator(), event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeClient(), new BadEyesLanguageProvider(event.getGenerator()));

        event.getGenerator().addProvider(event.includeServer(), new BadEyesTagProviders.BadEyesItemTagProvider(event.getGenerator(), event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new BadEyesRecipeProvider(event.getGenerator()));
    }
}
