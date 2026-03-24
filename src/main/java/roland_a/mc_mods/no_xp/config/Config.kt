package roland_a.mc_mods.no_xp.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
	val enableXpBar: Boolean = false
)
