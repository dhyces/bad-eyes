package dhyces.badeyes.datagen;

import dhyces.badeyes.BadEyes;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class BadEyesModelProviders {
    private static final Set<ResourceKey<TrimMaterial>> MATERIALS = Util.make(new HashSet<>(), trimMaterials -> {
        trimMaterials.add(TrimMaterials.IRON);
        trimMaterials.add(TrimMaterials.GOLD);
        trimMaterials.add(TrimMaterials.DIAMOND);
        trimMaterials.add(TrimMaterials.NETHERITE);
        trimMaterials.add(TrimMaterials.COPPER);
        trimMaterials.add(TrimMaterials.EMERALD);
        trimMaterials.add(TrimMaterials.AMETHYST);
        trimMaterials.add(TrimMaterials.LAPIS);
        trimMaterials.add(TrimMaterials.QUARTZ);
        trimMaterials.add(TrimMaterials.REDSTONE);
    });

    public static void init(Consumer<ModelProvider<?>> consumer, PackOutput output, ExistingFileHelper existingFileHelper) {
        consumer.accept(new BadEyesItemModelProvider(output, existingFileHelper));
    }

    public static class BadEyesItemModelProvider extends ItemModelProvider {

        public BadEyesItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, BadEyes.MODID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            doubleTexture("simple_glasses", mcLoc("item/generated"), modLoc("item/simple_frames"), modLoc("item/simple_lens"));
            doubleTexture("netherite_glasses", mcLoc("item/generated"), modLoc("item/netherite_frames"), modLoc("item/netherite_lens"));
            MATERIALS.forEach(key -> {
                getBuilder("simple_glasses_" + key.location().getPath() + "_trim")
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", "badeyes:item/simple_lens")
                        .texture("layer1", trackGeneratedTexture("badeyes:trims/items/glasses_frame_trim_" + key.location().getPath()));
                getBuilder("netherite_glasses_" + key.location().getPath() + "_trim")
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", "badeyes:item/netherite_lens")
                        .texture("layer1", trackGeneratedTexture("badeyes:trims/items/glasses_frame_trim_" + key.location().getPath()));
            });
        }

        protected void doubleTexture(String name, ResourceLocation parent, ResourceLocation layer0Tex, ResourceLocation layer1Tex) {
            getBuilder(name)
                    .parent(new ModelFile.UncheckedModelFile(parent))
                    .texture("layer0", layer0Tex)
                    .texture("layer1", layer1Tex);
        }

        private void createArmors(ResourceLocation trimMaterial, Set<ArmorMaterial> materials) {
            for (ArmorMaterial material : materials) {
                if (!(material == ArmorMaterials.LEATHER)) {
                    createTwoLayerArmor(material, trimMaterial);
                } else {
                    createThreeLayerArmor(material, trimMaterial);
                }
            }
        }

        private void createTwoLayerArmor(ArmorMaterial material, ResourceLocation trimMaterial) {
            uploadTwoLayerArmor(material, ArmorItem.Type.BOOTS, trimMaterial);
            uploadTwoLayerArmor(material, ArmorItem.Type.LEGGINGS, trimMaterial);
            uploadTwoLayerArmor(material, ArmorItem.Type.CHESTPLATE, trimMaterial);
            uploadTwoLayerArmor(material, ArmorItem.Type.HELMET, trimMaterial);
        }


        private void uploadTwoLayerArmor(ArmorMaterial material, ArmorItem.Type type, ResourceLocation trimMaterial) {
            String armorMaterialName = material == ArmorMaterials.GOLD ? "golden" : material.getName();
            ResourceLocation id = new ResourceLocation(trimMaterial.getNamespace(), "item/%s_%s_%s_trim".formatted(armorMaterialName, type.getName(), trimMaterial.getPath()));
            ResourceLocation layer0Id = trackGeneratedTexture(new ResourceLocation("item/%s_%s".formatted(armorMaterialName, type.getName())));
            ResourceLocation layer1Id = trackGeneratedTexture(new ResourceLocation("trims/items/%s_trim_%s".formatted(type.getName(), trimMaterial.getPath())));
            withExistingParent(id.getPath(), "item/generated")
                    .texture("layer0", layer0Id.toString())
                    .texture("layer1", layer1Id.toString());

        }

        private void createThreeLayerArmor(ArmorMaterial material, ResourceLocation trimMaterial) {
            uploadThreeLayerArmor(material, ArmorItem.Type.BOOTS, trimMaterial);
            uploadThreeLayerArmor(material, ArmorItem.Type.LEGGINGS, trimMaterial);
            uploadThreeLayerArmor(material, ArmorItem.Type.CHESTPLATE, trimMaterial);
            uploadThreeLayerArmor(material, ArmorItem.Type.HELMET, trimMaterial);
        }


        private void uploadThreeLayerArmor(ArmorMaterial material, ArmorItem.Type type, ResourceLocation trimMaterial) {
            ResourceLocation id = new ResourceLocation(trimMaterial.getNamespace(), "item/%s_%s_%s_trim".formatted(material.getName(), type.getName(), trimMaterial.getPath()));
            ResourceLocation layer0Id = trackGeneratedTexture(new ResourceLocation("item/%s_%s".formatted(material.getName(), type.getName())));
            ResourceLocation layer1Id = trackGeneratedTexture(new ResourceLocation("item/%s_%s_overlay".formatted(material.getName(), type.getName())));
            ResourceLocation layer2Id = trackGeneratedTexture(new ResourceLocation("trims/items/%s_trim_%s".formatted(type.getName(), trimMaterial.getPath())));
            withExistingParent(id.getPath(), "item/generated")
                    .texture("layer0", layer0Id.toString())
                    .texture("layer1", layer1Id.toString())
                    .texture("layer2", layer2Id.toString());
        }

        private ResourceLocation trackGeneratedTexture(ResourceLocation location) {
            existingFileHelper.trackGenerated(location, PackType.CLIENT_RESOURCES, ".png", "textures");
            return location;
        }

        private String trackGeneratedTexture(String location) {
            existingFileHelper.trackGenerated(new ResourceLocation(location), PackType.CLIENT_RESOURCES, ".png", "textures");
            return location;
        }
    }
}
