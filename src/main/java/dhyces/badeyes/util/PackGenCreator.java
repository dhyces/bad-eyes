package dhyces.badeyes.util;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;

public interface PackGenCreator {
    static PackGenCreator getAs(DataGenerator generator) {
        return (PackGenCreator) generator;
    }

    DataGenerator.PackGenerator createPackGenerator(boolean toRun, PackOutput.Target target, String namespace, String prefix);
}
