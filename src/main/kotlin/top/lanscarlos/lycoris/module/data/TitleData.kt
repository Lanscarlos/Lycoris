package top.lanscarlos.lycoris.module.data

import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.warning
import taboolib.library.configuration.YamlConfiguration
import top.lanscarlos.lycoris.Lycoris
import top.lanscarlos.lycoris.core.title.Title
import java.io.File

object TitleData {

    private val ids = mutableListOf<String>()
    private val titles = mutableMapOf<String, Title>() // 所有称号集合 id -> Title

    private val folder by lazy {
        File(Lycoris.plugin.dataFolder, "Titles")
    }

    private lateinit var defaultTitle: Title

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

    fun loadTitles() {
        UserData.clearPlayerBuff()

        ids.clear()
        titles.clear()
        defaultTitle = Title("default", Lycoris.config.getConfigurationSection("title-setting.default"))

        if (!folder.exists()) {
            listOf(
                "example.yml"
            ).forEach { releaseResourceFile("Titles/$it", true) }
        }

        var count = 0
        mutableListOf<YamlConfiguration>().apply {
            getFiles(folder).forEach {
                val config = YamlConfiguration.loadConfiguration(it)
                this.add(config)
            }
        }.forEach loop@{
            it.getKeys(false).forEach { key ->
                if (key == "default") return@forEach
                if (!it.isConfigurationSection(key)) {
                    warning("称号 [ $key ] 结构解析失败！请认真检查配置文件是否正确！")
                    // 跳过本次循环
                    return@forEach
                }
                Lycoris.debug(2, "正在载入称号 $key ...")
                ids += key
                titles[key] = Title(key, it.getConfigurationSection(key))
                count += 1
            }
        }
        info("载入称号完毕...共载入 $count 个称号")

        UserData.updatePlayerBuff()
    }

    /**
     * 过滤有效称号文件
     * */
    private fun getFiles(file : File, filter : String = "#") : List<File> {
        return mutableListOf<File>().apply {
            if(file.isDirectory) {
                file.listFiles()?.forEach {
                    addAll(getFiles(it))
                }
            } else if (!file.name.startsWith(filter)) {
                add(file)
            }
        }
    }
}
