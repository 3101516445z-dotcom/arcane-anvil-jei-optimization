# 版本记录

## 未发布 / Unreleased

### 中文

- JEI 最低支持版本下调至 15.20.0.100。

### English

- Extended JEI support to include version 15.20.0.100.

## 1.0.2 — 2026-11-07

### 中文

- 新增奥术铁砧配方渐进式预加载，优先准备当前浏览方向及附近页面，减少连续翻页时的即时加载。
- 配方布局缓存上限由 64 条提升至 1,024 条，优先保留当前页和附近页面。
- 完善缓存清理，在切换查询、关闭配方界面、退出世界或更新配方数据时释放缓存。
- 改进预加载异常处理；预加载中断时，仍可按需加载配方页面。

### English

- Added incremental preloading for Arcane Anvil recipes, prioritizing nearby pages and the current browsing direction to reduce on-demand loading during navigation.
- Increased the recipe layout cache limit from 64 to 1,024 entries, with priority given to the current and nearby pages.
- Improved cache cleanup when switching queries, closing the recipe screen, leaving a world, or updating recipe data.
- Improved error handling during preloading. If preloading stops, recipe pages remain available through on-demand loading.

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
