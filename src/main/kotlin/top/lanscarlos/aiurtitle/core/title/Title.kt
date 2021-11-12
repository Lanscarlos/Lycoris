package top.lanscarlos.aiurtitle.core.title

import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.info
import taboolib.common.util.VariableReader
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.util.getStringColored
import taboolib.module.configuration.util.getStringListColored
import taboolib.platform.util.buildItem
import top.lanscarlos.aiurtitle.api.AiurTitleAPI
import top.lanscarlos.aiurtitle.module.ui.Template

class Title(private val id: String, config: ConfigurationSection) {

    private val prefix: String // 后缀
    private val suffix: String // 前缀
    private val display: String // 显示内容，不包含前后缀
    private val description: List<String> // 对该称号的描述
    private val template: String // 指定的模板id，用于在GUI中显示
    private val customTemplate: Template?
    private val properties: MutableMap<String, Any> = mutableMapOf() // 自定义相关变量

    init {

        prefix = (config.getStringColored("prefix") ?:
            if (id == "default") "§f[" else AiurTitleAPI.getDefaultTitle().prefix).also {
            properties["prefix"] = it
        }

        suffix = (config.getStringColored("suffix") ?:
            if (id == "default") "§f]" else AiurTitleAPI.getDefaultTitle().suffix).also {
            properties["suffix"] = it
        }

        display = (config.getStringColored("display") ?:
            if (id == "default") "§7平民" else AiurTitleAPI.getDefaultTitle().display).also {
            properties["display"] = it
        }

        description = config.getStringListColored("description").also {
            properties["description"] = it
        }

        template = (config.getString("icon.template") ?:
            if (id == "default") "default" else AiurTitleAPI.getDefaultTitle().template).also {
            customTemplate = if (it == "custom") {
                Template("custom", config.getConfigurationSection("icon.template.custom"))
            }else {
                null
            }
            properties["template"] = it
        }

        properties["titleDisplay"] = getTitleDisplay()
        config.getConfigurationSection("properties")?.let {
            it.getKeys(true).forEach { key ->
                if (it.isConfigurationSection(key)) {
                    info("$key is section")
                    return@forEach
                }
                properties[key] = it.get(key)
            }
        }
    }

    fun getId() : String {
        return id
    }

    fun getTitleDisplay() : String {
        return prefix + display + suffix
    }

    fun getIcon(mode: String): ItemStack {
        val template: Template.TemplateSection = when(mode.lowercase()) {
            "shop" -> customTemplate?.getShopTemplate() ?: Template.getTemplate(this.template).getShopTemplate()
            else -> error("无法加载到正确的 TempleSection")
        }

        return buildItem(XMaterial.matchXMaterial(template.mat.replaceVariable()).get()) {
            name = template.name.replaceVariable()
            template.lore.forEach {

                val line = it.replaceVariable() // 替换所有非 List 的变量

                /*
                * 检测剩余 List 变量
                * */
                var maxSize = 0
                val values = mutableListOf<Pair<String, List<*>>>()
                VariableReader(line, '{', '}', 2).parts.forEach loop@{ part ->
                    if (!part.isVariable) return@loop
                    properties[part.text]?.let { value ->
                        if (value is List<*>) {
                            maxSize = if (value.size > maxSize) value.size else maxSize
                            values += Pair(part.text, value)
                        }
                    }
                }

                if (values.size <= 0) {
                    // 找不到变量相关的值
                    lore += line
                }else if(maxSize <= 0) {
                    // 变量相关的数组为空

                    var str = line
                    values.forEach { value ->
                        str = str.replace("{{${value.first}}}", "")
                    }
                    lore += line
                }else if(values.size == 1) {
                    /*
                    * 当一行内变量只有一个 List 时
                    * 直接遍历替换变量
                    * */
                    values.forEach { value ->
                        var str = line
                        value.second.forEach { v ->
                            str = str.replace("{{${value.first}}}", v.toString())
                        }
                        lore += str
                    }
                }else {
                    /*
                    * 当一行内含有两个或以上变量的值为 List 时
                    * 分别按行替换变量
                    * */

                    var index = 0
                    while (maxSize-- > 0) {
                        var str = line
                        values.forEach { v ->
                            str = if (v.second.size <= index) {
                                str.replace("{{${v.first}}}", "")
                            }else {
                                str.replace("{{${v.first}}}", v.second[index].toString())
                            }
                        }
                        lore += str
                        index += 1
                    }
                }
            }
            template.flags.forEach {
                flags += ItemFlag.valueOf(it.uppercase())
            }
            if (template.shiny.equals("true", true)) {
                shiny()
            }
        }
    }

    private fun String.replaceVariable(): String {
        var newStr = this
        VariableReader(this, '{', '}', 2).parts.forEach {
            if (!it.isVariable) return@forEach
            properties[it.text]?.let { value ->
                /*
                * 替换 properties 中已有的值
                * */
                if (value is List<*>) return@forEach
                newStr = newStr.replace("{{${it.text}}}", value.toString())
                return@forEach
            }
            newStr = newStr.replace("{{${it.text}}}", "") // 将 未找到值 的变量替换为空串
        }
        return newStr
    }

    enum class TitleState {
        Active, // 表示状态可用
        Expired // 表示已过期
    }
}