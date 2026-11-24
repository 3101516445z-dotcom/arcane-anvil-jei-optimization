package io.github.arcaneanviljeioptimization.mixin;

import io.github.arcaneanviljeioptimization.ArcaneVisibilityQuery;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.ingredients.RecipeIngredientSupplier.FocusLink;
import mezz.jei.library.recipes.RecipeManagerInternal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(value = RecipeManagerInternal.class, remap = false)
public abstract class RecipeVisibilityMixin {
    // 此处的调用方只判断结果是否为 null，以确定配方是否可见。
    @Redirect(
            method =
                    "isRecipeVisible(Lmezz/jei/library/recipes/collect/RecipeTypeData;Ljava/lang/Object;Lmezz/jei/api/recipe/IFocusGroup;)Z",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lmezz/jei/library/ingredients/RecipeIngredientSupplier$FocusLink;getVisibleIngredientIndexes(Lmezz/jei/api/recipe/IFocusGroup;Lmezz/jei/api/runtime/IIngredientManager;Ljava/util/function/Predicate;)Ljava/util/Set;"))
    private Set<Integer> arcaneOptimization$findVisibleIndex(
            FocusLink link,
            IFocusGroup focuses,
            IIngredientManager manager,
            Predicate<ITypedIngredient<?>> visible) {
        if (!focuses.isEmpty() || !ArcaneVisibilityQuery.active()) {
            return link.getVisibleIngredientIndexes(focuses, manager, visible);
        }
        var slots = link.slots();
        if (slots.isEmpty() || slots.get(0).ingredients().isEmpty()) return Set.of();
        int count = slots.get(0).ingredients().size();
        for (int index = 0; index < count; index++) {
            boolean matches = true;
            for (var slot : slots) {
                var ingredients = slot.ingredients();
                if (index >= ingredients.size()
                        || (ingredients.get(index) != null
                                && !ArcaneVisibilityQuery.visible(
                                        ingredients.get(index), visible))) {
                    matches = false;
                    break;
                }
            }
            if (matches) return Set.of();
        }
        return null;
    }
}
