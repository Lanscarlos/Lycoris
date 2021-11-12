package top.lanscarlos.aiurtitle.module.ui

import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.util.getStringColored
import taboolib.module.configuration.util.getStringListColored
import top.lanscarlos.aiurtitle.AiurTitle

class Template(private val id: String, config: ConfigurationSection) {

    private val shopTemplate: TemplateSection

    init {
        shopTemplate = TemplateSection(config.getConfigurationSection("shop"))
    }

    fun getShopTemplate(): TemplateSection {
        return shopTemplate
    }

    companion object {

        private val defaultUse: String by lazy {
            AiurTitle.config.getString("template-setting.default-use")
        }
        private val defaultTemplate: Template by lazy {
            Template("default", AiurTitle.config.getConfigurationSection("template-setting.default-template"))
        }
        private val templates = mutableMapOf<String, Template>()

        fun loadTemplates() {
            AiurTitle.templateConfig.reload()
            templates.clear()
            AiurTitle.templateConfig.let {
                it.getKeys(false).forEach { key ->
                    templates[key] = Template(key, it.getConfigurationSection(key))
                }
            }
        }

        fun getTemplate(id: String): Template {
            return templates[id] ?: templates[defaultUse] ?: defaultTemplate
        }
    }

    class TemplateSection(config: ConfigurationSection) {

        val mat: String // 材质
        val name: String // 名字
        val lore: List<String> // 介绍
        val shiny: String // 是否发光，为了兼容自定义变量，设置为String类型
        val flags: List<String> // ItemFlag

        init {
            mat = config.getString("display.mat") ?: "NAME_TAG"
            name = config.getStringColored("display.name") ?: "{{titleDisplay}}"
            lore = config.getStringListColored("display.lore")
            shiny = config.getString("display.shiny") ?: "false"
            flags = config.getStringList("display.flags") ?: listOf()
        }
    }

    enum class TemplateSectionType {
        OnShopActive, // 在商城中可用
        OnShopExpired, // 在商城中已过期，一般指全局期限
        OnRepositoryActive, // 在仓库中可用
        OnRepositoryExpired // 在仓库中已过期，指玩家的期限
    }

}