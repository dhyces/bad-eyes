package dhyces.badeyes.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Optional;

public final class ClientConfig {
    private static ForgeConfigSpec.ConfigValue<? extends String> blurShader;

    public static Optional<ResourceLocation> getShader() {
        return Optional.ofNullable(blurShader.get().equals("none") ? null : new ResourceLocation(blurShader.get()));
    }

    public static ForgeConfigSpec build() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        blurShader = builder.comment("The shader used for the blur. Options are 'none', 'badeyes:box_blur', and 'badeyes:linear_blur' ").define("blurShader", "badeyes:box_blur");
        return builder.build();
    }
}
