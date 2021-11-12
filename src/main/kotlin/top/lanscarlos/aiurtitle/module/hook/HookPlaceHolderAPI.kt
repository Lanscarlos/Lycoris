package top.lanscarlos.aiurtitle.module.hook

import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion
import top.lanscarlos.aiurtitle.api.AiurTitleAPI

object HookPlaceHolderAPI : PlaceholderExpansion {
    override val identifier = "aiurtitle"

    override fun onPlaceholderRequest(player: Player, args: String): String {
        val params = args.split("_")
        if (params.isEmpty()) {
            return "§cAiurTitle-Unknown-Params"
        }
        return when(params[0].lowercase()) {
            "use" -> AiurTitleAPI.getTitlePlayerUse(player).getDisplay()
            "count" -> AiurTitleAPI.getPlayerTitleList(player).size.toString()
            "piece" -> AiurTitleAPI.getPlayerTitlePiece(player).toString()
            else -> "§cAiurTitle-Unknown-Params"
        }
    }
}