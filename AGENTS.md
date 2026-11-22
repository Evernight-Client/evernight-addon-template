# 插件模板仓库约定

1. 开发前先读 `.github/skills/evernight-addon-dev/SKILL.md`，按里面的注册约定与检查清单改代码。
2. 玩家可见文本走语言表：`assets/<id>/lang/{en_us,zh_cn}.json` 键集一致，键用 `addons.<id>.*`，提交前 `python scripts/check-lang.py` 必须 0 error。
3. 模块名、指令名、配置文件是稳定标识，发布后不要改名。
4. 改完先 `gradlew.bat build`；`libs/*.jar` 与 `build/` 不入库。
5. 功能完成后按功能拆分提交，不要一个大提交。
6. 不在本环境启动游戏时，不要声称验证过游戏内行为。
