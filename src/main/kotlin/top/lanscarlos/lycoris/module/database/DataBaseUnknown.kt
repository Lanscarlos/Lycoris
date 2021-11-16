package top.lanscarlos.lycoris.module.database

import org.bukkit.OfflinePlayer

class DataBaseUnknown : Database() {
    override fun getUniqueIdList(): List<String> {
        TODO("Not yet implemented")
    }

    override fun getPlayerTitleUse(player: OfflinePlayer): String {
        TODO("Not yet implemented")
    }

    override fun updatePlayerTitleUse(player: OfflinePlayer, id: String) {
        TODO("Not yet implemented")
    }

    override fun getPlayerTitlePiece(player: OfflinePlayer): Int {
        TODO("Not yet implemented")
    }

    override fun updatePlayerTitlePiece(player: OfflinePlayer, amount: Int) {
        TODO("Not yet implemented")
    }

    override fun getPlayerRepository(player: OfflinePlayer): Map<String, Long> {
        TODO("Not yet implemented")
    }

    override fun updatePlayerRepository(player: OfflinePlayer, repository: Map<String, Long>) {
        TODO("Not yet implemented")
    }

    override fun insertPlayerData(player: OfflinePlayer, use: String, repository: Map<String, Long>) {
        TODO("Not yet implemented")
    }
}