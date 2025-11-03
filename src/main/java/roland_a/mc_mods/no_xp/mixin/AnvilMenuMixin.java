package roland_a.mc_mods.no_xp.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilMenu.class)
class AnvilMenuMixin {
	@Inject(method = "mayPickup", at = @At("RETURN"), cancellable = true)
	void canAlwaysPickUp(Player player, boolean bl, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}
}
