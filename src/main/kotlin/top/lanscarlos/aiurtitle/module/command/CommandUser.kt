package top.lanscarlos.aiurtitle.module.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.*
import taboolib.platform.util.sendLang
import top.lanscarlos.aiurtitle.api.AiurTitleAPI
import top.lanscarlos.aiurtitle.module.data.TitleData
import top.lanscarlos.aiurtitle.module.data.UserData

object CommandUser {

    val command = subCommand {
        dynamic(commit = "player", optional = true, dynamic = playerDynamic)
        execute<CommandSender> { sender, _, _ ->
            sender.sendLang("Command-User-Help")
        }
    }

    /**
     * at user <arg:player>
     * */
    private val playerDynamic: CommandBuilder.CommandComponentDynamic.() -> Unit = {
        suggestion<CommandSender>(uncheck = true) { _, _ ->
            Bukkit.getOfflinePlayers().map { it.name ?: "Player" }
        }
        execute<CommandSender> { sender, _, arg ->
            AiurTitleAPI.getOfflinePlayer(arg)?:let {
                sender.sendLang("Error-Unknown-Player", arg)
                return@execute
            }
            sender.sendLang("Command-User-Help")
        }

        dynamic(commit = "module", optional = true, dynamic = moduleDynamic)
    }

    /**
     * at user <-1:player> <arg:module>
     * */
    private val moduleDynamic: CommandBuilder.CommandComponentDynamic.() -> Unit = {
        suggestion<CommandSender>(uncheck = true) { _, _ ->
            listOf(
                "title",
                "piece"
            )
        }
        execute<CommandSender> { sender, context, arg ->
            AiurTitleAPI.getOfflinePlayer(context.argument(-1))?:let {
                sender.sendLang("Error-Unknown-Player", context.argument(-1))
                return@execute
            }
            when(arg.lowercase()) {
                "title" -> sender.sendLang("Command-User-Title-Help")
                "piece" -> sender.sendLang("Command-User-Piece-Help")
                else -> sender.sendLang("Command-User-Help")
            }
        }

        dynamic(commit = "method", optional = true, dynamic = methodDynamic)
    }

    /**
     * at user <-2:player> <-1:module> <arg:method>
     * */
    private val methodDynamic: CommandBuilder.CommandComponentDynamic.() -> Unit = {
        suggestion<CommandSender>(uncheck = true) { _, context ->
            when(context.argument(-1).lowercase()) {
                "title" -> listOf("clear", "give", "set", "remove")
                "piece" -> listOf("give", "set", "take")
                else -> listOf()
            }
        }
        execute<CommandSender> { sender, context, arg ->
            AiurTitleAPI.getOfflinePlayer(context.argument(-2))?:let {
                sender.sendLang("Error-Unknown-Player", context.argument(-2))
                return@execute
            }
            when(context.argument(-1).lowercase()) {
                "title" -> {
                    when(arg.lowercase()) {
                        "clear" -> {
                            AiurTitleAPI.getOfflinePlayer(context.argument(-2))?.let {
                                AiurTitleAPI.setTitlePlayerUse(it, TitleData.getDefaultTitle().getId())
                                sender.sendLang("Command-User-Title-Clear-Success", context.argument(-2))
                                return@execute
                            }
                            sender.sendLang("Error-Unknown-Player", context.argument(-2))
                            return@execute
                        }
                        "give" -> sender.sendLang("Command-User-Title-Give-Help")
                        "set" -> sender.sendLang("Command-User-Title-Set-Help")
                        "remove" -> sender.sendLang("Command-User-Title-Remove-Help")
                        else -> sender.sendLang("Command-User-Title-Help")
                    }
                }
                "piece" -> {
                    when(arg.lowercase()) {
                        "give" -> sender.sendLang("Command-User-Piece-Give-Help")
                        "set" -> sender.sendLang("Command-User-Piece-Set-Help")
                        "take" -> sender.sendLang("Command-User-Piece-Take-Help")
                        else -> sender.sendLang("Command-User-Piece-Help")
                    }
                }
                else -> sender.sendLang("Command-User-Help")
            }
        }

        dynamic(commit = "arg", optional = true, dynamic = argDynamic)
    }

