package com.example.exampleaddon;

import evernight.addon.AddonContext;
import evernight.addon.EvernightAddon;
import evernight.client.modules.ModuleCategory;

/**
 * 插件入口：{@code fabric.mod.json} 的 {@code evernight-addon} 入口键指向这里。
 *
 * <p>只在这里做一次性注册（模块、配置、指令、语言）。Evernight 在配置加载之前调用本方法，
 * 所以这里注册的设置会在启动时从 {@code evernight/modules.json} 恢复。
 *
 * <p>语言表 {@code assets/exampleaddon/lang/*.json} 由 AddonManager 在调用本方法前自动加载，
 * 不需要再调用 {@link AddonContext#loadLanguageFiles()}。
 */
public final class ExampleAddon extends EvernightAddon {
    private ExampleConfig config;

    @Override
    public void onInitialize(AddonContext context) {
        this.config = new ExampleConfig();

        // 注册插件自己的分类，再把返回的 ModuleCategory 传给模块构造器。
        // 分类显示名来自 addons.exampleaddon.names.categories.Example（en_us/zh_cn 两份）。
        ModuleCategory exampleCategory = context.registerCategory("Example");
        ExampleModule module = context.registerModule(new ExampleModule(exampleCategory));
        context.registerConfig(this.config);
        context.registerCommand(new ExampleCommand(module, this.config));

        context.logger().info("ExampleAddon {} initialized", context.metadata().version());
    }

    @Override
    public void onShutdown() {
        // 在这里停止线程、关闭连接或刷新非配置状态；模板没有需要清理的资源。
    }
}
