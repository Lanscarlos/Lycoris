package top.lanscarlos.lycoris

import org.bukkit.Bukkit
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.common.util.sync
import taboolib.expansion.setupPlayerDatabase
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile
import taboolib.module.lang.Language
import taboolib.platform.BukkitPlugin
import top.lanscarlos.lycoris.module.data.TitleData
import top.lanscarlos.lycoris.module.data.UserData
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

//    override fun onLoad() {
//        Language.default = "zh_CN"
//    }

    override fun onEnable() {

        when(config.getString("").lowercase()) {
            "mysql" -> {
                info("正在启用MySQL数据库")
                setupPlayerDatabase(config.getConfigurationSection("database.source.mysql"))
            }
            else -> {
                info("正在启用SQLite数据库")
                setupPlayerDatabase(File(getDataFolder(), "data.db"))
            }
        }

        period = config.getLong("scheduler-setting.period", period)
        delay = config.getLong("scheduler-setting.delay", delay)

        TitleData.loadTitles()
        Template.loadTemplates()
        MenuShop.loadMenu()

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

        TitleData.loadTitles()
        Template.loadTemplates()
        MenuShop.loadMenu()

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