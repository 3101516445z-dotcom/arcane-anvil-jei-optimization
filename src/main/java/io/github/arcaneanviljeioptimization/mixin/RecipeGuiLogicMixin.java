package io.github.arcaneanviljeioptimization.mixin;

import io.github.arcaneanviljeioptimization.LazyPageList;
import io.github.arcaneanviljeioptimization.LayoutCacheLifecycle;
import io.github.arcaneanviljeioptimization.PagedRecipeLayouts;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.common.Internal;
import mezz.jei.common.config.RecipeSorterStage;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtonsFactory;
import mezz.jei.gui.recipes.RecipeGuiLogic;
import mezz.jei.gui.recipes.RecipeLayoutWithButtons;
import mezz.jei.gui.recipes.layouts.IRecipeLayoutList;
import mezz.jei.gui.recipes.layouts.RecipeLayoutDrawableErrored;
import mezz.jei.gui.recipes.lookups.IFocusedRecipes;
import mezz.jei.gui.recipes.lookups.ILookupState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Set;

@Mixin(value = RecipeGuiLogic.class, remap = false)
public abstract class RecipeGuiLogicMixin {
    @Shadow @Final private IRecipeManager recipeManager;
    @Shadow @Final private IRecipeLayoutWithButtonsFactory recipeLayoutFactory;
    @Shadow private ILookupState state;

    @Inject(method = "createRecipeLayoutsWithButtons", at = @At("HEAD"), cancellable = true)
    private <T> void arcaneOptimization$lazy(Set<RecipeSorterStage> sorters, IFocusedRecipes<T> focused,
            AbstractContainerMenu menu, Player player, CallbackInfoReturnable<IRecipeLayoutList> cir) {
        var category = focused.getRecipeCategory();
        LayoutCacheLifecycle.clear();
        if (!category.getRecipeType().getUid().toString().equals("irons_spellbooks:arcane_anvil")) return;
        // 保留原配方对象，让铁魔法及附属的过滤逻辑继续生效。
        var recipes = focused.getRecipes();
        var focuses = state.getFocuses();
        var manager = recipeManager;
        var factory = recipeLayoutFactory;
        LazyPageList<RecipeLayoutWithButtons<?>> layouts = new LazyPageList<>(recipes.size(), index -> {
            T recipe = recipes.get(index);
            var layout = manager.createRecipeLayoutDrawable(category, recipe, focuses).orElseGet(() -> {
                manager.hideRecipes(category.getRecipeType(), List.of(recipe));
                return new RecipeLayoutDrawableErrored<>(category, recipe,
                        Internal.getTextures().getRecipeBackground(), 4);
            });
            return factory.create(layout);
        });
        // 收藏/可制作排序会提前遍历布局，因此此分类保持原配方顺序。
        cir.setReturnValue(new PagedRecipeLayouts(layouts));
    }
}
