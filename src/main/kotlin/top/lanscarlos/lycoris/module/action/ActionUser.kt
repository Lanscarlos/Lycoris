package top.lanscarlos.lycoris.module.action

import org.bukkit.entity.Player
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import top.lanscarlos.lycoris.api.LycorisAPI
import top.lanscarlos.lycoris.module.data.getUser
import java.util.concurrent.CompletableFuture

class ActionUser {

    class ActionUserTitleAdd(val id: ParsedAction<*>): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(id).run<String>().thenAccept {
                frame.getPlayer().getUser().addTitle(it)
            }
        }
    }

    class ActionUserTitleSet(val id: ParsedAction<*>): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(id).run<String>().thenAccept {
                frame.getPlayer().getUser().setUse(it)
            }
        }
    }

    class ActionUserTitleRemove(val id: ParsedAction<*>): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(id).run<String>().thenAccept {
                frame.getPlayer().getUser().setUse(it)
            }
        }
    }

    class ActionUserTitleUse(): ScriptAction<String>() {
        override fun run(frame: ScriptFrame): CompletableFuture<String> {
            return CompletableFuture.completedFuture(frame.getPlayer().getUser().getUse())
        }
    }

    class ActionUserTitleCheckActive(val id: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(id).run<String>().thenApply {
                frame.getPlayer().getUser().isTitleActive(it)
            }
        }
    }

    class ActionUserTitleCheckExpired(val id: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(id).run<String>().thenApply {
                frame.getPlayer().getUser().isTitleExpired(it)
            }
        }
    }

    class ActionUserTitleCheckPermanent(val id: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(id).run<String>().thenApply {
                frame.getPlayer().getUser().isTitlePermanent(it)
            }
        }
    }

    companion object {
        @KetherParser(["user"], namespace = "lycoris", shared = true)
        fun parser() = scriptParser {
            when(it.expects(
                "add", "set", "rm", "remove", "use",
                "active", "expired", "permanent"
            )) {
                "add" -> ActionUserTitleAdd(it.next(ArgTypes.ACTION))
                "set" -> ActionUserTitleSet(it.next(ArgTypes.ACTION))
                "remove", "rm" -> ActionUserTitleRemove(it.next(ArgTypes.ACTION))
                "use" -> ActionUserTitleUse()
                "active" -> ActionUserTitleCheckActive(it.next(ArgTypes.ACTION))
                "expired" -> ActionUserTitleCheckExpired(it.next(ArgTypes.ACTION))
                "permanent" -> ActionUserTitleCheckPermanent(it.next(ArgTypes.ACTION))
                else -> error("out of case")
            }
        }

        fun ScriptFrame.getPlayer(): Player {
            return script().sender?.castSafely<Player>() ?: error("No player selected.")
        }
    }

}