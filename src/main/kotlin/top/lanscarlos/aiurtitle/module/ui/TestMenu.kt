package top.lanscarlos.aiurtitle.module.ui

import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.buildMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem

object TestMenu {


//    val playerDataMenu = buildMenu<Linked<PlayerData>> {
//        elements {
//            这里传入一个方法来返回一个PlayerData的列表
//        }
//        onGenerate { _, element, _, _ ->
//            buildItem(XMaterial.PAPER) {
//                name = "&f${element.name}"
//                lore.addAll(
//                    arrayListOf(
//                        " ",
//                        "&8| &7玩家名: &b${element.name}",
//                        "&8| &7玩家等级: &b${element.level.toString()}",
//                        " "
//                    )
//                )
//                colored()
//            }
//        }
//        setNextPage(51) { _, hasNextPage ->
//            if (hasNextPage) {
//                buildItem(XMaterial.SPECTRAL_ARROW) {
//                    name = "&7下一页"
//                    colored()
//                }
//            } else {
//                buildItem(XMaterial.ARROW) {
//                    name = "&8下一页"
//                    colored()
//                }
//            }
//        }
//        setPreviousPage(47) { _, hasPreviousPage ->
//            if (hasPreviousPage) {
//                buildItem(XMaterial.SPECTRAL_ARROW) {
//                    name = "&7上一页"
//                    colored()
//                }
//            } else {
//                buildItem(XMaterial.ARROW) {
//                    name = "&8上一页"
//                    colored()
//                }
//            }
//        }
//    }


    val menu = buildMenu<Basic>("") {
        rows(6)
        map(
            "#########",
            " A",
            "#########"
        )
        set('#', buildItem(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE) {
            name = "§r"
        })
        set('A', buildItem(XMaterial.NAME_TAG) {
            name = "§a默认称号"
            lore += "&c啊哈哈哈哈"
            lore += "§e额鹅鹅鹅"
        })
        onClick('#')
        onClick('A')
    }

    fun openMenu(player: Player) {
        player.openInventory(menu)
    }

}