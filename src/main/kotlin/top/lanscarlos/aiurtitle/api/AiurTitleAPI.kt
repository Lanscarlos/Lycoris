package top.lanscarlos.aiurtitle.api

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import top.lanscarlos.aiurtitle.core.title.Title
import top.lanscarlos.aiurtitle.module.data.TitleData
import top.lanscarlos.aiurtitle.module.data.UserData

object AiurTitleAPI {

    fun isTitle(id: String) : Boolean {
        return TitleData.isTitle(id)
    }

    fun getTitleIds() : List<String> {
        return TitleData.getTitleIds()
    }

    fun getDefaultTitle() : Title {
        return TitleData.getDefaultTitle()
    }

    fun getTitle(id: String) : Title {
        return TitleData.getTitle(id)
    }

    fun getTitleDisplay(id: String) : String {
        return getTitle(id).getTitleDisplay()
    }

    fun getTitles() : Map<String, Title> {
        return TitleData.getTitles()
    }



    /**
     * 获取玩家当前使用的称号ID
     * */
    fun getTitleIdPlayerUse(player: OfflinePlayer) : String {
        return UserData.getPlayerTitle(player)
    }

    /**
     * 设置玩家当前使用的称号ID
     * */
    fun setTitlePlayerUse(player: OfflinePlayer, id: String) {
        UserData.setPlayerTitle(player, id)
    }

    /**
     * 给予玩家称号
     * */
    fun addPlayerTitle(player: OfflinePlayer, id: String) {
        UserData.addPlayerTitle(player, id)
    }

    /**
     * 给予玩家称号
     * */
    fun removePlayerTitle(player: OfflinePlayer, id: String) {
        UserData.removePlayerTitle(player, id)
    }



    fun getPlayerTitlePiece(player: OfflinePlayer) : Int {
        return UserData.getPlayerTitlePiece(player)
    }

    fun givePlayerTitlePiece(player: OfflinePlayer, amount: Int) {
        UserData.givePlayerTitlePiece(player, amount)
    }

    fun takePlayerTitlePiece(player: OfflinePlayer, amount: Int) {
        UserData.takePlayerTitlePiece(player, amount)
    }

    fun setPlayerTitlePiece(player: OfflinePlayer, amount: Int) {
        UserData.setPlayerTitlePiece(player, amount)
    }




    fun playerHasTitle(player: OfflinePlayer, id: String) : Boolean {
        return if (id == getDefaultTitle().getId()) true else getPlayerTitleList(player).contains(id)
    }

    /**
     * 获取玩家所拥有的称号ID列表
     * */
    fun getPlayerTitleList(name: String) : List<String> {
        getOfflinePlayer(name)?.let {
            return getPlayerTitleList(it)
        }
        return mutableListOf()
    }
    fun getPlayerTitleList(player: OfflinePlayer) : List<String> {
        return UserData.getPlayerTitleList(player)
    }

    fun getTitlePlayerUse(player: OfflinePlayer) : Title {
        return getTitle(getTitleIdPlayerUse(player))
    }

    fun getOfflinePlayer(name: String) : OfflinePlayer? {
        return Bukkit.getOfflinePlayers().let {
            it.forEach { offline ->
                if (offline.name == name) {
                    return@let offline
                }
            }
            return null
        }
    }
}