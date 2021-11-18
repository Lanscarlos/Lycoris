package top.lanscarlos.lycoris.module.database

import org.bukkit.OfflinePlayer
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.YamlConfiguration
import taboolib.module.configuration.util.getMap
import top.lanscarlos.lycoris.Lycoris
import java.io.File

object DatabaseYaml : Database() {

    private val file: File by lazy {
        val file = File(Lycoris.plugin.dataFolder, "data.yml")
        if (!file.exists()) {
            releaseResourceFile("data.yml", true)
        }
        file
    }

    private val data : YamlConfiguration by lazy {
        YamlConfiguration.loadConfiguration(file)
    }

    override fun getUniqueIdList(): List<String> {
        return data.getKeys(false).map { it }
    }

    override fun getPlayerTitleUse(player: OfflinePlayer): String {
        return data.getString("${player.uniqueId}.use")
    }

    override fun updatePlayerTitleUse(player: OfflinePlayer, id: String) {
        data.set("${player.uniqueId}.use", id)
        saveData()
    }

    override fun getPlayerTitlePiece(player: OfflinePlayer): Int {
        return data.getInt("${player.uniqueId}.piece")
    }

    override fun updatePlayerTitlePiece(player: OfflinePlayer, amount: Int) {
        data.set("${player.uniqueId}.piece", amount)
        saveData()
    }

    override fun getPlayerRepository(player: OfflinePlayer): Map<String, Long> {
//        val repository = mutableMapOf<String, Long>()
//        data.getConfigurationSection("${player.uniqueId}.repository").let { section ->
//            section.getKeys(false).forEach {
//                repository[it] = section.getLong(it, -1L)
//            }
//        }
//        return repository
        val map = data.getMap<String, Long>("${player.uniqueId}.repository")
        return mutableMapOf<String, Long>().apply {
            map.forEach { (k, v) ->
                if (v < 0) {
                    put(k, -1L)
                }else {
                    put(k, v)
                }
            }
        }
    }

    override fun updatePlayerRepository(player: OfflinePlayer, repository: Map<String, Long>) {
        info("正在保存仓库...")
        data.set("${player.uniqueId}.repository", repository)
        saveData()
    }


    override fun insertPlayerData(player: OfflinePlayer, use: String, repository: Map<String, Long>) {
        data.set("${player.uniqueId}.name", player.name)
        data.set("${player.uniqueId}.use", use)
        data.set("${player.uniqueId}.piece", 0)
        data.set("${player.uniqueId}.repository", repository)
        saveData()
    }

    private fun saveData() {
        info("检测到保存文件...")
        data.save(file)
    }

}