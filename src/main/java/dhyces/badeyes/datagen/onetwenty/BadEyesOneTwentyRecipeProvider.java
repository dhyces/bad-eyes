package dhyces.badeyes.datagen.onetwenty;

import dhyces.badeyes.BadEyes;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class BadEyesOneTwentyRecipeProvider extends RecipeProvider {

    public BadEyesOneTwentyRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(BadEyes.SIMPLE_GLASSES.get()), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.COMBAT, BadEyes.NETHERITE_GLASSES.get()).unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT)).save(pWriter, BadEyes.MODID + ":" + getItemName(BadEyes.NETHERITE_GLASSES.get()) + "_smithing");
    }


}
