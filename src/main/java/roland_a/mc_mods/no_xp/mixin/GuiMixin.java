package roland_a.mc_mods.no_xp.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Gui.class)
public abstract class GuiMixin {
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
