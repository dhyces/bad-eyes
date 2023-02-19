package dhyces.badeyes.datagen;

import dhyces.badeyes.BadEyes;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class BadEyesRecipeProvider extends RecipeProvider {

    public BadEyesRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BadEyes.SIMPLE_GLASSES.get()).define('I', Tags.Items.NUGGETS_IRON).define('G', Tags.Items.GLASS_PANES).pattern("I I").pattern("GIG").unlockedBy(getHasName(BadEyes.SIMPLE_GLASSES.get()), has(BadEyes.SIMPLE_GLASSES.get())).save(pWriter);
        UpgradeRecipeBuilder.smithing(Ingredient.of(BadEyes.SIMPLE_GLASSES.get()), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.COMBAT, BadEyes.NETHERITE_GLASSES.get()).unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT)).save(pWriter, BadEyes.MODID + ":" + getItemName(BadEyes.NETHERITE_GLASSES.get()) + "_smithing");
    }
}
