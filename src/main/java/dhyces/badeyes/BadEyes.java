package dhyces.badeyes;

import dhyces.badeyes.datagen.*;
import dhyces.badeyes.datagen.onetwenty.BadEyesOneTwentyRecipeProvider;
import dhyces.badeyes.datagen.onetwenty.OneTwentyItemTagProvider;
import dhyces.badeyes.network.Networking;
import dhyces.badeyes.network.packets.DisableShaderPacket;
import dhyces.badeyes.network.packets.EnableShaderPacket;
import dhyces.badeyes.util.CuriosUtil;
import dhyces.badeyes.util.GlassesSlot;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.SlotResult;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Mod(BadEyes.MODID)
public class BadEyes {

    public static final String MODID = "badeyes";
    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static final TagKey<Item> GLASSES = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation(MODID, "glasses"));
    public static final TagKey<Item> GLASSES_REPAIR_MATERIALS = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation(MODID, "glasses_repair_materials"));
    static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.Keys.ITEMS, BadEyes.MODID);
    public static final RegistryObject<Item> SIMPLE_GLASSES = ITEM_REGISTER.register("simple_glasses", () -> new GlassesItem(new Item.Properties().durability(100), () -> GLASSES_REPAIR_MATERIALS));
    public static final RegistryObject<Item> NETHERITE_GLASSES = ITEM_REGISTER.register("netherite_glasses", () -> new GlassesItem(new Item.Properties().durability(1000).fireResistant(), () -> GLASSES_REPAIR_MATERIALS));

    public BadEyes() {
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        ITEM_REGISTER.register(modbus);
        modbus.addListener(this::addBuiltInPack);

        forgeBus.addListener(this::playerGlassesEquipment);
        forgeBus.addListener(this::playerRespawn);

        Networking.register();

        if (FMLLoader.getDist().isClient()) {
            BadEyesClient.init(modbus, forgeBus);
        }

        if (FMLLoader.getLaunchHandler().isData()) {
            modbus.addListener(this::datagen);
        }

        if (ModList.get().isLoaded("curios")) {
            CuriosUtil.addListeners(modbus, forgeBus);
        }
    }

    private void playerGlassesEquipment(final LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getSlot() == EquipmentSlot.HEAD) {
            if (event.getTo().is(BadEyes.GLASSES)) {
                Networking.sendMessageToPlayer(player, new DisableShaderPacket());
            } else if (event.getFrom().is(BadEyes.GLASSES)) {
                Networking.sendMessageToPlayer(player, new EnableShaderPacket());
            }
        }
    }

    private void playerRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        if (!event.isEndConquered()) {
            if (!hasGlasses(event.getEntity())) {
                Networking.sendMessageToPlayer((ServerPlayer) event.getEntity(), new EnableShaderPacket());
            }
        }
    }

    private void addBuiltInPack(final AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(pOnLoad -> {
                pOnLoad.accept(
                        Pack.readMetaAndCreate("one_twenty", Component.literal("One Twenty Fixes"), false, pId -> {
                                    return new PathPackResources(pId, ModList.get().getModFileById(MODID).getFile().findResource("data/badeyes/datapacks/" + pId), true);
                                },
                                PackType.SERVER_DATA, Pack.Position.TOP, optionalFeatureSource()
                        )
                );
            });
        }
    }

    private PackSource optionalFeatureSource() {
        return PackSource.create(component -> Component.translatable("pack.nameAndSource", component, Component.translatable("pack.source.feature")), false);
    }

    public static boolean hasGlasses(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).is(BadEyes.GLASSES) || (ModList.get().isLoaded("curios") && CuriosUtil.getGlasses(entity).isPresent());
    }

    public static GlassesSlot getGlasses(LivingEntity entity) {
        ItemStack itemStack = entity.getItemBySlot(EquipmentSlot.HEAD).is(GLASSES) ? entity.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY;
        boolean isCurio = false;
        if (itemStack.isEmpty() && ModList.get().isLoaded("curios")) {
            itemStack = CuriosUtil.getGlasses(entity).map(SlotResult::stack).orElse(ItemStack.EMPTY);
            isCurio = !itemStack.isEmpty();
        }
        return new GlassesSlot(isCurio, itemStack);
    }

    private void datagen(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        if (event.includeClient()) {
            BadEyesModelProviders.init(modelProvider -> generator.addProvider(true, modelProvider), packOutput, fileHelper);
        }
        generator.addProvider(event.includeClient(), new BadEyesLanguageProvider(packOutput, "en_us"));
        generator.addProvider(event.includeClient(), new ItemOverrideProvider(packOutput));
        generator.addProvider(event.includeClient(), new ClientTagsProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new AtlasProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new ClientMapsProviders(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new ClientRegistryMapProvider(packOutput, lookupProvider, fileHelper));

        generator.addProvider(event.includeServer(), new BadEyesTagProviders.BadEyesItemTagProvider(packOutput, lookupProvider, CompletableFuture.supplyAsync(TagsProvider.TagLookup::empty), MODID, fileHelper));
        generator.addProvider(event.includeServer(), new BadEyesRecipeProvider(packOutput));

        DataGenerator.PackGenerator oneTwentyPackGenerator = createPackGenerator(generator, packOutput, true, PackOutput.Target.DATA_PACK, BadEyes.MODID, "one_twenty");
        oneTwentyPackGenerator.addProvider(output -> PackMetadataGenerator.forFeaturePack(output, Component.literal("Fixes the mod for 1.20 and adds trims to glasses"), FeatureFlagSet.of(FeatureFlags.UPDATE_1_20)));
        oneTwentyPackGenerator.addProvider(BadEyesOneTwentyRecipeProvider::new);
        oneTwentyPackGenerator.addProvider(output -> new OneTwentyItemTagProvider(output, lookupProvider, fileHelper));
    }

    private DataGenerator.PackGenerator createPackGenerator(DataGenerator generator, PackOutput packOutput, boolean toRun, PackOutput.Target target, String namespace, String prefix) {
        Path path = packOutput.getOutputFolder(target)
                .resolve(namespace)
                .resolve(target == PackOutput.Target.DATA_PACK ? "datapacks" : "resourcepacks")
                .resolve(prefix);
        try { // Inner class, so we have to do this silly little thing
            return DataGenerator.PackGenerator.class.getConstructor(DataGenerator.class, boolean.class, String.class, PackOutput.class).newInstance(generator, toRun, prefix, new PackOutput(path));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
