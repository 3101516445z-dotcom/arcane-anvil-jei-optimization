package io.github.arcaneanviljeioptimization;

import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public final class ArcaneAnvilCategory {
    private static final ResourceLocation ID =
            new ResourceLocation("irons_spellbooks", "arcane_anvil");

    private ArcaneAnvilCategory() {}

    public static boolean matches(IRecipeCategory<?> category) {
        return ID.equals(category.getRecipeType().getUid());
    }

    public static Set<?> sorterStages(IRecipeCategory<?> category, Set<?> stages) {
        return matches(category) ? Set.of() : stages;
    }
}
