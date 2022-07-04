package dhyces.badeyes.datagen;

import dhyces.badeyes.BadEyes;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class BadEyesLanguageProvider extends LanguageProvider {

    public BadEyesLanguageProvider(DataGenerator gen) {
        super(gen, BadEyes.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addItem(BadEyes.SIMPLE_GLASSES, "Glasses");
        addItem(BadEyes.NETHERITE_GLASSES, "Netherite Glasses");
    }
}
