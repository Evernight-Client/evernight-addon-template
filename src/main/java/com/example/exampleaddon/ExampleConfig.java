package com.example.exampleaddon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import evernight.client.settings.core.JsonConfigurable;

/**
 * 示例配置：通过 {@code context.registerConfig(...)} 注册后保存到
 * {@code evernight/exampleaddon.json}，在 {@code onInitialize} 里注册时启动阶段读盘。
 *
 * <p>模块开关与设置已经由 evernight/modules.json 自动持久化，这里只放插件自己的状态。
 * 文件名必须唯一，建议用 {@code <插件 id>.json}。{@code ;reload} 不会重读本文件。
 */
public final class ExampleConfig implements JsonConfigurable {
    private String greeting = "Hello from ExampleAddon.";

    public String greeting() {
        return this.greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting == null ? "" : greeting;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("greeting", this.greeting);
        return json;
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json == null || !json.isJsonObject()) {
            return;
        }
        JsonObject object = json.getAsJsonObject();
        if (object.has("greeting")) {
            this.setGreeting(object.get("greeting").getAsString());
        }
    }

    @Override
    public String getConfigName() {
        return "exampleaddon.json";
    }
}
