package dhyces.badeyes;

import dhyces.badeyes.datagen.BadEyesLanguageProvider;
import dhyces.badeyes.datagen.BadEyesModelProviders;
import dhyces.badeyes.datagen.BadEyesRecipeProvider;
import dhyces.badeyes.datagen.BadEyesTagProviders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
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

    public static boolean hasGlasses(LivingEntity entity) {
        return entity.getSlot(EquipmentSlot.HEAD.getIndex()).get().is(BadEyes.GLASSES);
    }

    private void datagen(GatherDataEvent event) {
        var generator = event.getGenerator();
        var fileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new BadEyesModelProviders.BadEyesItemModelProvider(generator, fileHelper));
        generator.addProvider(event.includeClient(), new BadEyesLanguageProvider(generator));

        generator.addProvider(event.includeServer(), new BadEyesTagProviders.BadEyesItemTagProvider(generator, fileHelper));
        generator.addProvider(event.includeServer(), new BadEyesRecipeProvider(generator));
    }
}
