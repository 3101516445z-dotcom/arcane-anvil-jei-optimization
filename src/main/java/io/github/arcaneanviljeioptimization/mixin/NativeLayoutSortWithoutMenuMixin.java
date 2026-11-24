package io.github.arcaneanviljeioptimization.mixin;

import io.github.arcaneanviljeioptimization.ArcaneAnvilCategory;

import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtonsFactory;
import mezz.jei.gui.recipes.RecipeGuiLogic;
import mezz.jei.gui.recipes.lookups.IFocusedRecipes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Set;

@Mixin(value = RecipeGuiLogic.class, remap = false)
public abstract class NativeLayoutSortWithoutMenuMixin {
    @ModifyArg(
            index = 0,
            method = "getVisibleRecipeLayoutsWithButtons",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lmezz/jei/gui/recipes/layouts/IRecipeLayoutList;create(Ljava/util/Set;Lmezz/jei/gui/recipes/lookups/IFocusedRecipes;Lmezz/jei/api/recipe/IFocusGroup;Lmezz/jei/gui/bookmarks/BookmarkList;Lmezz/jei/api/recipe/IRecipeManager;Lmezz/jei/gui/recipes/IRecipeLayoutWithButtonsFactory;)Lmezz/jei/gui/recipes/layouts/IRecipeLayoutList;"))
    private Set<?> arcaneOptimization$useRecipeOrder(
            Set<?> stages,
            IFocusedRecipes<?> focused,
            IFocusGroup focuses,
            BookmarkList bookmarks,
            IRecipeManager manager,
            IRecipeLayoutWithButtonsFactory factory) {
        return ArcaneAnvilCategory.sorterStages(focused.getRecipeCategory(), stages);
    }
}
