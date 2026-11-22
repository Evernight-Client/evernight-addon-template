package com.example.exampleaddon;

import evernight.addon.AddonModule;
import evernight.client.events.core.EventHandler;
import evernight.client.events.ui.MinecraftTickEvent;
import evernight.client.modules.ModuleCategory;
import evernight.client.render.text.Translations;
import evernight.client.settings.core.Setting;
import evernight.client.settings.types.BooleanSetting;
import evernight.client.settings.types.NumberSetting;
import evernight.client.utils.chat.ChatUtils;
import net.minecraft.network.chat.Component;

/**
 * 示例模块：开关打开后，每隔 Interval 个 tick 在聊天栏输出一条本地化消息。
 *
 * <p>模块名（{@code ExampleTicker}）是全局键：modules.json 的配置键、预设键和
 * {@code ;exampleticker} 指令都由它生成，发布后不要改名，也不能与其他插件重名。
 * 模块名、描述与设置标签分别读取 {@code addons.exampleaddon.names.modules.*}、
 * {@code addons.exampleaddon.descriptions.modules.*} 与 {@code addons.exampleaddon.names.settings.*}。
 */
public final class ExampleModule extends AddonModule {
    private final Setting<Boolean> announceSetting;
    private final Setting<Integer> intervalSetting;
    private int ticks;

    public ExampleModule() {
        super("exampleaddon", "ExampleTicker", "Prints a chat message on an interval.",
                ModuleCategory.MISC);
        this.announceSetting = this.add(new BooleanSetting("Announce", true));
        this.intervalSetting = this.add(
                new NumberSetting<Integer>("Interval", 100, 20, 1200).dependsOn(this.announceSetting));
    }

    public int intervalTicks() {
        return this.intervalSetting.getValue();
    }

    @EventHandler
    private void onTick(MinecraftTickEvent event) {
        if (!this.announceSetting.getValue()) {
            return;
        }
        if (++this.ticks < this.intervalSetting.getValue()) {
            return;
        }
        this.ticks = 0;
        ChatUtils.printChat(
                Component.literal(Translations.get("addons.exampleaddon.announce")),
                ChatUtils.moduleSignature(this));
    }
}
