package io.github.arcaneanviljeioptimization;

import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.common.Internal;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtonsFactory;
import mezz.jei.gui.recipes.layouts.IRecipeLayoutList;
import mezz.jei.gui.recipes.layouts.RecipeLayoutDrawableErrored;
import mezz.jei.gui.recipes.lookups.IFocusedRecipes;

import java.util.List;
import java.util.function.Function;

public final class ArcaneLayouts {
    private ArcaneLayouts() {}

    public static <T> IRecipeLayoutList create(
            IFocusedRecipes<T> focused,
            IFocusGroup focuses,
            IRecipeManager manager,
            IRecipeLayoutWithButtonsFactory factory) {
        return createWithFactory(focused, focuses, manager, factory::create);
    }

    public static <T> IRecipeLayoutList createWithFactory(
            IFocusedRecipes<T> focused,
            IFocusGroup focuses,
            IRecipeManager manager,
            Function<IRecipeLayoutDrawable<T>, ?> factory) {
        var category = focused.getRecipeCategory();
        LayoutCacheLifecycle.clear();
        if (!ArcaneAnvilCategory.matches(category)) return null;
        // 保留原配方对象，让铁魔法及附属的过滤逻辑继续生效。
        var recipes = focused.getRecipes();
        LazyPageList<?> layouts =
                new LazyPageList<>(
                        recipes.size(),
                        index -> {
                            T recipe = recipes.get(index);
                            var layout =
                                    manager.createRecipeLayoutDrawable(category, recipe, focuses)
                                            .orElseGet(
                                                    () -> {
                                                        manager.hideRecipes(
                                                                category.getRecipeType(),
                                                                List.of(recipe));
                                                        return new RecipeLayoutDrawableErrored<>(
                                                                category,
                                                                recipe,
                                                                Internal.getTextures()
                                                                        .getRecipeBackground(),
                                                                4);
                                                    });
                            return factory.apply(layout);
                        });
        // 收藏/可制作排序会提前遍历布局，因此此分类保持原配方顺序。
        return new PagedRecipeLayouts(layouts);
    }
}
