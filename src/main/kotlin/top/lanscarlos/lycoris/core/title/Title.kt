package top.lanscarlos.lycoris.core.title

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.info
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.util.getStringColored
import taboolib.module.configuration.util.getStringListColored
import top.lanscarlos.lycoris.api.LycorisAPI
import top.lanscarlos.lycoris.module.ui.Template

class Title(private val id: String, config: ConfigurationSection) {

    private val prefix: String // 后缀
    private val suffix: String // 前缀
    private val display: String // 显示内容，不包含前后缀
    private val description: List<String> // 对该称号的描述
    private val template: String // 指定的模板id，用于在GUI中显示
    private val customTemplate: Template?
    private val properties: MutableMap<String, Any> = mutableMapOf() // 自定义相关变量  小写变量名 -> 值

    init {

        prefix = (config.getStringColored("prefix") ?:
            if (id == "default") "§f[" else LycorisAPI.getDefaultTitle().prefix).also {
            properties["prefix"] = it
        }

        suffix = (config.getStringColored("suffix") ?:
            if (id == "default") "§f]" else LycorisAPI.getDefaultTitle().suffix).also {
            properties["suffix"] = it
        }

        display = (config.getStringColored("display") ?:
            if (id == "default") "§7平民" else LycorisAPI.getDefaultTitle().display).also {
            properties["display"] = it
        }

        description = config.getStringListColored("description").also {
            properties["description"] = it
        }

        template = (config.getString("icon.template") ?:
            if (id == "default") "default" else LycorisAPI.getDefaultTitle().template).also {
            customTemplate = if (it == "custom") {
                info("检测 - 加载custom")
                Template(config.getConfigurationSection("icon.custom"))
            }else {
                info("检测 - 未加载custom")
                null
            }
            properties["template"] = it
        }

        properties["title-display"] = getTitleDisplay()
        config.getConfigurationSection("properties")?.let { section ->
            section.getKeys(true).forEach {
                if (section.isConfigurationSection(it)) return@forEach
                if (section.isString(it)) {
                    properties[it.lowercase()] = section.getStringColored(it) ?: "null"
                }else if (section.isList(it)) {
                    properties[it.lowercase()] = section.getStringListColored(it)
                }else {
                    properties[it.lowercase()] = section.get(it)
                }
            }
        }
    }

    fun getId() : String {
        return id
    }

    fun getDisplay() : String {
        return display
    }

    fun getTitleDisplay() : String {
        return prefix + display + suffix
    }

    fun getProperties(): Map<String, Any> {
        return properties
    }

    /**
     * 获取在菜单显示的图标
     * @param sender 打开菜单的发起者
     * @param player 惨淡所属的主人，一般与称号仓库的主人对应
     * @param menuType 打开菜单的类型, 目前仅有 商城 以及 称号仓库
     * */
    fun getIcon(sender: Player, player: OfflinePlayer, menuType: String): ItemStack {
        val template: Template = customTemplate ?: Template.getTemplate(this.template)
        return template.buildIcon(sender, player, menuType, this)
    }

}