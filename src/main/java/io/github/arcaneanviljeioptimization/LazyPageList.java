package io.github.arcaneanviljeioptimization;

import java.util.AbstractList;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.LongSupplier;

/** 客户端线程上的按需布局缓存；预热不会改变配方顺序。 */
public final class LazyPageList<T> extends AbstractList<T> {
    static final int CAPACITY = 1024;
    private static final int FORWARD_PAGES = 32;
    private static final int BACKWARD_PAGES = 16;
    private static final int MAX_BATCH_LAYOUTS = 2;
    private static final long BATCH_INTERVAL_NANOS = 50_000_000L;
    private static final long BATCH_BUDGET_NANOS = 1_000_000L;
    private final int size;
    private final IntFunction<T> factory;
    private final LongSupplier clock;
    private final LinkedHashMap<Integer, T> cache = new LinkedHashMap<>(64, .75f, true);
    private int from, to, direction = 1, nearCursor, farCursor;
    private int[] nearby = new int[0];
    private long lastBatch;
    private boolean hasBatch, warming;

    public LazyPageList(int size, IntFunction<T> factory) { this(size, factory, System::nanoTime); }
    LazyPageList(int size, IntFunction<T> factory, LongSupplier clock) {
        if (size < 0) throw new IllegalArgumentException("negative size");
        this.size = size;
        this.factory = Objects.requireNonNull(factory);
        this.clock = Objects.requireNonNull(clock);
    }
    @Override
    public int size() { return size; }
    @Override
    public T get(int index) {
        Objects.checkIndex(index, size);
        T value = cache.get(index);
        if (value != null) return value;
        value = Objects.requireNonNull(factory.apply(index));
        if (cache.size() >= CAPACITY) {
            // 优先淘汰远处布局；即使窗口很大也保持容量硬上限。
            Integer victim = null;
            for (Integer key : cache.keySet()) {
                if (key < from || key >= to) {
                    if (victim == null) victim = key;
                    if (!isNearby(key)) { victim = key; break; }
                }
            }
            if (victim == null) victim = cache.keySet().iterator().next();
            cache.remove(victim);
        }
        cache.put(index, value);
        return value;
    }
    private boolean isNearby(int index) {
        int page = Math.max(1, to - from);
        long low = (long) from - (direction > 0 ? (long) BACKWARD_PAGES : FORWARD_PAGES) * page;
        long high = (long) to + (direction > 0 ? (long) FORWARD_PAGES : BACKWARD_PAGES) * page;
        return index >= low && index < high;
    }
    public void visibleRange(int start, int end) {
        Objects.checkFromToIndex(start, end, size);
        if (warming && start == from && end == to) return;
        if (start != from) direction = start > from ? 1 : -1;
        from = start;
        to = end;
        int page = end - start;
        // 极端窗口尺寸下，预热任务本身也不能无限增长。
        int count = (int) Math.min(CAPACITY - 1L, ((long) FORWARD_PAGES + BACKWARD_PAGES) * page);
        nearby = new int[count];
        int n = 0;
        for (int side = 0; side < 2 && n < count; side++) {
            int step = side == 0 ? direction : -direction;
            long limit = (side == 0 ? (long) FORWARD_PAGES : BACKWARD_PAGES) * page;
            for (long offset = 0; offset < limit && n < count; offset++) {
                long index = step > 0 ? (long) end + offset : (long) start - 1 - offset;
                if (index < 0 || index >= size) break;
                nearby[n++] = (int) index;
            }
        }
        nearby = java.util.Arrays.copyOf(nearby, n);
        nearCursor = 0;
        warming = page > 0;
    }
    /** 预算只限制是否继续创建；单条布局创建不能中途抢占。 */
    public void warm() {
        if (!warming) return;
        long now = clock.getAsLong();
        if (hasBatch && now - lastBatch < BATCH_INTERVAL_NANOS) return;
        lastBatch = now;
        hasBatch = true;
        int built = 0;
        while (built < MAX_BATCH_LAYOUTS && clock.getAsLong() - now < BATCH_BUDGET_NANOS) {
            int index;
            if (nearCursor < nearby.length) index = nearby[nearCursor++];
            else {
                if (cache.size() >= CAPACITY || farCursor >= size) return;
                index = farCursor++;
            }
            if (!cache.containsKey(index)) { get(index); built++; }
        }
    }
    public void clearCache() {
        cache.clear();
        nearby = new int[0];
        from = to = nearCursor = farCursor = 0;
        direction = 1;
        warming = hasBatch = false;
    }
    int cachedCount() { return cache.size(); }
    boolean isCached(int index) { return cache.containsKey(index); }
}
