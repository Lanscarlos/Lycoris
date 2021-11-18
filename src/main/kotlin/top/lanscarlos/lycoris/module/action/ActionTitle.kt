package top.lanscarlos.lycoris.module.action

import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import top.lanscarlos.lycoris.api.LycorisAPI
import top.lanscarlos.lycoris.module.data.getUser
import java.util.concurrent.CompletableFuture

class ActionTitle {

    class ActionTitleActive(val id: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(id).run<String>().thenApply {
                if (!LycorisAPI.isTitle(it)) return@thenApply false
                LycorisAPI.getTitle(it).isActive()
            }
        }
    }

    class ActionTitleExpired(val id: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(id).run<String>().thenApply {
                if (!LycorisAPI.isTitle(it)) return@thenApply false
                LycorisAPI.getTitle(it).isExpired()
            }
        }
    }

    class ActionTitlePermanent(val id: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(id).run<String>().thenApply {
                if (!LycorisAPI.isTitle(it)) return@thenApply false
                LycorisAPI.getTitle(it).isPermanent()
            }
        }
    }

    companion object {
        @KetherParser(["ly-title"], namespace = "lycoris", shared = true)
        fun parser() = scriptParser {
            when(it.expects(
                "active", "expired", "permanent"
            )) {
                "active" -> ActionTitleActive(it.next(ArgTypes.ACTION))
                "expired" -> ActionTitleExpired(it.next(ArgTypes.ACTION))
                "permanent" -> ActionTitlePermanent(it.next(ArgTypes.ACTION))
                else -> error("out of case")
            }
        }
    }

}