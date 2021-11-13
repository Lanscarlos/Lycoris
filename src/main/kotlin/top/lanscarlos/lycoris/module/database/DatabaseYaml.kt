package top.lanscarlos.lycoris.module.database

import org.bukkit.OfflinePlayer
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.YamlConfiguration
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

    override fun getPlayerTitleList(player: OfflinePlayer): MutableList<String> {
        return data.getStringList("${player.uniqueId.toString()}.list")
    }

    override fun updatePlayerTitleList(player: OfflinePlayer, list: List<String>) {
        data.set("${player.uniqueId.toString()}.list", list)
        saveData()
    }

    override fun insertPlayerData(player: OfflinePlayer, use: String, list: List<String>) {
        data.set("${player.uniqueId.toString()}.use", use)
        data.set("${player.uniqueId.toString()}.list", list)
        saveData()
    }

    fun saveData() {
        data.save(file)
    }

}