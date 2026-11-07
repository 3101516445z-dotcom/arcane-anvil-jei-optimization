package io.github.arcaneanviljeioptimization;
import mezz.jei.gui.recipes.RecipeLayoutWithButtons;
import mezz.jei.gui.recipes.layouts.IRecipeLayoutList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PagedRecipeLayouts implements IRecipeLayoutList {
    private final List<RecipeLayoutWithButtons<?>> layouts;
    public PagedRecipeLayouts(List<RecipeLayoutWithButtons<?>> layouts) { this.layouts = layouts; }
    @Override
    public int size() { return layouts.size(); }
    @Override
    public List<RecipeLayoutWithButtons<?>> subList(int from, int to) {
        return new ArrayList<>(layouts.subList(from, to));
    }
    @Override
    public Optional<RecipeLayoutWithButtons<?>> findFirst() {
        return layouts.isEmpty() ? Optional.empty() : Optional.of(layouts.get(0));
    }
    @Override
    // 不执行会遍历全部布局的动态排序；可见布局由 JEI 界面管理。
    public void tick() {}
}
