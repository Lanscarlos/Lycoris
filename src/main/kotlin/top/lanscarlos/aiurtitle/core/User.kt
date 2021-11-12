package top.lanscarlos.aiurtitle.core

import org.bukkit.OfflinePlayer
import top.lanscarlos.aiurtitle.module.database.Database

/**
 * 代表一个使用Title的用户
 * */
class User(private val offline: OfflinePlayer) {

    private var use: String = Database.instance.getPlayerTitleUse(offline) // 当前正在使用的称号 id
    private val repository: MutableMap<String, Long> = mutableMapOf() // 称号仓库 Title id -> 到期时间

    /**
     * 获取玩家正在使用的称号 id
     * */
    fun getUse(): String {
        if (!repository.contains(use)) {
            // 检测玩家是否拥有此称号
            setUse("default")
        }
        return use
    }

    /**
     * 设置用户称号
     * @param id 称号id
     * */
    fun setUse(id: String) {
        if (id != "default" && !repository.contains(id)) {
            return
        }
        use = id
    }

    /**
     * 给予用户称号
     * @param id 称号id
     * @param duration 持续时间, -1为永久
     * */
    fun addTitle(id: String, duration: Long = -1) {
        if (!repository.contains(id)) {
            repository[id] = duration
            return
        }
        val deadline = repository[id]
        if (deadline!! < 0) return
        if (duration < 0) {
            repository[id] = -1
        }else {
            repository[id] = deadline + deadline
        }
    }

    /**
     * 移除用户称号
     * @param id 称号id
     * */
    fun removeTitle(id: String) {
        if (!repository.contains(id)) return
        repository.remove(id)
    }
}