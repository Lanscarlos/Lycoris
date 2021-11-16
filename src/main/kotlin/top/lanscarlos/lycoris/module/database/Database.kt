package top.lanscarlos.lycoris.module.database

import org.bukkit.OfflinePlayer
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent
import top.lanscarlos.lycoris.Lycoris

abstract class Database {

    /**
     * 获取在数据库中已存储的玩家uuid
     * */
    abstract fun getUniqueIdList() : List<String>

    abstract fun getPlayerTitleUse(player: OfflinePlayer) : String

    abstract fun updatePlayerTitleUse(player: OfflinePlayer, id: String = "default")

    abstract fun getPlayerTitlePiece(player: OfflinePlayer) : Int

    abstract fun updatePlayerTitlePiece(player: OfflinePlayer, amount: Int = 0)

    abstract fun getPlayerRepository(player: OfflinePlayer): Map<String, Long>

    abstract fun updatePlayerRepository(player: OfflinePlayer, repository: Map<String, Long>)

    abstract  fun insertPlayerData(player: OfflinePlayer, use: String, repository: Map<String, Long>)

    companion object {
        val instance : Database by lazy {
            when(Lycoris.config.getString("database.storage", "unknown").lowercase()) {

                "mysql" -> DatabaseMySQL()

                "yml", "yaml" -> DatabaseYaml

                else -> DataBaseUnknown()
            }
        }

        @SubscribeEvent
        fun onPlayerJoin(e: PlayerJoinEvent) {
            if (!instance.getUniqueIdList().contains(e.player.uniqueId.toString())) {
                instance.insertPlayerData(e.player, "default", mapOf())
            }
        }
    }
}