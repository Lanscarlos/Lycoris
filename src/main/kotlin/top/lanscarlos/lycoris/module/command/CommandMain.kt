package top.lanscarlos.lycoris.module.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.common.platform.function.warning
import taboolib.expansion.createHelper
import taboolib.module.kether.Kether
import taboolib.platform.util.sendLang
import top.lanscarlos.lycoris.Lycoris
import top.lanscarlos.lycoris.module.config.TitleLoader
import top.lanscarlos.lycoris.module.ui.MenuShop
import top.lanscarlos.lycoris.module.ui.Template

@CommandHeader(name = "AiurTitle", aliases = ["atitle", "at"], permission = "aiurtitle.command", permissionDefault = PermissionDefault.TRUE, permissionMessage = "测试 permissionMessage 信息")
object CommandMain {

    @CommandBody
    val main = mainCommand {
        execute<CommandSender> { _, _, _ ->
            createHelper()
        }
    }

    @CommandBody(permission = "aiurtitle.command.reload", permissionDefault = PermissionDefault.OP)
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            try {
                Lycoris.config.reload()
                Lycoris.menuConfig.reload()

                TitleLoader.loadTitles()
                Template.loadTemplates()
                MenuShop.loadMenu()

                sender.sendLang("Plugin-Reload-Loaded")
            } catch (e : Exception) {
                warning(e.stackTrace)
                sender.sendLang("Plugin-Reload-Failed")
            }
        }
    }

    @CommandBody(permission = "aiurtitle.command.item", permissionDefault = PermissionDefault.OP)
    val title = CommandTitle

    @CommandBody(permission = "aiurtitle.command.user", permissionDefault = PermissionDefault.OP, optional = true)
    val user = CommandUser.command

    @CommandBody(permission = "aiurtitle.command.user", permissionDefault = PermissionDefault.OP, optional = true)
    val test = subCommand {
        execute<Player> { sender, _, _ ->
            MenuShop.openMenu(sender)
        }
    }

    @CommandBody(permission = "aiurtitle.command.user", permissionDefault = PermissionDefault.OP, optional = true)
    val debug = subCommand {
        execute<Player> { sender, _, _ ->
            Kether.scriptRegistry.registeredNamespace.forEach {
                sender.sendMessage("§c[System] §7  ${it}: §r${Kether.scriptRegistry.getRegisteredActions(it)}")
            }
        }
    }

}