package dhyces.badeyes;

import dhyces.badeyes.datagen.BadEyesLanguageProvider;
import dhyces.badeyes.datagen.BadEyesModelProviders;
import dhyces.badeyes.datagen.BadEyesRecipeProvider;
import dhyces.badeyes.datagen.BadEyesTagProviders;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.concurrent.CompletableFuture;

@Mod(BadEyes.MODID)
public class BadEyes {

    public static final String MODID = "badeyes";
    public static final TagKey<Item> GLASSES = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation(MODID, "glasses"));
    public static final TagKey<Item> GLASSES_REPAIR_MATERIALS = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation(MODID, "glasses_repair_materials"));
    static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.Keys.ITEMS, BadEyes.MODID);
    public static final RegistryObject<Item> SIMPLE_GLASSES = ITEM_REGISTER.register("simple_glasses", () -> new GlassesItem(new Item.Properties().durability(100), () -> GLASSES_REPAIR_MATERIALS));
    public static final RegistryObject<Item> NETHERITE_GLASSES = ITEM_REGISTER.register("netherite_glasses", () -> new GlassesItem(new Item.Properties().durability(1000).fireResistant(), () -> GLASSES_REPAIR_MATERIALS));

    public BadEyes() {
        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEM_REGISTER.register(modbus);

        if (FMLLoader.getDist().isClient()) {
            BadEyesClient.init(modbus);
        }

        if (FMLLoader.getLaunchHandler().isData()) {
            modbus.addListener(this::datagen);
        }
    }

    public static boolean hasGlasses(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).is(BadEyes.GLASSES);
    }

    private void datagen(GatherDataEvent event) {
        var generator = event.getGenerator();
        var fileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new BadEyesModelProviders.BadEyesItemModelProvider(generator.getPackOutput(), MODID, fileHelper));
        generator.addProvider(event.includeClient(), new BadEyesLanguageProvider(generator.getPackOutput(), MODID, "en_us"));

        CompletableFuture<HolderLookup.Provider> future = CompletableFuture.supplyAsync(() -> RegistryAccess.EMPTY);
        generator.addProvider(event.includeServer(), new BadEyesTagProviders.BadEyesItemTagProvider(generator.getPackOutput(), future, new BlockTagsProvider(generator.getPackOutput(), future, MODID, null) {
            @Override
            protected void addTags(HolderLookup.Provider pProvider) {

            }
        }, MODID, fileHelper));
        generator.addProvider(event.includeServer(), new BadEyesRecipeProvider(generator.getPackOutput()));
    }
}
