package io.github.arcaneanviljeioptimization.mixin;

import io.github.arcaneanviljeioptimization.ArcaneLayouts;
import io.github.arcaneanviljeioptimization.LayoutFactoryBridge;

import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.common.config.RecipeSorterStage;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtonsFactory;
import mezz.jei.gui.recipes.RecipeGuiLogic;
import mezz.jei.gui.recipes.layouts.IRecipeLayoutList;
import mezz.jei.gui.recipes.lookups.IFocusedRecipes;
import mezz.jei.gui.recipes.lookups.ILookupState;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(value = RecipeGuiLogic.class, remap = false)
public abstract class InterfaceFactoryMixin {
    @Shadow @Final private IRecipeManager recipeManager;
    @Shadow @Final private IRecipeLayoutWithButtonsFactory recipeLayoutFactory;
    @Shadow private ILookupState state;

    @Inject(method = "createRecipeLayoutsWithButtons", at = @At("HEAD"), cancellable = true)
    private <T> void arcaneOptimization$lazy(
            Set<RecipeSorterStage> sorters,
            IFocusedRecipes<T> focused,
            CallbackInfoReturnable<IRecipeLayoutList> cir) {
        var layouts =
                ArcaneLayouts.createWithFactory(
                        focused,
                        state.getFocuses(),
                        recipeManager,
                        layout -> LayoutFactoryBridge.create(recipeLayoutFactory, layout));
        if (layouts != null) cir.setReturnValue(layouts);
    }
}
