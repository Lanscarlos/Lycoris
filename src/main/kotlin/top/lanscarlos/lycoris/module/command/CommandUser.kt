package top.lanscarlos.lycoris.module.command

import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang
import top.lanscarlos.lycoris.api.LycorisAPI
import top.lanscarlos.lycoris.module.data.getUser

object CommandUser {

    val command = subCommand {
        dynamic(commit = "player") {
            suggestion<ProxyCommandSender> { _, _ ->
                Bukkit.getOfflinePlayers().map { it.name ?: "Player" }
            }
            literal("title") {

                literal("add") {
                    dynamic(commit = "add-title-id", dynamic = addTitleDynamic)
                }

                literal("clear") {
                    execute<ProxyCommandSender> { sender, context, _ ->
                        val name = context.argument(-2)
                        val offline = Bukkit.getOfflinePlayers().first { it.name == name }
                        offline.getUser().setUse(LycorisAPI.getDefaultTitle().getId())
                        sender.sendLang("Command-User-Title-Clear-Success", name)
                    }
                }

                literal("set") {
                    dynamic(commit = "set-title-id", dynamic = setTitleDynamic)
                }

                literal("remove") {
                    dynamic(commit = "remove-title-id", dynamic = removeTitleDynamic)
                }
            }

            literal("piece") {

                literal("give") {
                    dynamic(commit = "give-piece-amount") {
                        execute<ProxyCommandSender> { sender, context, argument ->
                            val name = context.argument(-3)
                            val offline = Bukkit.getOfflinePlayers().first { it.name == name }
                            offline.getUser().givePiece(argument.toInt())
                            sender.sendLang("Command-User-Piece-Give-Success", name, argument)
                        }
                    }
                }

                literal("set") {
                    dynamic(commit = "set-piece-amount") {
                        execute<ProxyCommandSender> { sender, context, argument ->
                            val name = context.argument(-3)
                            val offline = Bukkit.getOfflinePlayers().first { it.name == name }
                            offline.getUser().setPiece(argument.toInt())
                            sender.sendLang("Command-User-Piece-Set-Success", name, argument)
                        }
                    }
                }

                literal("take") {
                    dynamic(commit = "take-piece-amount") {
                        execute<ProxyCommandSender> { sender, context, argument ->
                            val name = context.argument(-3)
                            val offline = Bukkit.getOfflinePlayers().first { it.name == name }
                            offline.getUser().takePiece(argument.toInt())
                            sender.sendLang("Command-User-Piece-Take-Success", name, argument)
                        }
                    }
                }
            }
        }
    }

    private val addTitleDynamic: CommandBuilder.CommandComponentDynamic.() -> Unit = {
        suggestion<ProxyCommandSender> { _, context ->
            val name = context.argument(-3)

            // 获取玩家已拥有的永久称号
            Bukkit.getOfflinePlayers().firstOrNull { it.name == name }?.let { offline ->
                // 显示所有非永久称号
                val list = offline.getUser().getPermanentTitles()
                LycorisAPI.getTitles().filter { it.value.isAvailable() && it.key !in list }.map { it.key }
            }
        }

        execute<ProxyCommandSender> { sender, context, argument ->
            val name = context.argument(-3)
            val offline = Bukkit.getOfflinePlayers().first { it.name == name }
            val title = LycorisAPI.getTitle(argument)

            offline.getUser().addTitle(title.getId(), title.getDuration())
            sender.sendLang("Command-User-Title-Add-Success", name, title.getDisplay())
        }

        dynamic(optional = true) {
            execute<ProxyCommandSender> { sender, context, argument ->
                val name = context.argument(-4)
                val offline = Bukkit.getOfflinePlayers().first { it.name == name }
                val title = LycorisAPI.getTitle(context.argument(-1))
                val time = argument.toLong()

                offline.getUser().addTitle(title.getId(), time)
                sender.sendLang("Command-User-Title-Add-Success", name, title.getDisplay())
            }
        }
    }

    private val setTitleDynamic: CommandBuilder.CommandComponentDynamic.() -> Unit = {
        suggestion<ProxyCommandSender> { _, context ->
            val name = context.argument(-3)

            // 显示已有的称号
            Bukkit.getOfflinePlayers().firstOrNull { it.name == name }?.getUser()?.getAvailableTitles()?.let {
                mutableListOf<String>().apply {
                    add(LycorisAPI.getDefaultTitle().getId())
                    addAll(it)
                }
            }
        }
        execute<ProxyCommandSender> { sender, context, argument ->
            val name = context.argument(-3)
            val offline = Bukkit.getOfflinePlayers().first { it.name == name }
            val title = LycorisAPI.getTitle(argument)
            offline.getUser().setUse(title.getId())
            sender.sendLang("Command-User-Title-Set-Success", name, title.getDisplay())
        }
    }

    private val removeTitleDynamic: CommandBuilder.CommandComponentDynamic.() -> Unit = {
        suggestion<ProxyCommandSender> { _, context ->
            val name = context.argument(-3)
            Bukkit.getOfflinePlayers().firstOrNull { it.name == name }?.getUser()?.getTitles()
        }
        execute<ProxyCommandSender> { sender, context, argument ->
            val name = context.argument(-3)
            val offline = Bukkit.getOfflinePlayers().first { it.name == name }
            val title = LycorisAPI.getTitle(argument)
            offline.getUser().removeTitle(title.getId())
            sender.sendLang("Command-User-Title-Remove-Success", name, title.getDisplay())
        }
    }


    fun handleIncorrectCommand(sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, index: Int, state: Int) {
        when(index) {
            2 -> {
                // player error
                when(state) {
                    1 -> sender.sendLang("help") // 信息帮助
                    else -> sender.sendLang("Error-Unknown-Player", context.argument(0))
                }
            }
            3 -> {
                // module error
            }
            4 -> {
                // method error
            }
            5 -> {
                // arg error
            }
        }
    }

}