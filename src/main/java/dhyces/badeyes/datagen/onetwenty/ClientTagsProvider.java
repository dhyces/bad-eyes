package dhyces.badeyes.datagen.onetwenty;

import dhyces.badeyes.BadEyes;
import dhyces.trimmed.api.client.UncheckedClientTags;
import dhyces.trimmed.api.data.tags.ClientTagDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ClientTagsProvider extends ClientTagDataProvider {
    public ClientTagsProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, BadEyes.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        clientTag(UncheckedClientTags.CUSTOM_TRIM_ITEM_TEXTURES).add(BadEyes.id("trims/items/glasses_trim"));
    }
}
