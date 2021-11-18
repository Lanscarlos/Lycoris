package top.lanscarlos.lycoris.module.action

import org.bukkit.entity.Player
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionInventory {

    class ActionInventoryClose: ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            (frame.script().sender?.castSafely<Player>() ?: error("No player selected.")).closeInventory()
            return CompletableFuture.completedFuture(null)
        }
    }

    companion object {
        @KetherParser(["inv"], namespace = "lycoris", shared = true)
        fun parser() = scriptParser {
            when(it.expects("close")) {
                "close" -> ActionInventoryClose()
                else -> error("out of case")
            }
        }
    }

}