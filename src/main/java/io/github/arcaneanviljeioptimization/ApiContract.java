package io.github.arcaneanviljeioptimization;

import java.util.Set;

final class ApiContract {
    static boolean supported(ApiChecks.Nodes nodes, String target, String mixin) throws Exception {
        String owner = "mezz/jei/gui/recipes/RecipeGuiLogic";
        boolean bridge = mixin.endsWith(".InterfaceFactoryMixin");
        boolean menu = bridge || mixin.endsWith(".MenuRecipeGuiLogicMixin");
        String menuType = "Lnet/minecraft/world/inventory/AbstractContainerMenu;";
        String arguments = menu ? "" : menuType + "Lnet/minecraft/world/entity/player/Player;";
        if (!(ApiChecks.method(
                        nodes,
                        "mezz/jei/api/recipe/IRecipeManager",
                        "createRecipeLayoutDrawable",
                        "(Lmezz/jei/api/recipe/category/IRecipeCategory;Ljava/lang/Object;Lmezz/jei/api/recipe/IFocusGroup;)Ljava/util/Optional;",
                        9,
                        1)
                && ApiChecks.method(
                        nodes,
                        "mezz/jei/api/recipe/IRecipeManager",
                        "hideRecipes",
                        "(Lmezz/jei/api/recipe/RecipeType;Ljava/util/Collection;)V",
                        9,
                        1)
                && ApiChecks.method(
                        nodes,
                        "mezz/jei/api/recipe/RecipeType",
                        "getUid",
                        "()Lnet/minecraft/resources/ResourceLocation;",
                        9,
                        1)
                && ApiChecks.method(
                        nodes,
                        "mezz/jei/api/recipe/category/IRecipeCategory",
                        "getRecipeType",
                        "()Lmezz/jei/api/recipe/RecipeType;",
                        9,
                        1)
                && ApiChecks.method(
                        nodes,
                        "mezz/jei/common/Internal",
                        "getTextures",
                        "()Lmezz/jei/common/gui/textures/Textures;",
                        9,
                        9)
                && ApiChecks.method(
                        nodes,
                        "mezz/jei/common/gui/textures/Textures",
                        "getRecipeBackground",
                        "()Lmezz/jei/common/gui/elements/DrawableNineSliceTexture;",
                        9,
                        1)
                && ApiChecks.method(
                        nodes,
                        "mezz/jei/gui/recipes/IRecipeLayoutWithButtonsFactory",
                        "create",
                        "(Lmezz/jei/api/gui/IRecipeLayoutDrawable;)Lmezz/jei/gui/recipes/"
                                + (bridge
                                        ? "IRecipeLayoutWithButtons;"
                                        : "RecipeLayoutWithButtons;"),
                        9,
                        1)
                && ApiChecks.method(
                        nodes,
                        "mezz/jei/gui/recipes/layouts/RecipeLayoutDrawableErrored",
                        "<init>",
                        "(Lmezz/jei/api/recipe/category/IRecipeCategory;Ljava/lang/Object;Lmezz/jei/api/gui/drawable/IScalableDrawable;I)V",
                        9,
                        1)
                && ApiChecks.method(
                        nodes,
                        "mezz/jei/gui/recipes/lookups/IFocusedRecipes",
                        "getRecipeCategory",
                        "()Lmezz/jei/api/recipe/category/IRecipeCategory;",
                        9,
                        1)
                && ApiChecks.method(
                        nodes,
                        "mezz/jei/gui/recipes/lookups/IFocusedRecipes",
                        "getRecipes",
                        "()Ljava/util/List;",
                        9,
                        1)
                && ApiChecks.method(
                        nodes,
                        "mezz/jei/gui/recipes/lookups/ILookupState",
                        "getFocuses",
                        "()Lmezz/jei/api/recipe/IFocusGroup;",
                        9,
                        1)
                && ApiChecks.field(
                        nodes,
                        owner,
                        "recipeManager",
                        "Lmezz/jei/api/recipe/IRecipeManager;",
                        24,
                        16)
                && ApiChecks.field(
                        nodes,
                        owner,
                        "recipeLayoutFactory",
                        "Lmezz/jei/gui/recipes/IRecipeLayoutWithButtonsFactory;",
                        24,
                        16)
                && ApiChecks.field(
                        nodes, owner, "state", "Lmezz/jei/gui/recipes/lookups/ILookupState;", 8, 0)
                && ApiChecks.method(
                        nodes,
                        owner,
                        "createRecipeLayoutsWithButtons",
                        "(Ljava/util/Set;Lmezz/jei/gui/recipes/lookups/IFocusedRecipes;"
                                + arguments
                                + ")Lmezz/jei/gui/recipes/layouts/IRecipeLayoutList;",
                        0x508,
                        0))) return false;
        for (String type :
                Set.of(
                        "mezz/jei/api/recipe/IRecipeManager",
                        "mezz/jei/api/recipe/category/IRecipeCategory",
                        "mezz/jei/gui/recipes/IRecipeLayoutWithButtonsFactory",
                        "mezz/jei/gui/recipes/lookups/IFocusedRecipes",
                        "mezz/jei/gui/recipes/lookups/ILookupState"))
            if ((nodes.get(type).access & 0x200) == 0) return false;
        for (String type :
                Set.of(
                        owner,
                        "mezz/jei/api/recipe/RecipeType",
                        "mezz/jei/common/Internal",
                        "mezz/jei/common/gui/textures/Textures",
                        "mezz/jei/gui/recipes/layouts/RecipeLayoutDrawableErrored"))
            if ((nodes.get(type).access & 0x200) != 0) return false;
        var layout = nodes.get("mezz/jei/gui/recipes/layouts/IRecipeLayoutList");
        if ((layout.access & 0x200) == 0) return false;
        Set<String> implemented =
                menu
                        ? Set.of(
                                "size()I",
                                "subList(II" + menuType + ")Ljava/util/List;",
                                "findFirst(" + menuType + ")Ljava/util/Optional;",
                                "tick(" + menuType + ")V")
                        : Set.of(
                                "size()I",
                                "subList(II)Ljava/util/List;",
                                "findFirst()Ljava/util/Optional;",
                                "tick()V");
        if (!layout.interfaces.isEmpty()) return false;
        return layout.methods.stream()
                .filter(m -> (m.access & 0x400) != 0)
                .allMatch(m -> implemented.contains(m.name + m.desc));
    }
}
