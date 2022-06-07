package com.aeroshide.spectrier.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.aeroshide.spectrier.AutoReset;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    private static final Identifier GOLD_BOOTS = new Identifier("textures/item/netherite_boots.png");
    private ButtonWidget resetsButton;
    private Text difficultyString;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void initMixin(CallbackInfo info) {
        // If auto reset mode is on, instantly switch to create world menu.
        if (AutoReset.isPlaying) {
            CreateWorldScreen.create(this.client, this);
        } else if (!this.client.isDemo()) {
            // Add new button for starting auto resets.
            int y = this.height / 4 + 48;
            resetsButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 124, y, 20, 20, Text.literal(""), (buttonWidget) -> {
                if (!hasShiftDown()) {
                    AutoReset.isPlaying = true;
                    AutoReset.saveDifficulty();
                    CreateWorldScreen.create(this.client, this);
                } else {
                    AutoReset.difficulty++;
                    if (AutoReset.difficulty > 4) {
                        AutoReset.difficulty = 0;
                    }
                    refreshDifficultyString();
                }
            }));
        }

        refreshDifficultyString();
    }

    private void refreshDifficultyString() {
        switch (AutoReset.difficulty) {
            case 0:
                difficultyString = Text.literal("Peaceful");
                break;
            case 1:
                difficultyString = Text.literal("Easy");
                break;
            case 2:
                difficultyString = Text.literal("Normal");
                break;
            case 3:
                difficultyString = Text.literal("Hard");
                break;
            case 4:
                difficultyString = Text.literal("Hardcore");
                break;
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void goldBootsOverlayMixin(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int y = this.height / 4 + 48;
        RenderSystem.setShaderTexture(0,GOLD_BOOTS);
        drawTexture(matrices, (width / 2) - 122, y + 2, 0.0F, 0.0F, 16, 16, 16, 16);
        if (resetsButton.isHovered() && hasShiftDown()) {
            drawCenteredText(matrices, textRenderer, difficultyString, this.width / 2 - 114, this.height / 4 + 35, 16777215);
        }

    }
}