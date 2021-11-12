package top.lanscarlos.aiurtitle.module.ui

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.util.getStringColored
import taboolib.module.configuration.util.getStringListColored
import taboolib.module.kether.Kether
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import taboolib.module.ui.buildMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import top.lanscarlos.aiurtitle.AiurTitle
import top.lanscarlos.aiurtitle.core.title.Title
import top.lanscarlos.aiurtitle.module.data.TitleData

object MenuShop {

    private var title: String = "§8Title Shop"

    private var size : Int = 6

    private val slots = mutableListOf<Int>()
    private val layout = mutableMapOf<Int, String>()

    val openSound = mutableListOf<String>()

    val closeSound = mutableListOf<String>()

    private val buttons = mutableMapOf<String, Pair<ItemStack, List<String>>>()

    private fun loadLayout() {
        slots.clear()
        layout.clear()
        val regex = Regex("`[A-Za-z0-9_-]+`")
        AiurTitle.menuConfig.getStringList("menu-shop.layout").also {
            size = if (it.size > 0 || it.size <= 6) it.size else size
        }.forEachIndexed loop@{ i, it ->
            if (i >= 6) return@loop
            var line = it
            var index = 0
            regex.findAll(line).forEach { r ->
                layout[r.range.first - index + (i * 9)] = r.value.replace("`", "")
                line = line.replace(r.value, " ")
                index += r.value.length - 1
            }
            line.forEachIndexed line_loop@{ char_index, char ->
                if (char == ' ') return@line_loop
                when(char) {
                    ' ' -> return@line_loop
                    '$' -> slots.add(char_index + i * 9)
                    else -> layout[char_index + i * 9] = char.toString()
                }
            }
        }
    }

    private fun loadButtons() {
        buttons.clear()
        AiurTitle.menuConfig.getConfigurationSection("menu-shop.buttons")?.let { config ->
            config.getKeys(false)?.forEach { key ->
                when (key) {
                    "previous-page", "next-page" -> config.getConfigurationSection("${key}.no-page")?.let {
                        buttons["no-${key}"] = Pair(loadItem(config, "${key}.no-page"), config.getStringList("${key}.no-page.actions"))
                    }
                }
                val item = loadItem(config, key)
                val actions = config.getStringList("${key}.actions")
                buttons[key] = Pair(item, actions)
            }
        }
    }

    fun loadMenu() {
        title = AiurTitle.menuConfig.getStringColored("menu-shop.title") ?: title
        loadLayout()
        loadButtons()
    }

    fun openMenu(player: Player) {
        buildMenu<Linked<Title>>(title) {
            rows(size)
            slots(slots)
            elements {
                TitleData.getTitles().map { it.value }
            }
            layout.forEach { (i, key) ->
                buttons[key]?.let { v ->
                    when (key) {
                        "previous-page" -> setPreviousPage(i) setPreviousPage@{ _, hasPage ->
                            buttons["no-previous-page"]?.let {
                                return@setPreviousPage if (hasPage) v.first else it.first
                            }
                            return@setPreviousPage v.first
                        }
                        "next-page" -> setNextPage(i) setNextPage@{ _, hasPage ->
                            buttons["no-next-page"]?.let {
                                return@setNextPage if (hasPage) v.first else it.first
                            }
                            return@setNextPage v.first
                        }
                        else -> set(i, v.first) {
                            try {
                                v.second.forEach { line ->
                                    KetherShell.eval(line, sender = adaptCommandSender(player), namespace = Kether.scriptRegistry.registeredNamespace.toList())
                                }
                            } catch (ex: Throwable) {
                                ex.printKetherErrorMessage()
                            }
                        }
                    }
                }
            }
            onGenerate { player, element, index, slot ->
                element.getIcon("shop")
            }
            onClick { e, element ->

            }

        }.let {
            player.openInventory(it)
//            adaptPlayer(player).playsound
        }
    }

    private fun loadItem(config: ConfigurationSection, key: String): ItemStack {
        return buildItem(XMaterial.matchXMaterial(config.getString("${key}.display.mat")).get()) {
            config.getStringColored("${key}.display.name")?.let { name = it }
            config.getStringListColored("${key}.display.lore").forEach { lore += it }
        }
    }

}