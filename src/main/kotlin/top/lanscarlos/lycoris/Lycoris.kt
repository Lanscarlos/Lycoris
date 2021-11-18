package top.lanscarlos.lycoris

import org.bukkit.Bukkit
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.*
import taboolib.common.platform.service.PlatformExecutor
import taboolib.common.util.sync
import taboolib.expansion.setupPlayerDatabase
import taboolib.library.configuration.YamlConfiguration
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile
import taboolib.module.kether.KetherShell
import taboolib.module.lang.Language
import taboolib.platform.BukkitPlugin
import top.lanscarlos.lycoris.core.Acquire
import top.lanscarlos.lycoris.module.data.TitleData
import top.lanscarlos.lycoris.module.data.UserData
import top.lanscarlos.lycoris.module.ui.Menu
import top.lanscarlos.lycoris.module.ui.MenuShop
import top.lanscarlos.lycoris.module.ui.Template
import java.io.File

object Lycoris : Plugin() {

    var debug = 0

    var period: Long = 1200
    var delay: Long = 200
    lateinit var task: PlatformExecutor.PlatformTask


    @Config
    lateinit var config: SecuredFile
        private set

    @Config(value = "menu-setting.yml")
    lateinit var menuConfig: SecuredFile
        private set

    @Config(value = "template.yml")
    lateinit var templateConfig: SecuredFile
        private set

    val plugin by lazy { BukkitPlugin.getInstance() }

    override fun onEnable() {

//        when(config.getString("").lowercase()) {
//            "mysql" -> {
//                info("正在启用MySQL数据库")
//                setupPlayerDatabase(config.getConfigurationSection("database.source.mysql"))
//            }
//            else -> {
//                info("正在启用SQLite数据库")
//                setupPlayerDatabase(File(getDataFolder(), "data.db"))
//            }
//        }

        period = config.getLong("scheduler-setting.period", period)
        delay = config.getLong("scheduler-setting.delay", delay)

        Acquire.loadAcquires()
        Template.loadTemplates()
        TitleData.loadTitles()
        Menu.init()

        UserData.init()

        info("Successfully running ExamplePlugin!")
    }

    override fun onActive() {
        task = getScheduler()
    }

    fun onReload() {
        config.reload()
        menuConfig.reload()
        templateConfig.reload()

        period = config.getLong("scheduler-setting.period", period)
        delay = config.getLong("scheduler-setting.delay", delay)

        Acquire.loadAcquires()
        Template.loadTemplates()
        TitleData.loadTitles()
        Menu.init()

        task.cancel()
        task = getScheduler()
    }

    private fun getScheduler(): PlatformExecutor.PlatformTask {
        return submit(period = period, delay = delay) {
            info("调度器在工作...")
            UserData.updatePlayerTitle()
            UserData.updatePlayerBuff()
        }
    }

    fun debug(level: Int, message: String) {
        if (debug >= level) info(message)
    }
}