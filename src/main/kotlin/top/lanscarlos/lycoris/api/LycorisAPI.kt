package top.lanscarlos.lycoris.api

import org.bukkit.OfflinePlayer
import top.lanscarlos.lycoris.core.User
import top.lanscarlos.lycoris.core.title.Title
import top.lanscarlos.lycoris.module.data.TitleData
import top.lanscarlos.lycoris.module.data.getUser

object LycorisAPI {

    fun isTitle(id: String) : Boolean {
        return TitleData.isTitle(id)
    }

    fun isAvailableTitle(id: String) : Boolean {
        if (!isTitle(id)) return false
        return getTitle(id).isActive()
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
     * 获取玩家对应的用户实例
     * */
    fun getUser(player: OfflinePlayer): User {
        return player.getUser()
    }

}