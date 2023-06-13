package dhyces.badeyes.datagen;

import dhyces.badeyes.BadEyes;
import dhyces.badeyes.BadEyesClient;
import dhyces.trimmed.api.data.maps.ClientMapDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ClientMapsProviders extends ClientMapDataProvider {
    public ClientMapsProviders(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, BadEyes.MODID, existingFileHelper);
    }

    @Override
    protected void addMaps() {
        map(BadEyesClient.LENS_COLORS)
                .put(BadEyes.id("lens/colors/simple"), "simple")
                .put(BadEyes.id("lens/colors/netherite"), "netherite");
    }
}
