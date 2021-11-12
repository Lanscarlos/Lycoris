package top.lanscarlos.aiurtitle

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile
import taboolib.module.lang.Language
import taboolib.platform.BukkitPlugin
import top.lanscarlos.aiurtitle.module.config.TitleLoader
import top.lanscarlos.aiurtitle.module.data.TitleData
import top.lanscarlos.aiurtitle.module.data.UserData
import top.lanscarlos.aiurtitle.module.ui.MenuShop
import top.lanscarlos.aiurtitle.module.ui.Template

object AiurTitle : Plugin() {

    var debug = 0

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

    override fun onLoad() {
        Language.default = "zh_CN"
    }

    override fun onEnable() {

        TitleLoader.loadTitles()
        Template.loadTemplates()
        MenuShop.loadMenu()

        UserData.init()

        info("Successfully running ExamplePlugin!")
    }

    fun debug(level: Int, message: String) {
        if (debug >= level) info(message)
    }
}