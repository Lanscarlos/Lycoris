package top.lanscarlos.lycoris.module.ui

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.util.VariableReader
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.util.getStringColored
import taboolib.module.configuration.util.getStringListColored
import taboolib.module.kether.Kether
import taboolib.module.kether.KetherShell
import taboolib.platform.util.buildItem
import top.lanscarlos.lycoris.Lycoris
import top.lanscarlos.lycoris.core.title.Title

class Template(config: ConfigurationSection) {

    /*
    * display
    * */
    private val mat: String
    private val name: String
    private val lore: List<String>
    private val flags: List<ItemFlag>
    private val shiny: String

    private val scripts: Map<String, String>

    private val properties: Map<String, Any>

    init {

        mat = config.getString("display.mat", "Name_Tag")

        name = config.getStringColored("display.name") ?: "{{displayTitle}}"

        lore = config.getStringListColored("display.lore")

        flags = mutableListOf<ItemFlag>().apply {
            config.getStringList("display.flags").forEach {
                add(ItemFlag.valueOf(it.uppercase()))
            }
        }

        shiny = config.getString("display.shiny") ?: "false"


        scripts = config.getConfigurationSection("script")?.let { section ->
            mutableMapOf<String, String>().apply {
                section.getKeys(false).forEach {
                    if (section.isString(it)) this[it.lowercase()] = section.getString(it)
                }
            }
        } ?: mapOf()

        properties = config.getConfigurationSection("properties")?.let { section ->
            mutableMapOf<String, Any>().apply {
                section.getKeys(true).forEach {
                    if (section.isConfigurationSection(it)) return@forEach
                    if (section.isString(it)) {
                        this[it.lowercase()] = section.getStringColored(it) ?: "null"
                    }else if (section.isList(it)) {
                        this[it.lowercase()] = section.getStringListColored(it)
                    }else {
                        this[it.lowercase()] = section.get(it)
                    }
                }
            }
        } ?: mapOf()
    }

    fun buildIcon(sender: Player, player: OfflinePlayer, menuType: String, title: Title): ItemStack {
        val values = mutableMapOf<String, Any>().apply {
            putAll(title.getProperties())
            put("menu-type", menuType)
            put("sender", sender)
            put("player", player)
        }
        return buildItem(XMaterial.matchXMaterial(this.mat.replaceVariables(values).uppercase()).get()) {
            name = this@Template.name.replaceVariables(values)
            lore += this@Template.lore.replaceVariables(values)
            if (this@Template.shiny.replaceVariables(values).equals("true", true)) {
                shiny()
            }
            flags += this@Template.flags
        }
    }

    /**
     * @param values 传入的菜单变量值
     * @param set 已加载完毕的变量名，用来防止重复加载同一变量
     * */
    private fun String.getVariables(values: Map<String, Any>, set: Set<String> = setOf()): Map<String, Any> {
        return mutableMapOf<String, Any>().also { map ->
            VariableReader(this, '{', '}', 2).parts.forEach {
                if (!it.isVariable || set.contains(it.text) || map.contains(it.text)) return@forEach

                // 先检测Script是否有相关变量值, 将执行结果传递并替换变量
                scripts[it.text.lowercase()]?.let { script ->
                    val sender = adaptCommandSender(values["sender"] ?: Bukkit.getConsoleSender())
                    KetherShell.eval(script, sender = sender, namespace = Kether.scriptRegistry.registeredNamespace.toList(), context = {
                        values.forEach { (k, v) ->
                            set(k, v)
                        }
                        properties.forEach { (k, v) ->
                            set(k, v)
                        }
                    }).thenApply { v ->
                        map[it.text] = v ?: "null"
                    }
                    return@forEach
                }
                values[it.text.lowercase()]?.let { value ->
                    map[it.text] = value
                    return@forEach
                }
                properties[it.text.lowercase()]?.let { value ->
                    map[it.text] = value
                    return@forEach
                }
            }
        }
    }

    /**
     * 使用传入的数据集合替换字符串中的变量
     * @param values 传入的数据集合
     * @param replaceList 是否替换 List 数据类型的变量
     * */
    private fun String.replaceVariables(values: Map<String, Any>, replaceList: Boolean = true): String {
        var str = this
        this.getVariables(values = values).forEach {
            str = str.replaceVariable(it.key, it.value, replaceList)
        }
        return str
    }

    private fun String.replaceVariable(variable: String, value: Any? = "null", replaceList: Boolean): String {
        // 变量值为 List 且不替换 List 类型, 取消替换

        if (value is List<*>) {
            if (!replaceList) return this
            this.replace("{{$variable}}", value.joinToString("\n"))
        }
        return this.replace("{{$variable}}", value?.toString() ?: "")
    }

    private fun List<String>.replaceVariables(values: Map<String, Any>): List<String> {
        val list = mutableListOf<String>()
        val location = mutableListOf<MutableSet<String>>()
        val data = mutableMapOf<String, Any>()

        this.forEachIndexed { index, line ->
            location.add(index, mutableSetOf())
            data += line.getVariables(values, data.keys).onEach { location[index].add(it.key) }
        }

        this.forEachIndexed { i, string ->
            var maxSize = 0
            val valueSet = mutableSetOf<Pair<String, List<*>>>() // 所有 List 变量集合
            var line = string
            location[i].forEach { vars ->
                val value = data[vars]
                // 先将非 List 变量替换, List 变量存储起来 再一起替换
                if (value is List<*>) {
                    maxSize = if (value.size > maxSize) value.size else maxSize
                    valueSet += Pair(vars, value)
                    return@forEach
                }
                line = line.replaceVariable(vars, data[vars], false)
            }

            if (valueSet.size <= 0) {
                // 找不到变量相关的值
                list += line
            }else if (maxSize <= 0) {
                // 变量相关的 List 为空
                var str = line
                valueSet.forEach {
                    str = str.replaceVariable(it.first, "", false)
                }
                list += str
            }else {
                /*
                * 当一行内变量只有一个 List 时
                * 直接遍历替换变量
                * */
                var index = 0
                while (maxSize-- > 0) {
                    var str = line
                    valueSet.forEach {
                        str = if (it.second.size <= index) {
                            str.replaceVariable(it.first, "", false)
                        }else {
                            str.replaceVariable(it.first, it.second[index], false)
                        }
                    }
                    list += str
                    index += 1
                }
            }
        }

        // 分割 \n 换行符
        return mutableListOf<String>().also {
            list.forEach { line ->
                if (line.contains("\\n")) {
                    line.split("\\n").forEach { part ->
                        it += part
                    }
                }else {
                    it += line
                }
            }
        }
    }

    companion object {

        private lateinit var defaultTemplate: Template

        private val templates = mutableMapOf<String, Template>()

        fun loadTemplates() {
            defaultTemplate = Template(Lycoris.config.getConfigurationSection("template-setting.default"))
            Lycoris.templateConfig.getKeys(false).forEach {
                templates[it] = Template(Lycoris.templateConfig.getConfigurationSection(it))
            }
        }

        fun getTemplate(id: String): Template {
            return templates[id] ?: defaultTemplate
        }

    }

}