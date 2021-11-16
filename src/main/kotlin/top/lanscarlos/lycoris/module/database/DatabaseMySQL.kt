package top.lanscarlos.lycoris.module.database

import com.google.gson.Gson
import com.google.gson.JsonParser
import org.bukkit.OfflinePlayer
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table
import taboolib.module.database.getHost
import top.lanscarlos.lycoris.Lycoris
import top.lanscarlos.lycoris.module.data.TitleData
import javax.sql.DataSource

class DatabaseMySQL : Database() {

    private val host = Lycoris.config.getHost("database.source.mysql")

    private val tablePrefix = Lycoris.config.getString("database.source.mysql.table-prefix", "aiurtitle_")

    private val tableUser = Table("${tablePrefix}_userdata", host) {
        add { id() }
        add("uuid") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.UNIQUE_KEY)
            }
        }
        add("name") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.UNIQUE_KEY)
            }
        }
        add("use") {
            type(ColumnTypeSQL.VARCHAR, 36) {

            }
        }
        add("piece") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.UNSIGNED)
                options(ColumnOptionSQL.ZEROFILL)
            }
        }
        add("repository") {
            type(ColumnTypeSQL.LONGTEXT) {

            }
        }
    }

    private val dataSource : DataSource by lazy {
        host.createDataSource()
    }

    init {
        tableUser.workspace(dataSource) { createTable() }.run()
    }

    override fun getUniqueIdList(): List<String> {
        return tableUser.select(dataSource) {
            rows("uuid")
        }.map { getString("uuid") }
    }

    override fun getPlayerTitleUse(player: OfflinePlayer) : String {
        return tableUser.select(dataSource) {
            rows("use")
            where("uuid" eq player.uniqueId.toString())
        }.firstOrNull { getString("use") } ?: TitleData.getDefaultTitle().getId()
    }

    override fun updatePlayerTitleUse(player: OfflinePlayer, id: String) {
        tableUser.update(dataSource) {
            where("uuid" eq player.uniqueId.toString())
            set("use", id)
        }
    }

    override fun getPlayerTitlePiece(player: OfflinePlayer) : Int {
        return tableUser.select(dataSource) {
            rows("piece")
            where("uuid" eq player.uniqueId.toString())
        }.firstOrNull { getInt("piece") } ?: 0
    }

    override fun updatePlayerTitlePiece(player: OfflinePlayer, amount: Int) {
        tableUser.update(dataSource) {
            where("uuid" eq player.uniqueId.toString())
            set("piece", amount)
        }
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

//    override fun getPlayerTitleList(player: OfflinePlayer) : MutableList<String> {
//        val json = tableUser.select(dataSource) {
//            rows("repository")
//            where("uuid" eq player.uniqueId.toString())
//        }.firstOrNull { getString("repository") } ?: "[\"default\"]"
//
//        return mutableListOf<String>().apply {
//            jsonParser.parse(json).asJsonArray.forEach{
//                add(it.asString)
//            }
//        }
//    }
//
//    override fun updatePlayerTitleList(player: OfflinePlayer, list: List<String>) {
//        tableUser.update(dataSource) {
//            where("uuid" eq  player.uniqueId.toString())
//            set("repository", gson.toJson(list))
//        }
//    }
//
//    override fun insertPlayerData(player: OfflinePlayer, use: String, list: List<String>) {
//        tableUser.insert(dataSource, "uuid", "name", "use", "repository") {
//            value(player.uniqueId.toString(), player.name!!, use, gson.toJson(list))
//        }
//    }

    companion object {
        val gson = Gson()
        val jsonParser = JsonParser()
    }

}