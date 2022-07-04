package dhyces.badeyes.datagen;

import dhyces.badeyes.BadEyes;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class BadEyesTagProviders {

    public static class BadEyesItemTagProvider extends ForgeRegistryTagsProvider<Item> {

        public BadEyesItemTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
            super(generator, ForgeRegistries.ITEMS, BadEyes.MODID, existingFileHelper);
        }

        @Override
        protected void addTags() {
            tag(BadEyes.GLASSES_REPAIR_MATERIALS).add(Items.IRON_INGOT).add(Items.GLASS_PANE);
            tag(BadEyes.GLASSES).add(BadEyes.SIMPLE_GLASSES.get()).add(BadEyes.NETHERITE_GLASSES.get());
        }
    }
}
