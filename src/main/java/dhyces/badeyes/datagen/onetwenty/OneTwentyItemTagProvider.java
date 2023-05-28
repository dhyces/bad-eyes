package dhyces.badeyes.datagen.onetwenty;

import dhyces.badeyes.BadEyes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class OneTwentyItemTagProvider extends ItemTagsProvider {
    public OneTwentyItemTagProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, CompletableFuture.supplyAsync(TagLookup::empty), BadEyes.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ItemTags.TRIMMABLE_ARMOR).add(BadEyes.SIMPLE_GLASSES.get()).add(BadEyes.NETHERITE_GLASSES.get());
    }
}
