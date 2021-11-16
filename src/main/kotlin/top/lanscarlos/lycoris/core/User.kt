package top.lanscarlos.lycoris.core

import org.bukkit.OfflinePlayer
import taboolib.expansion.getDataContainer
import top.lanscarlos.lycoris.api.LycorisAPI
import top.lanscarlos.lycoris.core.title.Title
import top.lanscarlos.lycoris.module.data.UserData
import top.lanscarlos.lycoris.module.database.Database

/**
 * 代表一个使用Title的用户
 * */
class User(private val offline: OfflinePlayer) {

    private var use: String = Database.instance.getPlayerTitleUse(offline) // 当前正在使用的称号 id
    private var piece: Int = Database.instance.getPlayerTitlePiece(offline) // 称号碎片数量
    private val repository: MutableMap<String, Long> = mutableMapOf() // 称号仓库 Title id -> 到期时间


    /**
     * 检查当前使用的称号是否失效
     * */
    fun updateTitle(): Boolean {
        if (!LycorisAPI.isTitle(getUse())) {
            setUse(LycorisAPI.getDefaultTitle().getId())
            return false
        }
        if (!LycorisAPI.getTitle(getUse()).isAvailable()) {
            setUse(LycorisAPI.getDefaultTitle().getId())
            return false
        }
        if (getDeadline(getUse()) >0 && System.currentTimeMillis() >= getDeadline(getUse())) {
            setUse(LycorisAPI.getDefaultTitle().getId())
            return false
        }
        return true
    }

    fun clearBuff() {
        offline.player?.let {
            LycorisAPI.getTitle(getUse()).getBuff().forEach { potion ->
                it.removePotionEffect(potion.type)
            }
        }
    }

    fun updateBuff() {
        offline.player?.let {
            LycorisAPI.getTitle(getUse()).getBuff().forEach { potion ->
                it.addPotionEffect(potion)
            }
        }
    }

    /**
     * 获取玩家正在使用的称号 id
     * */
    fun getUse(): String {
        if (use != LycorisAPI.getDefaultTitle().getId() && !repository.contains(use)) {
            // 检测玩家是否拥有此称号
            setUse(LycorisAPI.getDefaultTitle().getId())
        }
        return use
    }

    /**
     * 设置用户称号
     * @param id 称号id
     * */
    fun setUse(id: String) {
        if (!LycorisAPI.isAvailableTitle(id)) return // 非有效称号
        if (id != LycorisAPI.getDefaultTitle().getId() && !repository.contains(id)) return // 玩家未拥有称号

        offline.player?.let {
            LycorisAPI.getTitle(this.use).getBuff().forEach { potion ->
                it.removePotionEffect(potion.type)
            }
            LycorisAPI.getTitle(id).getBuff().forEach { potion ->
                it.addPotionEffect(potion)
            }
        }
        use = id
        Database.instance.updatePlayerTitleUse(offline, id)
    }

    /**
     * 获取某一称号的到期时间
     * */
    fun getDeadline(id: String): Long {
        if (id == LycorisAPI.getDefaultTitle().getId()) return -1
        return repository[id] ?: error("Title Not Found!")
    }

    fun hasTitle(id: String): Boolean {
        return if (id == LycorisAPI.getDefaultTitle().getId()) true else repository.contains(id)
    }

    /**
     * 获取正在使用的称号实例
     * */
    fun getTitle(): Title {
        return LycorisAPI.getTitle(getUse())
    }

    /**
     * 给予用户称号
     * @param id 称号id
     * @param duration 持续时间, -1为永久
     * */
    fun addTitle(id: String, duration: Long = -1) {
        if (!LycorisAPI.isTitle(id)) return // 非称号

        if (!repository.contains(id)) {
            repository[id] = duration
            Database.instance.updatePlayerRepository(offline, repository)
            return
        }
        val deadline = repository[id]
        if (deadline!! < 0) return
        if (duration < 0) {
            repository[id] = -1
        }else {
            repository[id] = deadline + deadline
        }
        Database.instance.updatePlayerRepository(offline, repository)
    }

    /**
     * 移除用户称号
     * @param id 称号id
     * */
    fun removeTitle(id: String) {
        if (!repository.contains(id)) return

        repository.remove(id)
        Database.instance.updatePlayerRepository(offline, repository)
    }

    /**
     * 获取已经有的所有永久称号
     * */
    fun getPermanentTitles(): List<String> {
        return repository.filter { it.value < 0 }.map { it.key }
    }

    /**
     * 获取玩家所有有效的称号
     * */
    fun getAvailableTitles(): List<String> {
        val now = System.currentTimeMillis()
        val available = LycorisAPI.getTitles().filter { it.value.isAvailable() }.keys
        return getRepository().filter { it.value < 0 || (it.key in available && it.value > now) }.map { it.key }
    }

    /**
     * 获取玩家的所有称号
     * */
    fun getTitles(): List<String> {
        return getRepository().map { it.key }
    }

    fun getRepository(): Map<String, Long> {
        return repository
    }

    /**
     * 获取玩家的称号碎片
     * */
    fun getPiece(): Int {
        return piece
    }

    /**
     * 设置称号碎片
     * */
    fun setPiece(piece: Int) {
        this.piece = if (piece > 0) piece else 0
        Database.instance.updatePlayerTitlePiece(offline, piece)
    }

    /**
     * 给予称号碎片
     * */
    fun givePiece(piece: Int) {
        setPiece(getPiece() + piece)
    }

    /**
     * 扣除称号碎片
     * */
    fun takePiece(piece: Int) {
        setPiece(getPiece() - piece)
    }
}