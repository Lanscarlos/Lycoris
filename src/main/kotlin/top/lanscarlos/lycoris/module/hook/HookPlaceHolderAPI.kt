package top.lanscarlos.lycoris.module.hook

import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion
import top.lanscarlos.lycoris.api.LycorisAPI
import top.lanscarlos.lycoris.module.data.getUser

object HookPlaceHolderAPI : PlaceholderExpansion {
    override val identifier = "lycoris"

    override fun onPlaceholderRequest(player: Player, args: String): String {
        val params = args.split("_")
        if (params.isEmpty()) {
            return "§cAiurTitle-Unknown-Params"
        }
        return when(params[0].lowercase()) {
            "use" -> player.getUser().getTitle().getTitleDisplay()
            "available" -> player.getUser().getAvailableTitles().size.toString()
            "count" -> player.getUser().getTitles().size.toString()
            "piece" -> player.getUser().getPiece().toString()
            else -> "§cAiurTitle-Unknown-Params"
        }
    }
}