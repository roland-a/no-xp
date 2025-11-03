package roland_a.no_xp.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(AnvilMenu.class)
class AnvilMenuMixin {
    @Inject(method = "mayPickup", at = @At("RETURN"), cancellable = true)
    void canAlwaysPickUp(Player player, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}

@Mixin(AnvilScreen.class)
class AnvilScreenMixin {
    @Redirect(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"))
    void removeXpRequirementBackground(GuiGraphics instance, int i, int j, int k, int l, int m) {
    }

    @Redirect(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"))
    int removeXPRequirementText(GuiGraphics instance, Font font, Component component, int i, int j, int k) {
        return 0;
    }
}

@Mixin(ExperienceOrb.class)
abstract class ExperienceOrbMixin extends Entity {
    ExperienceOrbMixin(EntityType<?> entityType, Level level) {super(entityType, level);}

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    void removeFromExistence(CallbackInfo ci) {
        this.discard();

        ci.cancel();
    }
}

@Mixin(Gui.class)
abstract class GuiMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void renderHeart(
        GuiGraphics guiGraphics,
        Gui.HeartType heartType,
        int i,
        int j,
        boolean bl,
        boolean bl2,
        boolean bl3
    );

    @Redirect(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
    void removeXpBarProgress(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l, int m, int n, int o, int p) {}

    @Redirect(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    void removeXPBarBackground(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l) {}

    @ModifyArgs(method = "renderExperienceLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"))
    void removeXpBarLevels(Args args) {
        args.set(1, "");
    }

    @Redirect(method = "renderHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIZZZ)V"))
    void moveHealthBarDown(Gui instance, GuiGraphics guiGraphics, Gui.HeartType heartType, int i, int j, boolean bl, boolean bl2, boolean bl3) {
        if (this.minecraft.player != null && this.minecraft.player.jumpableVehicle() != null) {
            this.renderHeart(guiGraphics, heartType, i, j, bl, bl2, bl3);
            return;
        }

        this.renderHeart(guiGraphics, heartType, i, j + 7, bl, bl2, bl3);
    }

    @Redirect(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    void moveHungerBarDown(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        if (this.minecraft.player != null && this.minecraft.player.jumpableVehicle() != null) {
            instance.blitSprite(resourceLocation, i, j, k, l);
            return;
        }

        instance.blitSprite(resourceLocation, i, j+7, k, l);
    }

    @Redirect(method = "renderVehicleHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    void moveRiddenHealthBarDown(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        if (this.minecraft.player != null && this.minecraft.player.jumpableVehicle() != null) {
            instance.blitSprite(resourceLocation, i, j, k, l);
            return;
        }

        instance.blitSprite(resourceLocation, i, j+7, k, l);
    }

    @Redirect(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    void moveAirBarDown(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        if (this.minecraft.player != null && this.minecraft.player.jumpableVehicle() != null) {
            instance.blitSprite(resourceLocation, i, j, k, l);
            return;
        }

        instance.blitSprite(resourceLocation, i, j+7, k, l);
    }

    @Redirect(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private static void moveArmorBarDown(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.jumpableVehicle() != null) {
            instance.blitSprite(resourceLocation, i, j, k, l);
            return;
        }

        instance.blitSprite(resourceLocation, i, j+7, k, l);
    }
}
