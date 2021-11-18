package top.lanscarlos.lycoris.module.action

import taboolib.common.platform.function.info
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionEmpty(val collection: ParsedAction<*>): ScriptAction<Boolean>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
        return frame.newFrame(collection).run<Any>().thenApply {
            it !is Collection<*> || it.isEmpty()
        }
    }

    companion object {
        @KetherParser(["empty"], namespace = "lycoris", shared = true)
        fun parser() = scriptParser {
            ActionEmpty(it.next(ArgTypes.ACTION))
        }
    }
}