package dhyces.badeyes.datagen;

import dhyces.badeyes.BadEyes;
import dhyces.badeyes.BadEyesClient;
import dhyces.trimmed.impl.client.atlas.OpenPalettedPermutations;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

public class AtlasProvider extends SpriteSourceProvider {
    public AtlasProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper, BadEyes.MODID);
    }

    @Override
    protected void addSources() {
        atlas(new ResourceLocation("armor_trims")).addSource(new OpenPalettedPermutations(BadEyes.id("lens/colors/palette"), BadEyesClient.LENS_COLORS, BadEyesClient.LENS_SHAPES));
    }
}
