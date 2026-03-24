package roland_a.mc_mods.no_xp

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import roland_a.mc_mods.common.config.loadConfig
import roland_a.mc_mods.common.config.parseWithType
import roland_a.mc_mods.common.config.withDefaultConfigLocation
import roland_a.mc_mods.common.config.withLogger
import roland_a.mc_mods.no_xp.config.Config

object Mod: ModInitializer {
	val MOD_ID: String = Mod::class.java.packageName.split(".").last()

	val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

	val config: Config by lazy {
		loadConfig {
			withDefaultConfigLocation(MOD_ID)

			withLogger(LOGGER)

			parseWithType<Config>()
		}
	}

	override fun onInitialize() {
		//This makes sure the config is always initialized by this time
		config
	}
}
