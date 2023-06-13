package dhyces.badeyes.datagen;

import dhyces.badeyes.BadEyes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BadEyesTagProviders {

    public static class BadEyesItemTagProvider extends ItemTagsProvider {

        public BadEyesItemTagProvider(PackOutput p_255871_, CompletableFuture<HolderLookup.Provider> p_256035_, CompletableFuture<TagLookup<Block>> p_256467_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(p_255871_, p_256035_, p_256467_, modId, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            tag(BadEyes.GLASSES_REPAIR_MATERIALS).add(Items.IRON_INGOT).add(Items.GLASS_PANE);
            tag(BadEyes.GLASSES).add(BadEyes.SIMPLE_GLASSES.get()).add(BadEyes.NETHERITE_GLASSES.get());
            tag(ItemTags.TRIMMABLE_ARMOR).add(BadEyes.SIMPLE_GLASSES.get()).add(BadEyes.NETHERITE_GLASSES.get());
        }
    }
}
