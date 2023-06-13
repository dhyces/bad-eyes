package dhyces.badeyes.datagen;

import dhyces.badeyes.BadEyes;
import dhyces.trimmed.api.data.ItemOverrideDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;

public class ItemOverrideProvider extends ItemOverrideDataProvider {
    public ItemOverrideProvider(PackOutput output) {
        super(output, BadEyes.MODID);
    }

    @Override
    protected void addItemOverrides() {
        ResourceKey<TrimMaterial>[] materials = new ResourceKey[] {
                TrimMaterials.COPPER,
                TrimMaterials.IRON,
                TrimMaterials.GOLD,
                TrimMaterials.DIAMOND,
                TrimMaterials.NETHERITE,
                TrimMaterials.EMERALD,
                TrimMaterials.AMETHYST,
                TrimMaterials.LAPIS,
                TrimMaterials.QUARTZ,
                TrimMaterials.REDSTONE
        };

        for (ResourceKey<TrimMaterial> materialKey : materials) {
            addTrimOverride(BadEyes.SIMPLE_GLASSES.get(), materialKey);
            addTrimOverride(BadEyes.NETHERITE_GLASSES.get(), materialKey);
        }
    }
}
