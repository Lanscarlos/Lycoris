package top.lanscarlos.lycoris.module.action

import org.bukkit.entity.Player
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import top.lanscarlos.lycoris.api.LycorisAPI
import top.lanscarlos.lycoris.module.data.getUser
import java.util.concurrent.CompletableFuture

class ActionTitle {

    class ActionUserTitleSet(val id: ParsedAction<*>): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(id).run<String>().thenAccept {
                frame.getPlayer().getUser().setUse(it)
            }
        }
    }

    companion object {
        @KetherParser(["aiurtitletuser", "atuser"], namespace = "aiurtitle", shared = true)
        fun parser() = scriptParser {
            when(it.expects("add", "set", "remove")) {
                "set" -> ActionUserTitleSet(it.next(ArgTypes.ACTION))
                else -> error("out of case")
            }
        }

        fun ScriptFrame.getPlayer(): Player {
            return script().sender?.castSafely<Player>() ?: error("No player selected.")
        }
    }

}