package io.github.arcaneanviljeioptimization.mixin;

import io.github.arcaneanviljeioptimization.ArcaneAnvilCategory;
import io.github.arcaneanviljeioptimization.ArcaneVisibilityQuery;

import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.recipes.lookups.FocusedRecipes;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.Stream;

@Mixin(value = FocusedRecipes.class, remap = false)
public abstract class FocusedQueryMixin {
    @Shadow @Final private IRecipeCategory<?> recipeCategory;
    @Shadow @Final private IFocusGroup focuses;

    @Redirect(
            method = "getRecipes",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Ljava/util/stream/Stream;toList()Ljava/util/List;"))
    private List<?> arcaneOptimization$collect(Stream<?> stream) {
        if (focuses.isEmpty() && ArcaneAnvilCategory.matches(recipeCategory)) {
            return ArcaneVisibilityQuery.collect(stream);
        }
        return stream.toList();
    }
}
