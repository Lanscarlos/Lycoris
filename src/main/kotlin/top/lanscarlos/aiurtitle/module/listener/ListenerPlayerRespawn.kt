package top.lanscarlos.aiurtitle.module.listener

import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.common.platform.event.SubscribeEvent

object ListenerPlayerRespawn {

    @SubscribeEvent
    fun onPlayerRespawn(e : PlayerRespawnEvent) {
        e.player.sendMessage("检测到你重生了")
    }

}