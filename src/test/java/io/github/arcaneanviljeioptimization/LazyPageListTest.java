package io.github.arcaneanviljeioptimization;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public final class LazyPageListTest {
    public static void main(String[] args) {
        AtomicInteger calls = new AtomicInteger();
        var list = new LazyPageList<Integer>(100_000, i -> { calls.incrementAndGet(); return i; });
        require(list.size() == 100_000 && calls.get() == 0, "size eagerly built layouts");
        var page = new ArrayList<>(list.subList(10, 16));
        require(page.equals(java.util.List.of(10, 11, 12, 13, 14, 15)), "wrong page");
        require(calls.get() == 6, "built invisible layouts");
        list.get(10);
        require(calls.get() == 6, "cache miss");
        for (int i = 100; i < 165; i++) list.get(i);
        list.get(10);
        require(calls.get() == 72, "cache did not evict old page");
        try { list.get(100_000); throw new AssertionError("invalid index accepted"); }
        catch (IndexOutOfBoundsException expected) {}
        require(new LazyPageList<>(0, i -> { throw new AssertionError(); }).isEmpty(), "empty list");
        System.out.println("PASS: lazy pagination, exact visible range, reuse, bounded eviction, bounds, empty list");
    }
    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
