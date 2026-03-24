package roland_a.mc_mods.common

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class ConditionalMixinLoader(
	val packages: Map<Package, ()->Boolean> = mapOf(),
	val classes: Map<KClass<*>, ()->Boolean> = mapOf(),
): IMixinConfigPlugin {
	override fun shouldApplyMixin(targetClassName: String?, mixinClassName: String?): Boolean {
		for ((`package`, condition) in packages){
			val mixinPackageName =
				mixinClassName
				?.split(".")
				?.dropLast(1)
				?.joinToString(".")

			if (`package`.name == mixinPackageName){
				return condition()
			}
		}

		for ((`class`, condition) in classes){
			if (`class`.jvmName == mixinClassName){
				return condition()
			}
		}

		return true
	}

	//region boilerplate
	override fun onLoad(mixinPackage: String?) {}

	override fun getRefMapperConfig(): String? {
		return null
	}

	override fun acceptTargets(
		myTargets: Set<String?>?,
		otherTargets: Set<String?>?,
	) {}

	override fun getMixins(): List<String?>? {
		return null
	}

	override fun preApply(
		targetClassName: String?,
		targetClass: ClassNode?,
		mixinClassName: String?,
		mixinInfo: IMixinInfo?,
	) {}

	override fun postApply(
		targetClassName: String?,
		targetClass: ClassNode?,
		mixinClassName: String?,
		mixinInfo: IMixinInfo?,
	) {}
	//endregion
}
