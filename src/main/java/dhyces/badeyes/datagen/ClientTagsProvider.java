package dhyces.badeyes.datagen;

import dhyces.badeyes.BadEyes;
import dhyces.badeyes.BadEyesClient;
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
        clientTag(UncheckedClientTags.CUSTOM_TRIM_ITEM_TEXTURES)
                .add(BadEyes.id("trims/items/glasses_frame_trim"));
        clientTag(UncheckedClientTags.CUSTOM_TRIM_PATTERN_TEXTURES)
                .add(BadEyes.id("trims/models/glasses/frames_coast"))
                .add(BadEyes.id("trims/models/glasses/frames_dune"));
        clientTag(BadEyesClient.ADDITIONAL_MODELS)
                .add(BadEyes.id("armor/glasses"))
                .add(BadEyes.id("armor/simple_glasses"))
                .add(BadEyes.id("armor/netherite_glasses"));
        clientTag(BadEyesClient.LENS_SHAPES)
                .add(BadEyes.id("lens/shapes/lens_coast"))
                .add(BadEyes.id("lens/shapes/lens_dune"));
    }
}
