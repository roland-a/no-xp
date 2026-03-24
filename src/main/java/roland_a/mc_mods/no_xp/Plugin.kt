package roland_a.mc_mods.no_xp

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import roland_a.mc_mods.common.ConditionalMixinLoader
import roland_a.mc_mods.no_xp.mixin.GuiMixin

class Plugin: IMixinConfigPlugin by base{
	companion object{
		private val base = ConditionalMixinLoader(
			classes = mapOf(
				GuiMixin::class to {
					!Mod.config.enableXpBar
				}
			)
		)
	}
}
