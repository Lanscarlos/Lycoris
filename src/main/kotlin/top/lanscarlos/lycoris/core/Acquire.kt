package top.lanscarlos.lycoris.core

import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.util.VariableReader
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.util.getStringColored
import taboolib.module.kether.Kether
import taboolib.module.kether.KetherShell
import top.lanscarlos.lycoris.Lycoris

class Acquire(map: Map<*, *>) {

    private val type: String
    private val value: Any
    private val actions: List<String>
    private val deny: List<String>

    init {
        type = map["type"]?.toString() ?: "free"
        value = map["value"] ?: 0

        actions = let {
            return@let when (val value = map["action"]) {
                is String -> listOf(value.toString())
                is List<*> -> value.map { it.toString() }
                else -> listOf()
            }
        }

        deny = let {
            return@let when (val value = map["deny"]) {
                is String -> listOf(value.toString())
                is List<*> -> value.map { it.toString() }
                else -> listOf()
            }
        }
    }

    fun checkCondition(player: Player, vararg values: Map<String, Any>): Boolean {
        return getMechanic(type).checkCondition(player, value, *values)
    }

    fun execute(player: Player, result: Boolean, vararg values: Map<String, Any>) {
        getMechanic(type).execute(player, result, value, *values)

        // 空集合
        if ((if (result) actions else deny).isEmpty()) return

        KetherShell.eval(if (result) actions else deny, sender = adaptPlayer(player), namespace = Kether.scriptRegistry.registeredNamespace.toList(), context = {
            values.forEach {
                it.forEach { (k, v) ->
                    set(k, v)
                }
            }
            set("value", value)
        })
    }

    fun getDisplay(): String {
        return getMechanic(type).getDisplay(value)
    }

    companion object {
        private val acquires = mutableMapOf<String, AcquireMechanic>()

        fun loadAcquires() {
            Lycoris.config.getConfigurationSection("acquire-setting")?.let { section ->
                section.getKeys(false).forEach {
                    acquires[it.lowercase()] = AcquireMechanic(section.getConfigurationSection(it))
                }
            }
        }

        fun getMechanic(id: String):AcquireMechanic {
            return acquires[id.lowercase()] ?: error("没有定义相对应的 $id 类型！")
        }

        /**
         * 真正用来执行操作的类
         * */
        class AcquireMechanic(config: ConfigurationSection) {
            private val condition: List<String>
            private val actions: List<String>
            private val deny: List<String>
            private val format: String

            init {
                format = config.getStringColored("display.format") ?: "未知获取途径"
                condition = when {
                    config.isString("condition") -> listOf(config.getString("condition", "*true"))
                    config.isList("condition") -> config.getStringList("condition")
                    else -> listOf()
                }
                actions = when {
                    config.isString("action") -> listOf(config.getString("action"))
                    config.isList("action") -> config.getStringList("action")
                    else -> listOf()
                }
                deny = when {
                    config.isString("deny") -> listOf(config.getString("deny"))
                    config.isList("deny") -> config.getStringList("deny")
                    else -> listOf()
                }
            }

            fun checkCondition(player: Player, value: Any, vararg values: Map<String, Any>): Boolean {
                if (condition.isEmpty()) return true
                var result: Any? = null
                KetherShell.eval(condition, sender = adaptPlayer(player), namespace = Kether.scriptRegistry.registeredNamespace.toList(), context = {
                    values.forEach {
                        it.forEach { (k, v) ->
                            set(k, v)
                        }
                        set("value", value)
                    }
                }).thenApply { result = it }
                return result?.toString() == "true"
            }

            fun execute(player: Player, result: Boolean, value: Any, vararg values: Map<String, Any>) {
                if ((if (result) actions else deny).isEmpty()) return
                KetherShell.eval(if (result) actions else deny, sender = adaptPlayer(player), namespace = Kether.scriptRegistry.registeredNamespace.toList(), context = {
                    values.forEach {
                        it.forEach { (k, v) ->
                            set(k, v)
                        }
                    }
                    set("value", value.toString())
                })
            }

            fun getDisplay(value: Any): String {
                var display = format
                VariableReader(format, '{', '}', 2).parts.forEach {
                    if (!it.isVariable) return@forEach
                    display = when(it.text) {
                        "value" -> display.replace("{{${it.text}}}", value.toString())
                        else -> display
                    }
                }
                return display
            }
        }
    }
}