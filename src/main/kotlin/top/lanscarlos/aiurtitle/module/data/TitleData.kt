package top.lanscarlos.aiurtitle.module.data

import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.util.getStringColored
import top.lanscarlos.aiurtitle.AiurTitle
import top.lanscarlos.aiurtitle.core.title.Title

object TitleData {

    private val ids = mutableListOf<String>()

    /**
     * id -> Title
     * 所有称号集合
     * */
    private val titles = mutableMapOf<String, Title>()

    private var defaultTitle: Title = Title("default", AiurTitle.config.getConfigurationSection("title-setting.default"))

    fun isTitle(id: String) : Boolean {
        return if(id == "default") true else ids.contains(id)
    }

    fun getDefaultTitle() : Title {
        return defaultTitle
    }

    fun getTitle(id: String) : Title {
        return titles[id] ?: defaultTitle
    }

    fun getTitleIds() : List<String> {
        return ids.toList()
    }

    fun getTitles() : Map<String, Title> {
        return titles.toMap()
    }

    fun clearData() {
        ids.clear()
        titles.clear()
    }

    /**
     * 加载默认数据
     * 前缀、后缀、默认称号
     * */
    fun loadDefault() {
        defaultTitle = Title(defaultTitle.getId(), AiurTitle.config.getConfigurationSection("title-setting.default"))
    }

    fun loadTitle(id: String, config: ConfigurationSection, replace: Boolean = false) : Title? {
        if (id == "default") {
            return null
        }
        if(ids.contains(id) && !replace) {
            return null
        }
        val title = Title(id, config)
        ids.add(id)
        titles[id] = title
        return title
    }
}
