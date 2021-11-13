package top.lanscarlos.lycoris.module.hook

import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion
import top.lanscarlos.lycoris.api.LycorisAPI

object HookPlaceHolderAPI : PlaceholderExpansion {
    override val identifier = "aiurtitle"

    override fun onPlaceholderRequest(player: Player, args: String): String {
        val params = args.split("_")
        if (params.isEmpty()) {
            return "§cAiurTitle-Unknown-Params"
        }
        return when(params[0].lowercase()) {
            "use" -> LycorisAPI.getTitlePlayerUse(player).getTitleDisplay()
            "count" -> LycorisAPI.getPlayerTitleList(player).size.toString()
            "piece" -> LycorisAPI.getPlayerTitlePiece(player).toString()
            else -> "§cAiurTitle-Unknown-Params"
        }
    }
}