package io.github.arcaneanviljeioptimization;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class LazyPageListTest {
    public static void main(String[] args) {
        AtomicLong clock = new AtomicLong();
        List<Integer> built = new ArrayList<>();
        var list = new LazyPageList<Integer>(100_000, index -> {
            built.add(index);
            return index;
        }, clock::get);

        check(list.size() == 100_000 && built.isEmpty(), "size must not create layouts");
        list.visibleRange(600, 606);
        check(new ArrayList<>(list.subList(600, 606)).equals(List.of(600, 601, 602, 603, 604, 605)),
                "visible page must preserve recipe order");
        list.warm();
        check(built.subList(6, 8).equals(List.of(606, 607)), "forward priority and two-layout limit");
        list.warm();
        check(built.size() == 8, "batch interval must prevent immediate repeat work");

        advanceBatches(list, clock, 143);
        check(list.isCached(797) && list.isCached(504), "32 forward and 16 backward pages");
        list.visibleRange(594, 600);
        advanceBatches(list, clock, 1);
        check(list.isCached(503), "reverse browsing must update preload priority");
        advanceBatches(list, clock, 1_000);
        check(list.cachedCount() == 1_024, "cache must stop at capacity");
        for (int index = 2_000; index < 4_000; index++) {
            list.get(index);
        }
        check(list.cachedCount() == 1_024 && list.isCached(600), "eviction must protect nearby layouts");

        list.clearCache();
        int before = built.size();
        advanceBatches(list, clock, 1);
        check(list.cachedCount() == 0 && built.size() == before, "clear must discard pending work");
        list.visibleRange(0, 6);
        list.get(0);
        list.warm();
        check(list.isCached(6), "reopening must resume preloading");

        var small = new LazyPageList<Integer>(624, index -> index, clock::get);
        small.visibleRange(0, 6);
        advanceBatches(small, clock, 400);
        check(small.cachedCount() == 624, "queries below capacity must eventually warm fully");

        var costly = new LazyPageList<Integer>(50, index -> {
            clock.addAndGet(2_000_000);
            return index;
        }, clock::get);
        costly.visibleRange(0, 6);
        costly.warm();
        check(costly.cachedCount() == 1, "an expensive layout must end the current batch");

        try {
            list.get(100_000);
            throw new AssertionError("out-of-range access accepted");
        } catch (IndexOutOfBoundsException expected) {
            // AbstractList's index contract remains intact.
        }
        var empty = new LazyPageList<Integer>(0, index -> {
            throw new AssertionError("empty query created a layout");
        });
        empty.visibleRange(0, 0);
        empty.warm();
        check(empty.isEmpty(), "empty query");

        for (int session = 0; session < 1_000; session++) {
            list.visibleRange(100, 106);
            advanceBatches(list, clock, 10);
            list.clearCache();
            check(list.cachedCount() == 0, "repeated close must clear cached layouts");
            advanceBatches(list, clock, 1);
            check(list.cachedCount() == 0, "closed query must remain idle");
        }
        System.out.println("PASS: lazy display, directional prefetch, time/count budgets, full warming, "
                + "bounded eviction, cleanup, reopening, bounds");
    }

    private static void advanceBatches(LazyPageList<?> list, AtomicLong clock, int batches) {
        for (int batch = 0; batch < batches; batch++) {
            clock.addAndGet(50_000_000);
            list.warm();
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
