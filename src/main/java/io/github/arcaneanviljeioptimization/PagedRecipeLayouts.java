package io.github.arcaneanviljeioptimization;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import mezz.jei.gui.recipes.RecipeLayoutWithButtons;
import mezz.jei.gui.recipes.layouts.IRecipeLayoutList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PagedRecipeLayouts implements IRecipeLayoutList {
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean prefetchFailed;
    private final LazyPageList<RecipeLayoutWithButtons<?>> layouts;
    public PagedRecipeLayouts(LazyPageList<RecipeLayoutWithButtons<?>> layouts) { this.layouts = layouts; }
    @Override
    public int size() { return layouts.size(); }
    @Override
    public List<RecipeLayoutWithButtons<?>> subList(int from, int to) {
        LayoutCacheLifecycle.activate(this);
        layouts.visibleRange(from, to);
        return new ArrayList<>(layouts.subList(from, to));
    }
    @Override
    public Optional<RecipeLayoutWithButtons<?>> findFirst() {
        LayoutCacheLifecycle.activate(this);
        return layouts.isEmpty() ? Optional.empty() : Optional.of(layouts.get(0));
    }
    @Override
    public void tick() {
        if (!prefetchFailed && LayoutCacheLifecycle.isActive(this)) {
            try {
                layouts.warm();
            } catch (RuntimeException exception) {
                prefetchFailed = true;
                LOGGER.warn("Arcane anvil layout prefetch stopped for this query; visible pages remain on demand", exception);
            }
        }
    }
    void clearCache() {
        layouts.clearCache();
        prefetchFailed = false;
    }
}
