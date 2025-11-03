package roland_a.mc_mods.no_xp

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Mod: ModInitializer {
	val MOD_ID: String = Mod::class.java.packageName.split(".").last()

	val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
	}
}
