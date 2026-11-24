package io.github.arcaneanviljeioptimization;

import mezz.jei.api.ingredients.ITypedIngredient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class ArcaneVisibilityQuery {
    private record ItemKey(Item item, int count, CompoundTag tag) {}

    private static final ThreadLocal<Map<ItemKey, Boolean>> ACTIVE = new ThreadLocal<>();

    private ArcaneVisibilityQuery() {}

    public static <T> List<T> collect(Stream<T> recipes) {
        var previous = ACTIVE.get();
        ACTIVE.set(new HashMap<>());
        try {
            return recipes.toList();
        } finally {
            if (previous == null) ACTIVE.remove();
            else ACTIVE.set(previous);
        }
    }

    public static boolean active() {
        return ACTIVE.get() != null;
    }

    public static boolean visible(
            ITypedIngredient<?> ingredient, Predicate<ITypedIngredient<?>> original) {
        // 这些 JEI 包装类只用物品、数量和标签重建物品栈。
        String type = ingredient.getClass().getName();
        if (!type.equals("mezz.jei.common.ingredients.itemStacks.NormalizedTypedItemStack")
                && !type.equals("mezz.jei.common.ingredients.itemStacks.NormalizedTypedItem")
                && !type.equals("mezz.jei.common.ingredients.itemStacks.FullTypedItemStack")) {
            return original.test(ingredient);
        }
        var stack = ingredient.getItemStack().orElseThrow();
        var key = new ItemKey(stack.getItem(), stack.getCount(), stack.getTag());
        return ACTIVE.get().computeIfAbsent(key, ignored -> original.test(ingredient));
    }
}
