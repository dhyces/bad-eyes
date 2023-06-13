package dhyces.badeyes.datagen;

import dhyces.badeyes.BadEyes;
import dhyces.badeyes.BadEyesClient;
import dhyces.trimmed.api.data.maps.ClientRegistryMapDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ClientRegistryMapProvider extends ClientRegistryMapDataProvider<Item> {
    public ClientRegistryMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, BadEyes.MODID, lookupProviderFuture, Registries.ITEM, existingFileHelper);
    }

    @Override
    protected void addMaps(HolderLookup.Provider lookupProvider) {
        registryAware(BadEyesClient.ITEM_LENS_SUFFIXES, lookupProvider)
                .put(BadEyes.SIMPLE_GLASSES, "simple")
                .put(BadEyes.NETHERITE_GLASSES, "netherite");
    }
}
