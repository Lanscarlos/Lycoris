package top.lanscarlos.aiurtitle.module.data

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import top.lanscarlos.aiurtitle.module.database.Database

object UserData {
    /**
     * 玩家当前正在使用的称号
     * uuid.toString -> Title.id
     * */
    private val use = mutableMapOf<String, String>()


    /**
     * 玩家的称号碎片数量
     * uuid.toString -> Piece
     * */
    private val piece = mutableMapOf<String, Int>()

    /**
     * 玩家拥有的所有称号
     * uuid.toString -> List<Title.id>
     * */
    private val list = mutableMapOf<String, MutableList<String>>()


    fun init() {
        val uuids = Database.instance.getUniqueIdList()
        Bukkit.getOfflinePlayers().forEach {
            if(!uuids.contains(it.uniqueId.toString())) {
                Database.instance.insertPlayerData(it, "default", listOf())
            }
        }
    }

    /**
     * 获取玩家正在使用的称号
     * */
    fun getPlayerTitle(player: OfflinePlayer): String {
        val uuid = player.uniqueId.toString()
        use[uuid] = use[uuid] ?: Database.instance.getPlayerTitleUse(player)
        return use[uuid]!!
    }

    /**
     * 设置玩家正在使用的称号
     * */
    fun setPlayerTitle(player: OfflinePlayer, id : String) {
        val uuid = player.uniqueId.toString()
        use[uuid] = id
        Database.instance.updatePlayerTitleUse(player, id)
//        DataLoader.setTitleUse(player, id)
    }

    fun getPlayerTitlePiece(player: OfflinePlayer) : Int {
        val uuid = player.uniqueId.toString()
        piece[uuid] = piece[uuid] ?: Database.instance.getPlayerTitlePiece(player)
        return piece[uuid]!!
    }

    fun setPlayerTitlePiece(player: OfflinePlayer, amount: Int) {
        val uuid = player.uniqueId.toString()
        val piece = if (amount > 0) amount else 0
        this.piece[uuid] = piece
        Database.instance.updatePlayerTitlePiece(player, piece)
    }

    fun givePlayerTitlePiece(player: OfflinePlayer, amount: Int) {
        setPlayerTitlePiece(player, getPlayerTitlePiece(player) + amount)
    }

    fun takePlayerTitlePiece(player: OfflinePlayer, amount: Int) {
        setPlayerTitlePiece(player, getPlayerTitlePiece(player) - amount)
    }

    /**
     * 获取玩家拥有的所有称号
     * */
    fun getPlayerTitleList(player: OfflinePlayer): MutableList<String> {
        val uuid = player.uniqueId.toString()
        list[uuid] = list[uuid] ?: Database.instance.getPlayerTitleList(player)
        return list[uuid]!!
    }

    /**
     * 设置玩家拥有的所有称号
     * */
    private fun setPlayerTitleList(player: OfflinePlayer, list : MutableList<String>) {
        val uuid = player.uniqueId.toString()
        this.list[uuid] = list
        Database.instance.updatePlayerTitleList(player, list)
    }

    /**
     * 给予玩家称号
     * */
    fun addPlayerTitle(player: OfflinePlayer, id: String) {
        setPlayerTitleList(player, getPlayerTitleList(player).also { if (!it.contains(id)) it.add(id) })
    }

    /**
     * 移除玩家指定称号
     * */
    fun removePlayerTitle(player: OfflinePlayer, id : String) {
        if (getPlayerTitle(player) == id) setPlayerTitle(player, "default")
        setPlayerTitleList(player, getPlayerTitleList(player).also { if (it.contains(id)) it.remove(id) })
    }

    /**
     * 清除内存中的玩家缓存数据
     * */
    fun clearPlayerCache() {
        use.clear()
        piece.clear()
        list.clear()
    }

}