    /**
     * at user <-3:player> <-2:module> <-1:method> <arg:arg>
     * */
    private val argDynamic: CommandBuilder.CommandComponentDynamic.() -> Unit = {
        suggestion<CommandSender>(uncheck = true) { _, context ->
            when(context.argument(-2).lowercase()) {
                "title" -> {
                    when(context.argument(-1).lowercase()) {
                        "give" -> {
                            AiurTitleAPI.getPlayerTitleList(context.argument(-3)).let { list ->
                                return@let mutableListOf<String>().apply {
                                    AiurTitleAPI.getTitles().forEach {
                                        if (!list.contains(it.key)) this.add(it.key)
                                    }
                                }
                            }
                        }
                        "set" -> {
                            mutableListOf<String>().apply {
                                add(AiurTitleAPI.getDefaultTitle().getId())
                                addAll(AiurTitleAPI.getPlayerTitleList(context.argument(-3)))
                            }
                        }
                        "remove" -> {
                            AiurTitleAPI.getPlayerTitleList(context.argument(-3))
                        }
                        else -> listOf()
                    }
                }
                "piece" -> listOf("1", "10", "100")
                else -> listOf()
            }
        }
        execute<CommandSender> { sender, context, arg ->
            val player = AiurTitleAPI.getOfflinePlayer(context.argument(-3))?:let {
                sender.sendLang("Error-Unknown-Player", arg)
                return@execute
            }
            when (context.argument(-2).lowercase()) {
                "title" -> {
                    if (context.argument(-1).equals("clear", true)) {
                        sender.sendLang("Command-User-Title-Clear-Help")
                        return@execute
                    }
                    if (!AiurTitleAPI.isTitle(arg)) {
                        sender.sendLang("Error-Unknown-Title", arg)
                        return@execute
                    }
                    when(context.argument(-1).lowercase()) {
                        "give" -> {
                            if(arg == AiurTitleAPI.getDefaultTitle().getId()) {
                                sender.sendLang("Command-User-Title-Give-Cannot-Give-Default")
                                return@execute
                            }
                            if(UserData.getPlayerTitleList(player).contains(arg)) {
                                sender.sendLang("Command-User-Title-Give-Already-Owned", context.argument(-3), arg)
                                return@execute
                            }
                            AiurTitleAPI.addPlayerTitle(player, arg)
                            sender.sendLang("Command-User-Title-Give-Success", context.argument(-3), arg)
                        }
                        "set" -> {
                            if(!AiurTitleAPI.playerHasTitle(player, arg)) {
                                sender.sendLang("Command-User-Title-Set-Not-Owned", context.argument(-3), arg)
                                return@execute
                            }
                            AiurTitleAPI.setTitlePlayerUse(player, arg)
                            sender.sendLang("Command-User-Title-Set-Success", context.argument(-3), arg)
                        }
                        "remove" -> {
                            if (arg == AiurTitleAPI.getDefaultTitle().getId()) {
                                sender.sendLang("Command-User-Title-Remove-Cannot-Remove-Default")
                                return@execute
                            }
                            if(!AiurTitleAPI.playerHasTitle(player, arg)) {
                                sender.sendLang("Command-User-Title-Remove-Not-Owned", context.argument(-3), arg)
                                return@execute
                            }
                            AiurTitleAPI.removePlayerTitle(player, arg)
                            sender.sendLang("Command-User-Title-Remove-Success", context.argument(-3), arg)
                        }
                    }
                }
                "piece" -> {
                    if (!Regex("^[1-9]\\d*$").matches(arg)) {
                        sender.sendLang("Error-Unknown-Title", arg)
                        return@execute
                    }
                    when(context.argument(-1).lowercase()) {
                        "give" -> {
                            UserData.givePlayerTitlePiece(player, arg.toInt())
                            sender.sendLang("Command-User-Piece-Give-Success", context.argument(-3), arg)
                        }
                        "set" -> {
                            UserData.setPlayerTitlePiece(player, arg.toInt())
                            sender.sendLang("Command-User-Piece-Set-Success", context.argument(-3), arg)
                        }
                        "take" -> {
                            UserData.takePlayerTitlePiece(player, arg.toInt())
                            sender.sendLang("Command-User-Piece-Take-Success", context.argument(-3), arg)
                        }
                    }
                }
            }
        }
    }

//
////    @CommandBody(permission = "aiurtitle.command.user.give", permissionDefault = PermissionDefault.OP)
//    val give = onCommand { sender, context, argument, player ->
//        Data.addPlayerTitle(player, argument)
//        sender.sendLang("Command-Give-Success", context.argument(-1), argument)
//    }
//
////    @CommandBody(permission = "aiurtitle.command.user.remove", permissionDefault = PermissionDefault.OP)
//    val remove = onCommand { sender, context, argument, player ->
//        Data.removePlayerTitle(player, argument)
//        sender.sendLang("Command-Remove-Success", context.argument(-1), argument)
//    }
//
////    @CommandBody(permission = "aiurtitle.command.user.set", permissionDefault = PermissionDefault.OP)
//    val set = onCommand { sender, context, argument, player ->
//        Data.setPlayerTitle(player, argument)
//        sender.sendLang("Command-Set-Success", context.argument(-1), argument)
//    }
//
//    private fun onCommand(
//        method: (sender: CommandSender, context: CommandContext<CommandSender>, arg: String, player: OfflinePlayer) -> Unit
//    ) : SimpleCommandBody {
//        return subCommand {
//            dynamic(commit = "player") {
//                suggestion<CommandSender>(uncheck = false) { _, _ ->
//                    Bukkit.getOfflinePlayers().map { it.name ?: "Unknown" }
//                }
//                dynamic(commit = "称号ID") {
//                    suggestion<CommandSender>(uncheck = false) { _, _ ->
//                        Data.titles.map { it.key }
//                    }
//                    execute<CommandSender> { sender, context, arg ->
//                        val player = Bukkit.getOfflinePlayers().let {
//                            val name = context.argument(-1)
//                            it.forEach { offline ->
//                                if (offline.name == name) {
//                                    return@let offline
//                                }
//                            }
//                            sender.sendLang("Error-Player-Not-Exist", name)
//                            return@execute
//                        }
//                        if (!Data.isTitle(arg)) {
//                            sender.sendLang("Error-Title-Not-Exist", arg)
//                            return@execute
//                        }
//                        method(sender, context, arg, player)
//                    }
//                }
//            }
//        }
//    }

}