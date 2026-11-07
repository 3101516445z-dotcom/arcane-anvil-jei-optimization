# 版本记录

## 1.0.2 — 2026-11-07

### 中文

- 新增渐进式配方布局预加载：当前页先显示，再优先准备翻页方向的 32 页和反方向的 16 页，随后逐步缓存当前查询的其他配方。
- 缓存上限从 64 条提高至 1,024 条，优先保留当前页和附近页；达到上限后停止远处配方的预加载。
- 预加载在客户端主线程分批执行，每批最多创建 2 条布局，耗时预算为 1 毫秒，两批间隔至少 50 毫秒。
- 切换查询、关闭配方界面、退出世界或更新配方数据时清理缓存和待加载任务，避免旧查询的缓存持续积压。
- 预加载遇到异常时停止该次预热，保留按需加载页面的方式。

### English

- Added incremental recipe layout preloading. The current page appears first, followed by up to 32 pages in the browsing direction and 16 pages in the opposite direction. Remaining recipes in the current query are then cached gradually.
- Increased the cache limit from 64 to 1,024 layouts, prioritizing the current and nearby pages. Preloading distant recipes stops when the cache is full.
- Preloading runs in small batches on the client thread, with at most 2 new layouts per batch, a 1 ms time budget, and at least 50 ms between batches.
- Clear cached layouts and pending preload work when changing queries, closing the recipe screen, leaving a world, or updating recipe data to prevent old query caches from accumulating.
- Stop preloading for the current query if an exception occurs, while keeping on-demand page loading available.

## 1.0.1 — 2026-11-07

- 铁魔法支持范围扩大至 1.20.1-3.4.0.11 ～ 1.20.1-3.16.3。
- JEI 支持范围扩大至 15.20.0.132 ～ 15.28.1.166，保留对后续不兼容接口版本的限制。
- 精简使用说明。

## 1.0.0 — 2026-11-07

首个正式版。

- 奥术铁砧 JEI 配方布局按需创建，并缓存最近 64 项。
- 保留铁魔法和附属的原配方与注入限制。
- 补齐资源包元信息；限定已核查的铁魔法和 JEI 版本。
- 奥术铁砧分类不执行收藏优先、可制作优先排序，收藏功能保留。
