package com.example.exampleaddon;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import evernight.client.commands.Command;
import evernight.client.utils.chat.ChatUtils;
import net.minecraft.network.chat.Component;

/**
 * 示例指令：{@code ;exampleaddon}，别名 {@code ;exaddon}。
 *
 * <p>指令名是游戏内输入在客户端前缀（默认 {@code ;}）后面的字面量，保持小写英文。
 * 不要与模块名或其别名重复：模块会自动注册同名指令。
 */
public final class ExampleCommand extends Command {
    private final ExampleModule module;
    private final ExampleConfig config;

    public ExampleCommand(ExampleModule module, ExampleConfig config) {
        super("exampleaddon");
        this.module = module;
        this.config = config;
        this.setAliases("exaddon");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void exec(LiteralArgumentBuilder root) {
        com.mojang.brigadier.Command action = context -> {
            ChatUtils.printChat(Component.literal(this.config.greeting()), ChatUtils.DEFAULT_SIGNATURE);
            ChatUtils.printChat(Component.literal("ExampleTicker is "
                            + (this.module.isToggled() ? "on" : "off")
                            + ", interval " + this.module.intervalTicks() + " ticks."),
                    ChatUtils.DEFAULT_SIGNATURE);
            return 1;
        };
        root.executes(action);
    }
}
