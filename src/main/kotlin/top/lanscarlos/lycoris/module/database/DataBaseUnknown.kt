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

    override fun getPlayerTitleList(player: OfflinePlayer): MutableList<String> {
        TODO("Not yet implemented")
    }

    override fun updatePlayerTitleList(player: OfflinePlayer, list: List<String>) {
        TODO("Not yet implemented")
    }

    override fun insertPlayerData(player: OfflinePlayer, use: String, list: List<String>) {
        TODO("Not yet implemented")
    }
}