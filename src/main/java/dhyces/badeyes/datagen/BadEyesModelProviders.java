package dhyces.badeyes.datagen;

import dhyces.badeyes.BadEyes;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BadEyesModelProviders {

    public static class BadEyesItemModelProvider extends ItemModelProvider {

        public BadEyesItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator, BadEyes.MODID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            this.singleTexture("simple_glasses", mcLoc("item/generated"), "layer0", modLoc("item/simple_glasses"));
            this.singleTexture("netherite_glasses", mcLoc("item/generated"), "layer0", modLoc("item/netherite_glasses"));
        }
    }
}
