package top.lanscarlos.lycoris.module.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.common.platform.function.warning
import taboolib.expansion.createHelper
import taboolib.module.chat.TellrawJson
import taboolib.module.kether.Kether
import taboolib.platform.util.sendLang
import top.lanscarlos.lycoris.Lycoris
import top.lanscarlos.lycoris.module.ui.MenuRepository
import top.lanscarlos.lycoris.module.ui.MenuShop

@CommandHeader(name = "Lycoris", aliases = ["ly"], permission = "lycoris.command", permissionDefault = PermissionDefault.TRUE, permissionMessage = "测试 permissionMessage 信息")
object CommandMain {

    @CommandBody
    val main = mainCommand {
        incorrectCommand { sender, context, index, state ->
            when(index) {
                1 -> {
                    // lycoris error<<
                    TellrawJson().sendTo(sender) {
                        append("  ")
                        append("§3Lycoris").hoverText("§7Lycoris is modern and advanced Minecraft menu-plugin")
                    }
                }

                2 -> {
                    // lycoris user player<<
                    when(context.argument(-1).lowercase()) {
                        "user" -> CommandUser.handleIncorrectCommand(sender, context, index, state)
                    }
                }
            }
        }
        execute<CommandSender> { sender, _, _ ->
            sender.sendMessage("帮助？")
            createHelper()
        }
    }

    @CommandBody(permission = "lycoris.command.reload", permissionDefault = PermissionDefault.OP)
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            try {
                Lycoris.onReload()
                sender.sendLang("Plugin-Reload-Loaded")
            } catch (e : Exception) {
                warning(e.stackTrace)
                sender.sendLang("Plugin-Reload-Failed")
            }
        }
    }

    @CommandBody(permission = "lycoris.command.title", permissionDefault = PermissionDefault.OP)
    val title = CommandTitle

    @CommandBody(permission = "lycoris.command.user", permissionDefault = PermissionDefault.OP, optional = true)
    val user = CommandUser.command

    @CommandBody(permission = "lycoris.command.shop", permissionDefault = PermissionDefault.OP)
    val shop = subCommand {
        execute<Player> { sender, _, _ ->
            MenuShop.openMenu(sender)
        }
    }

    @CommandBody(permission = "lycoris.command.repository", permissionDefault = PermissionDefault.OP)
    val repository = subCommand {
        execute<Player> { sender, _, _ ->
            MenuRepository.openMenu(sender)
        }
    }

    @CommandBody(permission = "lycoris.command.user", permissionDefault = PermissionDefault.OP, optional = true)
    val debug = subCommand {
        execute<Player> { sender, _, _ ->
            Kether.scriptRegistry.registeredNamespace.forEach {
                sender.sendMessage("§c[System] §7  ${it}: §r${Kether.scriptRegistry.getRegisteredActions(it)}")
            }
        }
    }

}