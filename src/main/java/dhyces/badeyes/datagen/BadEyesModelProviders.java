package dhyces.badeyes.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BadEyesModelProviders {

    public static class BadEyesItemModelProvider extends ItemModelProvider {

        public BadEyesItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
            super(output, modid, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            this.singleTexture("simple_glasses", mcLoc("item/generated"), "layer0", modLoc("item/simple_glasses"));
            this.singleTexture("netherite_glasses", mcLoc("item/generated"), "layer0", modLoc("item/netherite_glasses"));
        }
    }
}
