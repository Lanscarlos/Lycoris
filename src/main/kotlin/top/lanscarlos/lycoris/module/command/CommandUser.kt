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
                LycorisAPI.getTitles().filter { it.key !in list }.map { it.key }
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
            sender.sendLang("Command-User-Title-Set-Success", name, argument)
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
            sender.sendLang("Command-User-Title-Remove-Success", name, argument)
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

//
//
//
//    /**
//     * at user <-1:player> <arg:module>
//     * */
//    private val moduleDynamic: CommandBuilder.CommandComponentDynamic.() -> Unit = {
//        suggestion<CommandSender>(uncheck = true) { _, _ ->
//            listOf(
//                "title",
//                "piece"
//            )
//        }
//        execute<CommandSender> { sender, context, arg ->
//            LycorisAPI.getOfflinePlayer(context.argument(-1))?:let {
//                sender.sendLang("Error-Unknown-Player", context.argument(-1))
//                return@execute
//            }
//            when(arg.lowercase()) {
//                "title" -> sender.sendLang("Command-User-Title-Help")
//                "piece" -> sender.sendLang("Command-User-Piece-Help")
//                else -> sender.sendLang("Command-User-Help")
//            }
//        }
//
//        dynamic(commit = "method", optional = true, dynamic = methodDynamic)
//    }
//
//    /**
//     * at user <-2:player> <-1:module> <arg:method>
//     * */
//    private val methodDynamic: CommandBuilder.CommandComponentDynamic.() -> Unit = {
//        suggestion<CommandSender>(uncheck = true) { _, context ->
//            when(context.argument(-1).lowercase()) {
//                "title" -> listOf("clear", "give", "set", "remove")
//                "piece" -> listOf("give", "set", "take")
//                else -> listOf()
//            }
//        }
//        execute<CommandSender> { sender, context, arg ->
//            LycorisAPI.getOfflinePlayer(context.argument(-2))?:let {
//                sender.sendLang("Error-Unknown-Player", context.argument(-2))
//                return@execute
//            }
//            when(context.argument(-1).lowercase()) {
//                "title" -> {
//                    when(arg.lowercase()) {
//                        "clear" -> {
//                            LycorisAPI.getOfflinePlayer(context.argument(-2))?.let {
//                                LycorisAPI.setTitlePlayerUse(it, TitleData.getDefaultTitle().getId())
//                                sender.sendLang("Command-User-Title-Clear-Success", context.argument(-2))
//                                return@execute
//                            }
//                            sender.sendLang("Error-Unknown-Player", context.argument(-2))
//                            return@execute
//                        }
//                        "give" -> sender.sendLang("Command-User-Title-Give-Help")
//                        "set" -> sender.sendLang("Command-User-Title-Set-Help")
//                        "remove" -> sender.sendLang("Command-User-Title-Remove-Help")
//                        else -> sender.sendLang("Command-User-Title-Help")
//                    }
//                }
//                "piece" -> {
//                    when(arg.lowercase()) {
//                        "give" -> sender.sendLang("Command-User-Piece-Give-Help")
//                        "set" -> sender.sendLang("Command-User-Piece-Set-Help")
//                        "take" -> sender.sendLang("Command-User-Piece-Take-Help")
//                        else -> sender.sendLang("Command-User-Piece-Help")
//                    }
//                }
//                else -> sender.sendLang("Command-User-Help")
//            }
//        }
//
//        dynamic(commit = "arg", optional = true, dynamic = argDynamic)
//    }
//
//    /**
//     * at user <-3:player> <-2:module> <-1:method> <arg:arg>
//     * */
//    private val argDynamic: CommandBuilder.CommandComponentDynamic.() -> Unit = {
//        suggestion<CommandSender>(uncheck = true) { _, context ->
//            when(context.argument(-2).lowercase()) {
//                "title" -> {
//                    when(context.argument(-1).lowercase()) {
//                        "give" -> {
//                            LycorisAPI.getPlayerTitleList(context.argument(-3)).let { list ->
//                                return@let mutableListOf<String>().apply {
//                                    LycorisAPI.getTitles().forEach {
//                                        if (!list.contains(it.key)) this.add(it.key)
//                                    }
//                                }
//                            }
//                        }
//                        "set" -> {
//                            mutableListOf<String>().apply {
//                                add(LycorisAPI.getDefaultTitle().getId())
//                                addAll(LycorisAPI.getPlayerTitleList(context.argument(-3)))
//                            }
//                        }
//                        "remove" -> {
//                            LycorisAPI.getPlayerTitleList(context.argument(-3))
//                        }
//                        else -> listOf()
//                    }
//                }
//                "piece" -> listOf("1", "10", "100")
//                else -> listOf()
//            }
//        }
//        execute<CommandSender> { sender, context, arg ->
//            val player = LycorisAPI.getOfflinePlayer(context.argument(-3))?:let {
//                sender.sendLang("Error-Unknown-Player", arg)
//                return@execute
//            }
//            when (context.argument(-2).lowercase()) {
//                "title" -> {
//                    if (context.argument(-1).equals("clear", true)) {
//                        sender.sendLang("Command-User-Title-Clear-Help")
//                        return@execute
//                    }
//                    if (!LycorisAPI.isTitle(arg)) {
//                        sender.sendLang("Error-Unknown-Title", arg)
//                        return@execute
//                    }
//                    when(context.argument(-1).lowercase()) {
//                        "give" -> {
//                            if(arg == LycorisAPI.getDefaultTitle().getId()) {
//                                sender.sendLang("Command-User-Title-Give-Cannot-Give-Default")
//                                return@execute
//                            }
//                            if(UserData.getPlayerTitleList(player).contains(arg)) {
//                                sender.sendLang("Command-User-Title-Give-Already-Owned", context.argument(-3), arg)
//                                return@execute
//                            }
//                            LycorisAPI.addPlayerTitle(player, arg)
//                            sender.sendLang("Command-User-Title-Give-Success", context.argument(-3), arg)
//                        }
//                        "set" -> {
//                            if(!LycorisAPI.playerHasTitle(player, arg)) {
//                                sender.sendLang("Command-User-Title-Set-Not-Owned", context.argument(-3), arg)
//                                return@execute
//                            }
//                            LycorisAPI.setTitlePlayerUse(player, arg)
//                            sender.sendLang("Command-User-Title-Set-Success", context.argument(-3), arg)
//                        }
//                        "remove" -> {
//                            if (arg == LycorisAPI.getDefaultTitle().getId()) {
//                                sender.sendLang("Command-User-Title-Remove-Cannot-Remove-Default")
//                                return@execute
//                            }
//                            if(!LycorisAPI.playerHasTitle(player, arg)) {
//                                sender.sendLang("Command-User-Title-Remove-Not-Owned", context.argument(-3), arg)
//                                return@execute
//                            }
//                            LycorisAPI.removePlayerTitle(player, arg)
//                            sender.sendLang("Command-User-Title-Remove-Success", context.argument(-3), arg)
//                        }
//                    }
//                }
//                "piece" -> {
//                    if (!Regex("^[1-9]\\d*$").matches(arg)) {
//                        sender.sendLang("Error-Unknown-Title", arg)
//                        return@execute
//                    }
//                    when(context.argument(-1).lowercase()) {
//                        "give" -> {
//                            UserData.givePlayerTitlePiece(player, arg.toInt())
//                            sender.sendLang("Command-User-Piece-Give-Success", context.argument(-3), arg)
//                        }
//                        "set" -> {
//                            UserData.setPlayerTitlePiece(player, arg.toInt())
//                            sender.sendLang("Command-User-Piece-Set-Success", context.argument(-3), arg)
//                        }
//                        "take" -> {
//                            UserData.takePlayerTitlePiece(player, arg.toInt())
//                            sender.sendLang("Command-User-Piece-Take-Success", context.argument(-3), arg)
//                        }
//                    }
//                }
//            }
//        }
//    }

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