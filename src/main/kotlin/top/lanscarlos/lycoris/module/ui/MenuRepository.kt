package top.lanscarlos.lycoris.module.ui

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptCommandSender
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.util.getStringColored
import taboolib.module.configuration.util.getStringListColored
import taboolib.module.kether.Kether
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import taboolib.module.ui.buildMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import top.lanscarlos.lycoris.Lycoris
import top.lanscarlos.lycoris.api.LycorisAPI
import top.lanscarlos.lycoris.core.title.Title
import top.lanscarlos.lycoris.module.data.TitleData
import top.lanscarlos.lycoris.module.data.getUser

object MenuRepository: Menu() {

    fun init() {
        loadMenu("repository")
    }

    fun openMenu(sender: Player) {
        openMenu(sender, "repository", elements = {
            listOf(LycorisAPI.getDefaultTitle(), *(sender.getUser().getRepository().map { LycorisAPI.getTitle(it.key) }).toTypedArray())
        }) { e, element ->
            sender.getUser().onIconClicked(sender, e.clicker, "repository", element, e)
        }
    }

}