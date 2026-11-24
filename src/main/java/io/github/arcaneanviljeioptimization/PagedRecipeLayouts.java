package io.github.arcaneanviljeioptimization;

import com.mojang.logging.LogUtils;

import mezz.jei.gui.recipes.RecipeLayoutWithButtons;
import mezz.jei.gui.recipes.layouts.IRecipeLayoutList;

import net.minecraft.world.inventory.AbstractContainerMenu;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PagedRecipeLayouts implements IRecipeLayoutList {
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean prefetchFailed;
    private final LazyPageList<?> layouts;

    public PagedRecipeLayouts(LazyPageList<?> layouts) {
        this.layouts = layouts;
    }

    @Override
    public int size() {
        return layouts.size();
    }

    // 旧版接口返回具体布局类，带菜单的接口允许布局接口的其他实现。
    @Override
    @SuppressWarnings("unchecked")
    public List<RecipeLayoutWithButtons<?>> subList(int from, int to) {
        return (List<RecipeLayoutWithButtons<?>>) subList(from, to, null);
    }

    @Override
    public Optional<RecipeLayoutWithButtons<?>> findFirst() {
        return findFirst(null).map(RecipeLayoutWithButtons.class::cast);
    }

    @Override
    public void tick() {
        if (!prefetchFailed && LayoutCacheLifecycle.isActive(this)) {
            try {
                layouts.warm();
            } catch (RuntimeException exception) {
                prefetchFailed = true;
                LOGGER.warn(
                        "Arcane anvil layout prefetch stopped for this query; visible pages remain on demand",
                        exception);
            }
        }
    }

    // JEI 的新版分页接口在关闭排序时也会传入菜单。
    public List<?> subList(int from, int to, AbstractContainerMenu menu) {
        LayoutCacheLifecycle.activate(this);
        layouts.visibleRange(from, to);
        return new ArrayList<>(layouts.subList(from, to));
    }

    public Optional<?> findFirst(AbstractContainerMenu menu) {
        LayoutCacheLifecycle.activate(this);
        return layouts.isEmpty() ? Optional.empty() : Optional.of(layouts.get(0));
    }

    public void tick(AbstractContainerMenu menu) {
        tick();
    }

    void clearCache() {
        layouts.clearCache();
        prefetchFailed = false;
    }
}
