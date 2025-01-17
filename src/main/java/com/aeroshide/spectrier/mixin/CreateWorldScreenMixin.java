package com.aeroshide.spectrier.mixin;

import com.aeroshide.spectrier.AutoReset;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow
    public boolean hardcore;
    @Shadow
    private TextFieldWidget levelNameField;
    @Shadow
    private Difficulty currentDifficulty;

    @Shadow
    protected abstract void createLevel();

    @Inject(method = "init", at = @At("TAIL"))
    private void autoStartMixin(CallbackInfo info) {
        // If auto reset mode is on, set difficulty to easy and instantly create world.
        if (AutoReset.isPlaying) {
            Difficulty difficulty = null;

            switch (AutoReset.difficulty) {
                case 0:
                    difficulty = Difficulty.PEACEFUL;
                    break;
                case 1:
                    difficulty = Difficulty.EASY;
                    break;
                case 2:
                    difficulty = Difficulty.NORMAL;
                    break;
                case 3:
                    difficulty = Difficulty.HARD;
                    break;
                case 4:
                    difficulty = Difficulty.HARD;
                    hardcore = true;
                    break;
            }

            assert difficulty != null;
            currentDifficulty = difficulty;
            levelNameField.setText("Speedrun #" + AutoReset.getNextAttempt());
            createLevel();
        }
    }
}
