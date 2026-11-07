# 奥术铁砧 JEI 优化

**Arcane Anvil JEI Optimization**

奥术铁砧 JEI 客户端优化模组，当前正式版本 **1.0.0**。

装有大量法术附属时，打开奥术铁砧配方可能长时间卡顿。本模组让 JEI 按需创建当前页面的配方布局，最多缓存最近访问的 64 个布局，减少打开页面时的集中计算。

## 安装与使用

| 依赖 | 支持版本 |
|---|---|
| Minecraft | 1.20.1 |
| Forge | 47.x（验证版本 47.4.20） |
| Iron's Spells 'n Spellbooks | 1.20.1-3.16.3 |
| JEI | 15.20.0.132 |

关闭游戏，将 `arcane-anvil-jei-optimization-1.0.0.jar` 放入客户端 `mods`，然后重新启动。仅需客户端安装，服务器无需安装。由于使用 JEI 内部接口，铁魔法和 JEI 采用精确版本限制。

照常在 JEI 中查看武器或法术卷轴的用途，或打开奥术铁砧分类即可。没有新增 GUI、按键或配置项。关闭游戏后移除本模组即可卸载；本模组不注册物品或存档数据。

## 行为与限制

- 保留原配方对象及附属的注入限制，不修改实际铁砧消耗、装备属性或法术效果。
- 配方数量和页数通常不会减少；优化的是创建页面布局的时机。
- 奥术铁砧分类保持原配方顺序，不执行“收藏优先”或“可制作优先”排序；收藏功能仍可使用。其他分类沿用 JEI 原实现。
- 首次进入世界时的 JEI 配方索引建立不在优化范围内。一条配方中的大量法术变体仍需计算，首次翻到新页面可能产生短暂开销。
- 不保证兼容所有附属或修改 JEI 同一内部方法的其他模组。

## 构建

使用 JDK 21，项目自带 Gradle 8.8 Wrapper，输出 Java 17 字节码。将完整的 `jei-1.20.1-forge-15.20.0.132.jar` 放入项目 `libs/`，然后运行：

Windows PowerShell：

```powershell
.\gradlew.bat clean build
```

Linux / macOS：

```shell
./gradlew clean build
```

首次构建需联网下载 ForgeGradle 等依赖；已缓存依赖后可添加 `--offline`。第三方 JEI JAR 不包含在源码发布包和成品中。产物位于 `build/libs/`。版本由 `build.gradle` 统一填写并展开至模组元信息。

`check` 自动执行 `verifyLazyPages`，检查按需创建、可见范围、缓存命中与淘汰、空列表和索引越界。独立测试不能代替完整客户端验证。

实现说明见 [DEVELOPMENT.md](DEVELOPMENT.md)，版本记录见 [CHANGELOG.md](CHANGELOG.md)。项目采用 [MIT 许可证](LICENSE)。
