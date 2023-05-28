package dhyces.badeyes.mixins;

import dhyces.badeyes.util.PackGenCreator;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

@Mixin(DataGenerator.class)
public class DataGeneratorMixin implements PackGenCreator {


    @Shadow @Final private PackOutput vanillaPackOutput;

    @Override
    public DataGenerator.PackGenerator createPackGenerator(boolean toRun, PackOutput.Target target, String namespace, String prefix) {
        Path path = this.vanillaPackOutput.getOutputFolder(target)
                .resolve(namespace)
                .resolve(target == PackOutput.Target.DATA_PACK ? "datapacks" : "resourcepacks")
                .resolve(prefix);
        try { // Inner class, so we have to do this silly little thing
            return DataGenerator.PackGenerator.class.getConstructor(DataGenerator.class, boolean.class, String.class, PackOutput.class).newInstance((DataGenerator)(Object)this, toRun, prefix, new PackOutput(path));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
