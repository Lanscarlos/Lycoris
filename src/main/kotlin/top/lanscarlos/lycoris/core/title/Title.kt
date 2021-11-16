package top.lanscarlos.lycoris.core.title

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.function.info
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.util.getStringColored
import taboolib.module.configuration.util.getStringListColored
import top.lanscarlos.lycoris.Lycoris
import top.lanscarlos.lycoris.api.LycorisAPI
import top.lanscarlos.lycoris.module.ui.Template
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Title(private val id: String, config: ConfigurationSection) {

    private val prefix: String // 后缀
    private val suffix: String // 前缀
    private val display: String // 显示内容，不包含前后缀
    private val description: List<String> // 对该称号的描述
    private val hidden: Boolean // 是否在商城隐藏图标
    private val template: String // 指定的模板id，用于在GUI中显示
    private val customTemplate: Template?
    private val buff: MutableSet<PotionEffect> // 药水效果集合
//    private val buff: MutableSet<PotionEffect> // 粒子效果集合
    private val duration: Long // 称号默认持续时间
    private val deadline: Long // 称号截止时间

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

        hidden = config.getBoolean("icon.hidden", false).also {
            properties["hidden"] = it
        }

        template = (config.getString("icon.template") ?:
            if (id == "default") "default" else LycorisAPI.getDefaultTitle().template).also {
            customTemplate = if (it == "custom") {
                Template(config.getConfigurationSection("icon.custom"))
            }else {
                null
            }
            properties["template"] = it
        }

        buff = mutableSetOf<PotionEffect>().also { set ->
            config.getMapList("buff").forEach { map ->
                val type = map["type"]?.let { PotionEffectType.getByName(it.toString().uppercase()) } ?: return@forEach
                val amplifier = (map["level"]?.toString()?.toInt() ?: map["amplifier"]?.toString()?.toInt() ?: 0).let { if (it < 0) 0 else it }
//                val duration = (map["duration"]?.toString()?.toInt() ?: -1).let { if (it <= 0) 9999 else it }
                val duration = Lycoris.period + 600
                val ambient = map["ambient"]?.toString()?.toBoolean() ?: false
                val particles = map["particles"]?.toString()?.toBoolean() ?: true
                val icon = map["icon"]?.toString()?.toBoolean() ?: true
                set += PotionEffect(type, duration.toInt(), amplifier, ambient, particles, icon)
            }
        }

        duration = config.getString("duration")?.let {
            var time: Long = 0
            val list = mutableListOf(*(it.split("d", "h", "m").filter { s -> s.isNotEmpty() }).toTypedArray())
            if (it.contains("d")) time += list.removeAt(0).toInt() * 24 * 60 * 60 * 1000
            if (it.contains("h")) time = (time + list.removeAt(0).toInt()) * 60 * 60 * 1000
            if (it.contains("m")) time = (time + list.removeAt(0).toInt()) * 60 * 1000
            time
        } ?: -1L

        deadline = config.getString("deadline")?.let {
            SimpleDateFormat("yyyy-MM-dd HH:mm").parse(it, ParsePosition(0)).time
        } ?: -1L

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

    /**
     * 称号是否有效， 是否已过期
     * */
    fun isAvailable(): Boolean {
        return getId() == "default" || getDeadline() < 0 || System.currentTimeMillis() < getDeadline()
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

    fun getId() : String {
        return id
    }

    fun getDisplay() : String {
        return display
    }

    fun getTitleDisplay() : String {
        return prefix + display + suffix
    }

    fun getDuration(): Long {
        return duration
    }

    fun getDeadline(): Long {
        return deadline
    }

    fun getBuff(): Set<PotionEffect> {
        return buff
    }

    fun getProperties(): Map<String, Any> {
        return properties
    }

}