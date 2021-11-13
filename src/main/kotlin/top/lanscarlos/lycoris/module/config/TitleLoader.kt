package top.lanscarlos.lycoris.module.config

import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.warning
import taboolib.library.configuration.YamlConfiguration
import top.lanscarlos.lycoris.Lycoris
import top.lanscarlos.lycoris.module.data.TitleData
import java.io.File

object TitleLoader {

    private val folder by lazy {
        val folder = File(Lycoris.plugin.dataFolder, "Titles")

        // 当 Titles 文件夹不存在则创建默认文件
        if (!folder.exists()) {
            listOf(
                "example.yml"
            ).forEach { releaseResourceFile("Titles/$it", true) }
        }

        folder
    }

    fun loadTitles() {

        TitleData.loadDefault()

        var count = 0
        TitleData.clearData()
        mutableListOf<YamlConfiguration>().apply {
            getFiles(folder).forEach {
                val config = YamlConfiguration.loadConfiguration(it)
                this.add(config)
            }
        }.forEach {
            it.getKeys(false).forEach { key ->
                if (!it.isConfigurationSection(key)) {
                    warning("称号 [ $key ] 结构解析失败！请认真检查配置文件是否正确！")
                    // 跳过本次循环
                    return@forEach
                }
                Lycoris.debug(2, "正在载入称号 $key ...")
                TitleData.loadTitle(key, it.getConfigurationSection(key))?.let { title ->
                    count += 1
                    Lycoris.debug(1, "载入称号成功 $key -> ${title.getDisplay()}")
                    return@forEach
                }
                Lycoris.debug(2, "载入称号 $key 失败")
            }
        }
        info("载入称号完毕...共载入 $count 个称号")
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