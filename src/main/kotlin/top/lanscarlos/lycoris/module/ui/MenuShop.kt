package top.lanscarlos.lycoris.module.ui

import org.bukkit.entity.Player
import top.lanscarlos.lycoris.api.LycorisAPI
import top.lanscarlos.lycoris.module.data.TitleData

object MenuShop: Menu() {

    fun init() {
        loadMenu("shop")
    }

    fun openMenu(sender: Player) {
        openMenu(sender, "shop", elements = {
            listOf(LycorisAPI.getDefaultTitle(), *(TitleData.getTitles().filter { !it.value.isHidden() }.map { it.value }).toTypedArray())
        }) { e, element ->
            element.onIconClicked(sender, e.clicker, "shop", e)
        }
    }

}