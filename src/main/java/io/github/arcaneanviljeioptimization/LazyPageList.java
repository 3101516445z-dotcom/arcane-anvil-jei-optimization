package io.github.arcaneanviljeioptimization;

import java.util.AbstractList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;

/** 查询数量时不创建布局，仅缓存最近访问的 64 个布局。仅供客户端线程使用。 */
public final class LazyPageList<T> extends AbstractList<T> {
    private final int size;
    private final IntFunction<T> factory;
    private final Map<Integer, T> cache = new LinkedHashMap<>(64, .75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, T> entry) {
            return size() > 64;
        }
    };
    public LazyPageList(int size, IntFunction<T> factory) {
        if (size < 0) throw new IllegalArgumentException("negative size");
        this.size = size;
        this.factory = Objects.requireNonNull(factory);
    }
    @Override
    public int size() { return size; }
    @Override
    public T get(int index) {
        Objects.checkIndex(index, size);
        return cache.computeIfAbsent(index, factory::apply);
    }
}
