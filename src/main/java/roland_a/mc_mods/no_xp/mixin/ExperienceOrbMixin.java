package roland_a.mc_mods.no_xp.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
abstract class ExperienceOrbMixin extends Entity {
	ExperienceOrbMixin(EntityType<?> entityType, Level level) {super(entityType, level);}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	void removeFromExistence(CallbackInfo ci) {
		this.discard();

		ci.cancel();
	}
}
