package dhyces.badeyes.datagen;

import dhyces.badeyes.BadEyes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class BadEyesRecipeProvider extends RecipeProvider {
    public BadEyesRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ShapedRecipeBuilder.shaped(BadEyes.SIMPLE_GLASSES.get()).define('I', Tags.Items.NUGGETS_IRON).define('G', Tags.Items.GLASS_PANES).pattern("I I").pattern("GIG").unlockedBy(getHasName(BadEyes.SIMPLE_GLASSES.get()), has(BadEyes.SIMPLE_GLASSES.get())).save(pFinishedRecipeConsumer);
        UpgradeRecipeBuilder.smithing(Ingredient.of(BadEyes.SIMPLE_GLASSES.get()), Ingredient.of(Items.NETHERITE_INGOT), BadEyes.NETHERITE_GLASSES.get()).unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT)).save(pFinishedRecipeConsumer, BadEyes.MODID + ":" + getItemName(BadEyes.NETHERITE_GLASSES.get()) + "_smithing");
    }
}
