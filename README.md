# 奥术铁砧 JEI 优化

**Arcane Anvil JEI Optimization · 1.0.1**

减少大量法术附属带来的奥术铁砧 JEI 页面卡顿。按需创建当前页的配方布局，并缓存最近访问的 64 个布局，让打开配方页面更流畅。

## 安装与使用

| 依赖 | 支持版本 |
|---|---|
| Minecraft | 1.20.1 |
| Forge | 47.x（实测 47.4.20） |
| Iron's Spells 'n Spellbooks | 1.20.1-3.4.0.11 ～ 1.20.1-3.16.3 |
| JEI | 15.20.0.132 ～ 15.28.1.166 |

将 `arcane-anvil-jei-optimization-1.0.1.jar` 放入客户端 `mods`，重新启动游戏即可，服务器无需安装。

照常在 JEI 中查看武器、法术卷轴的用途，或打开奥术铁砧分类，无需额外设置。卸载时关闭游戏并移除 JAR。

## 使用说明

- 保留原配方、附属注入规则以及实际铁砧操作。
- 配方数量与页数保持原样，优化集中在打开配方页面；进入世界时的 JEI 索引加载仍沿用原流程。
- 奥术铁砧分类按原配方顺序显示，不执行“收藏优先”和“可制作优先”排序；收藏能力沿用原版。其他分类不变。

支持范围依据代表版本的客户端对照测试和接口检查确定，实测组合见 [兼容性验证](DEVELOPMENT.md#兼容性验证)。JEI 15.28.1.167 起的已检查版本改动了相关内部接口，当前版本不支持。

## 构建

使用 JDK 21 和项目自带的 Gradle 8.8 Wrapper，输出 Java 17 字节码。将 `jei-1.20.1-forge-15.20.0.132.jar` 放入 `libs/`，然后运行：

Windows PowerShell：

```powershell
.\gradlew.bat clean build
```

Linux / macOS：

```shell
./gradlew clean build
```

首次构建会下载构建依赖，缓存齐全后可添加 `--offline`。产物位于 `build/libs/`，JEI 依赖不打入成品。构建自动执行分页与缓存测试。

实现说明见 [DEVELOPMENT.md](DEVELOPMENT.md)，更新内容见 [CHANGELOG.md](CHANGELOG.md)。项目采用 [MIT 许可证](LICENSE)。
