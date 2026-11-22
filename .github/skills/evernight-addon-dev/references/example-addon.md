# 模板里的示例插件

本仓库本身就是一份可编译的示例：`src/main/java/com/example/exampleaddon/` 下的四个类各负责一件事。改插件时先读它们，再按需删改。

## 文件职责

| 文件 | 职责 | 通常要改的地方 |
| --- | --- | --- |
| `ExampleAddon.java` | `evernight-addon` 入口，`onInitialize` 里一次性注册模块/配置/指令 | 类名、注册列表 |
| `ExampleModule.java` | 继承 `AddonModule`：布尔 + 数值设置、`@EventHandler` tick 事件、本地化聊天消息 | 模块名、分类、设置 |
| `ExampleCommand.java` | 继承 `Command`：`;exampleaddon` / `;exaddon` | 指令名、别名、输出 |
| `ExampleConfig.java` | `JsonConfigurable`：`evernight/exampleaddon.json` | 文件名、字段 |

## 入口

```java
public final class ExampleAddon extends EvernightAddon {
    private ExampleConfig config;

    @Override
    public void onInitialize(AddonContext context) {
        this.config = new ExampleConfig();
        ExampleModule module = context.registerModule(new ExampleModule());
        context.registerConfig(this.config);
        context.registerCommand(new ExampleCommand(module, this.config));
    }

    @Override
    public void onShutdown() { }
}
```

## fabric.mod.json 的关键字段

```json
{
  "id": "exampleaddon",
  "version": "${version}",
  "environment": "client",
  "entrypoints": { "evernight-addon": ["com.example.exampleaddon.ExampleAddon"] },
  "depends": {
    "fabricloader": ">=0.20.1",
    "minecraft": "1.21.11",
    "java": ">=21",
    "fabric-api": "*",
    "evernight": ">=1.0.1"
  }
}
```

`version` 由 `processResources` 用 `gradle.properties` 的 `mod_version` 展开。

## 语言表

`assets/exampleaddon/lang/en_us.json` 与 `zh_cn.json` 的键集必须一致，全部放在 `addons.exampleaddon.*`：

| 键 | 用途 |
| --- | --- |
| `addons.exampleaddon.names.modules.ExampleTicker` | 模块显示名 |
| `addons.exampleaddon.descriptions.modules.ExampleTicker` | 模块描述 |
| `addons.exampleaddon.names.settings.Announce` / `...Interval` | 设置标签 |
| `addons.exampleaddon.announce` | 私有文案（`Translations.get`） |

`AddonModule` 按约定自动读取前四类键；缺键时回退英文标识，不会把完整键显示到界面。

## 依赖

`build.gradle` 通过 `modImplementation files("libs/evernight-<evernight_version>.jar")` 链接 Evernight 的发行 jar，首次构建从 GitHub Release 下载；Loom 负责 intermediary → named 重映射。细节与 `-dev.jar` 的坑见 `SKILL.md` §3。

## 验收

```bash
gradlew.bat build
python scripts/check-lang.py
```

产物 `build/libs/example-addon-<version>.jar`；放进 Evernight 的 `mods/` 后，日志出现 `Loaded addon Example Addon <version> (exampleaddon)`，游戏内 `;addons`、`;exampleaddon` 可查。
