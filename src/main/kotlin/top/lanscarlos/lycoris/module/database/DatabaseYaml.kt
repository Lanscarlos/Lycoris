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
        return data.getString("${player.uniqueId.toString()}.use")
    }

    override fun updatePlayerTitleUse(player: OfflinePlayer, id: String) {
        data.set("${player.uniqueId.toString()}.use", id)
        saveData()
    }

    override fun getPlayerTitlePiece(player: OfflinePlayer): Int {
        return data.getInt("${player.uniqueId.toString()}.piece")
    }

    override fun updatePlayerTitlePiece(player: OfflinePlayer, amount: Int) {
        data.set("${player.uniqueId.toString()}.piece", amount)
        saveData()
    }

    override fun getPlayerRepository(player: OfflinePlayer): Map<String, Long> {
        return data.getMap<String, Long>("${player.uniqueId.toString()}.repository")
    }

    override fun updatePlayerRepository(player: OfflinePlayer, repository: Map<String, Long>) {
        info("正在保存仓库...")
        data.set("${player.uniqueId.toString()}.repository", repository)
        saveData()
    }


    override fun insertPlayerData(player: OfflinePlayer, use: String, repository: Map<String, Long>) {
        data.set("${player.uniqueId.toString()}.use", use)
        data.set("${player.uniqueId.toString()}.piece", 0)
        data.set("${player.uniqueId.toString()}.repository", repository)
        saveData()
    }

    private fun saveData() {
        info("检测到保存文件...")
        data.save(file)
    }

}