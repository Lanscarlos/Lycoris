package top.lanscarlos.aiurtitle.module.command

import org.bukkit.command.CommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.subCommand

object CommandTitle {

    @CommandBody(aliases = ["s"], permission = "aiurtitle.command.title.set", permissionDefault = PermissionDefault.OP, optional = true)
    val set = subCommand {
        dynamic(commit = "id") {
            suggestion<CommandSender> { _, _ ->
                listOf("title1", "title2")
            }
            execute<CommandSender> {sender, _, _ ->
                sender.sendMessage("ahhhhh")
            }
        }
    }
}