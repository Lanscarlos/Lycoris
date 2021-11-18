package top.lanscarlos.lycoris.core

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.module.configuration.util.getStringColored
import taboolib.module.ui.ClickEvent
import taboolib.platform.util.sendLang
import top.lanscarlos.lycoris.Lycoris
import top.lanscarlos.lycoris.api.LycorisAPI
import top.lanscarlos.lycoris.core.title.Title
import top.lanscarlos.lycoris.module.database.Database
import java.text.SimpleDateFormat

/**
 * 代表一个使用Title的用户
 * */
class User(private val offline: OfflinePlayer) {

    private var use: String = Database.instance.getPlayerTitleUse(offline) // 当前正在使用的称号 id
    private var piece: Int = Database.instance.getPlayerTitlePiece(offline) // 称号碎片数量
    private val repository: MutableMap<String, Long> = mutableMapOf() // 称号仓库 Title id -> 到期时间

    init {
        use = Database.instance.getPlayerTitleUse(offline)
        piece = Database.instance.getPlayerTitlePiece(offline)
        repository.putAll(Database.instance.getPlayerRepository(offline))
    }


    /**
     * 当仓库按钮被点击时触发
     * */
    fun onIconClicked(sender: Player, player: Player, menuType: String, title: Title, event: ClickEvent) {
        val values = mutableMapOf<String, Any>().apply {
            putAll(title.getProperties())
            put("menu-type", menuType)
            put("isAvailable", title.isActive())
            put("sender", sender)
            put("player", player)
            put("event", event)
            put("use", this@User.getUse() == title.getId())
        }

        if (!isTitleActive(title.getId()) || getUse() == title.getId()) return
        setUse(title.getId())
        submit { sender.closeInventory() }
        title.getTemplate(menuType).onIconClicked(player, event, values)
    }

    /**
     * 检查当前使用的称号是否失效
     * */
    fun updateTitle(): Boolean {
        if (!LycorisAPI.isTitle(getUse())) {
            setUse(LycorisAPI.getDefaultTitle().getId())
            return false
        }
        if (!LycorisAPI.getTitle(getUse()).isActive()) {
            offline.player?.sendLang("User-Title-Expire", LycorisAPI.getTitle(getUse()).getTitleDisplay())
            setUse(LycorisAPI.getDefaultTitle().getId())
            return false
        }
        if (getDeadline(getUse()) >0 && System.currentTimeMillis() >= getDeadline(getUse())) {
            offline.player?.sendLang("User-Title-Expire", LycorisAPI.getTitle(getUse()).getTitleDisplay())
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
        if (id == LycorisAPI.getDefaultTitle().getId()) return -1L
        return repository[id] ?: error("Title Not Found!")
    }

    /**
     * 用户是否拥有称号，不判断是否过期
     * */
    fun hasTitle(id: String): Boolean {
        return if (id == LycorisAPI.getDefaultTitle().getId()) true else repository.contains(id)
    }

    /**
     * 用户对此称号是否可用
     * */
    fun isTitleActive(id: String): Boolean {
        if (id == LycorisAPI.getDefaultTitle().getId()) return true
        if (!LycorisAPI.isTitle(id)) return false
        if (!LycorisAPI.getTitle(id).isActive()) return false
        if (!hasTitle(id)) return false

        val deadline = getDeadline(id)
        if (deadline < 0) return true
        return deadline > System.currentTimeMillis()
    }

    /**
     * 用户对此称号是否已过期
     * @return 若未拥有称号或已过期返回true，反之false
     * */
    fun isTitleExpired(id: String): Boolean {
        return !isTitleActive(id)
    }

    /**
     * 称号是否是永久时限
     * */
    fun isTitlePermanent(id: String): Boolean {
        if (id == LycorisAPI.getDefaultTitle().getId()) return true
        if (!hasTitle(id)) return false
        if (!LycorisAPI.getTitle(id).isActive()) return false
        return getDeadline(id) < 0
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
    fun addTitle(id: String) {
        if (!LycorisAPI.isTitle(id)) return // 非称号
        val deadline = if (repository.containsKey(id) && repository[id]!! > System.currentTimeMillis()) {
            repository[id]!!
        }else {
            System.currentTimeMillis()
        }
        if (deadline < 0) return
        val duration = LycorisAPI.getTitle(id).getDuration()
        if (duration < 0) {
            repository[id] = -1L
        }else {
            repository[id] = deadline + duration
        }
        Database.instance.updatePlayerRepository(offline, repository)
    }

    /**
     * 给予用户称号
     * @param id 称号id
     * @param duration 持续时间, -1为永久
     * */
    fun addTitle(id: String, duration: Long = -1L) {
        if (!LycorisAPI.isTitle(id)) return // 非称号
        val deadline = if (id in repository) {
            repository[id]!!
        }else {
            System.currentTimeMillis()
        }

        if (deadline < 0) return
        if (duration < 0) {
            repository[id] = -1L
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

        if (id == getUse()) {
            setUse(LycorisAPI.getDefaultTitle().getId())
        }

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
        val available = LycorisAPI.getTitles().filter { it.value.isActive() }.keys
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