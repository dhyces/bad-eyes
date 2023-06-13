package dhyces.badeyes.datagen;

import dhyces.badeyes.BadEyes;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class BadEyesLanguageProvider extends LanguageProvider {

    public BadEyesLanguageProvider(PackOutput output, String locale) {
        super(output, BadEyes.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        addItem(BadEyes.SIMPLE_GLASSES, "Glasses");
        addItem(BadEyes.NETHERITE_GLASSES, "Netherite Glasses");
    }
}
