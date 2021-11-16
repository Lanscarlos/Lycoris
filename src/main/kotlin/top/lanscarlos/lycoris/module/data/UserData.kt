package top.lanscarlos.lycoris.module.data

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import taboolib.platform.util.sendLang
import top.lanscarlos.lycoris.api.LycorisAPI
import top.lanscarlos.lycoris.core.User
import top.lanscarlos.lycoris.module.database.Database

object UserData {

    /**
     * 用户数据
     * */
    private val users = mutableMapOf<OfflinePlayer, User>()


    fun init() {
        Bukkit.getOfflinePlayers().forEach {
            users[it] = getUser(it)
        }
    }

    /**
     * 获取用户实例
     * */
    fun getUser(player: OfflinePlayer): User {
        return users[player] ?: let {
            Database.instance.insertPlayerData(player, LycorisAPI.getDefaultTitle().getId(), mapOf())
            val user = User(player)
            users[player] = user
            user
        }
    }

    /**
     * 更新在线玩家的称号
     * */
    fun updatePlayerTitle() {
        Bukkit.getOnlinePlayers().forEach {
            getUser(it).updateTitle()
        }
    }

    /**
     * 清空在线玩家的Buff
     * */
    fun clearPlayerBuff() {
        users.forEach {
            it.value.clearBuff()
        }
    }

    /**
     * 更新在线玩家的Buff
     * */
    fun updatePlayerBuff() {
        users.forEach {
            it.value.updateBuff()
        }
    }
}

fun OfflinePlayer.getUser(): User {
    return UserData.getUser(this)
}