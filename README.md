# Evernight Addon Template

[![Build](https://github.com/Evernight-Client/evernight-addon-template/actions/workflows/build.yml/badge.svg)](https://github.com/Evernight-Client/evernight-addon-template/actions/workflows/build.yml)

Evernight 插件（Addon）模板：一个可以直接构建、加载的 Fabric 客户端模组，演示如何通过 `evernight-addon` 入口注册模块、指令、配置与语言表。

- 目标版本：Evernight 1.0.3 · Minecraft 1.21.11 · Fabric Loader 0.20.1 · Java 21
- 构建工具：Gradle Wrapper 9.8.0 + Loom 1.18.4，运行 Gradle 需要 JDK 25
- 模板自带：示例模块 + 示例指令 + 示例配置 + 中英语言表 + 语言表校验脚本 + GitHub Actions
- 自带 agent skill（`.github/skills/evernight-addon-dev/`）与 `AGENTS.md`，让编码代理按插件约定工作
- 示例演示注册**插件自定义分类**（`context.registerCategory("Example")`），模块落在 ClickGUI 的 Example（示例）分类

## 快速开始

1. 点右上角 **Use this template → Create a new repository**，建一个自己的仓库（直接 fork 也行，但改起来不如新建干净）。
2. 克隆到本地，用 JDK 25 执行：

```bash
# Windows
gradlew.bat build

# Linux / macOS
./gradlew build
```

3. 首次构建会把 Evernight 发行 jar 下载到 `libs/`（见[关于 Evernight 依赖](#关于-evernight-依赖)），产物是 `build/libs/example-addon-<version>.jar`。

## 环境要求

| 组件 | 要求 |
| --- | --- |
| Minecraft | 1.21.11 |
| Fabric Loader | 0.20.1（`fabric.mod.json` 声明 `>=0.20.1`） |
| Fabric API | 0.141.6+1.21.11 |
| Evernight | 1.0.3 或更高 |
| 运行 Java | 21+ |
| 构建 JDK | 25（Loom 1.18.4 的 Gradle JVM 要求） |

## 改造成自己的插件

模板使用 `exampleaddon` / `com.example.exampleaddon` 这套占位标识。需要同步替换的地方：

| 位置 | 改什么 |
| --- | --- |
| `gradle.properties` | `mod_version`、`maven_group`、`archives_base_name` |
| `settings.gradle` | `rootProject.name` |
| `src/main/resources/fabric.mod.json` | `id`、`name`、`description`、`authors`、`contact`、入口类全名、`depends.evernight` |
| `src/main/java/com/example/exampleaddon/` | 包名与类名（用 IDE 的 Rename / Move） |
| `src/main/resources/assets/exampleaddon/` | 目录名必须等于 `fabric.mod.json` 的 `id` |
| `assets/<id>/lang/*.json` | 所有键改成 `addons.<id>.*` |
| 模块构造器 | 模块名全局唯一，发布后不改 |

约定（摘自 Evernight 的 `docs/addons.md`）：

- `environment` 必须是 `client`，入口键必须是 `evernight-addon`，入口类继承 `evernight.addon.EvernightAddon`。
- 模块名是全局键：`modules.json` 的配置键、预设键、`;<模块名>` 指令都由它生成，**发布后不要改名**，也不能与其他插件重名。
- 玩家可见文本都放进 `assets/<id>/lang/{en_us,zh_cn}.json`，键用 `addons.<id>.*`；**不要用 `evernight.` 前缀**，那是本体保留命名空间。查找链是「当前语言 → 英文 → 完整键」：中文缺键回退英文，两份都缺才显示完整键，Java 里不写死显示文本。
- 所有注册都在 `onInitialize` 里完成，运行期不支持动态增删模块/指令。
- 插件失败不会拖垮本体：`onInitialize` 抛异常时该插件被标记为 `FAILED`，已成功的注册保留。

## 目录结构

```
.
├── build.gradle                构建脚本（含 Evernight 依赖下载）
├── gradle.properties           构建版本与模组元数据
├── settings.gradle             项目名与 Loom 插件解析
├── libs/                       Evernight 发行 jar 的存放位置（jar 不入库）
├── scripts/check-lang.py       语言表校验
├── .github/workflows/build.yml CI：构建 + 校验 + 上传 jar
├── .github/skills/evernight-addon-dev/  agent skill（插件开发约定）
├── AGENTS.md                   仓库约定：先读 skill，再动代码
└── src/main/
    ├── java/com/example/exampleaddon/
    │   ├── ExampleAddon.java   入口：注册模块/配置/指令
    │   ├── ExampleModule.java  示例模块：开关 + 设置 + tick 事件
    │   ├── ExampleCommand.java 示例指令：;exampleaddon / ;exaddon
    │   └── ExampleConfig.java  示例配置：evernight/exampleaddon.json
    └── resources/
        ├── fabric.mod.json
        └── assets/exampleaddon/lang/{en_us,zh_cn}.json
```

## 关于 Evernight 依赖

插件编译需要 Evernight 的类。模板使用 GitHub Release 里的发行 jar（`evernight-<version>.jar`，intermediary 命名空间），并用 `modImplementation files(...)` 声明：Loom 会在构建时把它重映射成开发用的 named 命名空间，所以插件代码可以直接继承 `EvernightAddon`、使用 `Module` / `Setting` 等本体 API。

模板的处理方式：

1. `libs/evernight-<evernight_version>.jar` 存在时直接使用。
2. 不存在时在配置阶段从 Evernight 对应版本的 GitHub Release 下载（URL 由 `gradle.properties` 的 `evernight_version` 决定）。
3. 也可以自行构建本体：在 Evernight 仓库执行 `gradlew.bat build`，把 `build/libs/evernight-<version>.jar` 复制到 `libs/`。
4. 离线或镜像环境：`gradlew build -Pevernight_jar_url=<可访问的 URL>`，或直接把 jar 放进 `libs/`。

> 不要改用 `build/devlibs/evernight-<version>-dev.jar`。它的 access widener 头是 `named`，而 Loom 会把 `mod*` 配置里的依赖按 intermediary 处理，构建会报 `Namespace mismatch, expected intermediary got named`。

升级 Evernight 时同时修改 `gradle.properties` 的 `evernight_version` 和 `fabric.mod.json` 的 `depends.evernight`。

## 构建与检查

```bash
gradlew.bat build                # 编译 + 打 jar
python scripts/check-lang.py     # 语言表键集/前缀校验（CI 也会执行）
```

## 游戏内联调

1. 把 `build/libs/example-addon-<version>.jar` 放进 Evernight 开发目录的 `run/mods/`（或正式游戏的 `.minecraft/mods/`）。
2. 启动客户端，日志应出现 `Loaded addon Example Addon <version> (exampleaddon)`。
3. 游戏内执行 `;addons` 查看已加载插件，`;exampleaddon` 执行示例指令；ClickGUI 的 Example（示例）分类里能看到 **Example Ticker**。
4. 模块状态写入 `evernight/modules.json`，示例配置写入 `evernight/exampleaddon.json`。

## 发布

- 修改 `mod_version` 后构建，发布 `build/libs/<archives_base_name>-<version>.jar` 即可。
- 在 `depends.evernight` 里声明实际测试过的版本范围，避免 API 不兼容。

## 下一步

- 加自定义分类：`context.registerCategory("MyCategory")`，把返回的 `ModuleCategory` 传给模块构造器，并在两份语言表里写 `addons.<id>.names.categories.MyCategory`。
- 加 HUD 组件：模块改为继承 `HudWidgetModule`，并在 `onInitialize` 里用 `registerHudModule(...)` 注册。
- 加独立状态文件：实现 `JsonConfigurable`（参考 `ExampleConfig`）并调用 `context.registerConfig(...)`。
- 加 Mixin：在插件的 `fabric.mod.json` 里声明自己的 mixin 配置，本体的清单不会代管插件。

## 参考

- [Evernight 插件开发指南](https://github.com/Evernight-Client/Evernight/blob/main/docs/addons.md)
- [插件 API 源码](https://github.com/Evernight-Client/Evernight/tree/main/src/main/java/evernight/addon)
- [本仓库的插件开发 skill](.github/skills/evernight-addon-dev/SKILL.md)
- [Evernight 本体仓库](https://github.com/Evernight-Client/Evernight)

## 许可

Evernight 以 `GPL-3.0-only` 发布，链接本体公开 API 的插件按 GPL-3.0 处理（见开发指南「版本与兼容」）。本模板同样以 [GPL-3.0-only](LICENSE) 发布。
