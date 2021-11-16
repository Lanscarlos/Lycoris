package top.lanscarlos.lycoris.module.listener

import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.common.platform.event.SubscribeEvent
import top.lanscarlos.lycoris.Lycoris
import top.lanscarlos.lycoris.api.LycorisAPI
import top.lanscarlos.lycoris.module.data.getUser

object ListenerPlayerRespawn {

    @SubscribeEvent
    fun onPlayerRespawn(e : PlayerRespawnEvent) {
        e.player.sendMessage("检测到你重生了")
        e.player.getUser().getTitle().getBuff().forEach {
            e.player.addPotionEffect(it)
        }
    }

    @SubscribeEvent
    fun onPlayerJoin(e : PlayerJoinEvent) {
        e.player.getUser().getTitle().getBuff().forEach {
            e.player.addPotionEffect(it)
        }
    }

    @SubscribeEvent
    fun onPlayerQuit(e : PlayerQuitEvent) {
        e.player.getUser().getTitle().getBuff().forEach {
            e.player.removePotionEffect(it.type)
        }
    }

}