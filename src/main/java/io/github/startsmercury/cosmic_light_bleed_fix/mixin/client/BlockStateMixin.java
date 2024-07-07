package io.github.startsmercury.cosmic_light_bleed_fix.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import finalforeach.cosmicreach.blocks.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(BlockState.class)
public class BlockStateMixin {
    /**
     * Triples sky-light level array allocation size. Each vertex needs separate
     * lighting data coming from three axes.
     *
     * @param original the original allocation size (usually eight)
     * @return the new allocation size
     */
    @ModifyExpressionValue(
        method = "addVertices(Lfinalforeach/cosmicreach/rendering/IMeshData;III)V",
        at = {
            @At(value = "CONSTANT", args = "intValue=8", ordinal = 0),
            @At(value = "CONSTANT", args = "intValue=8", ordinal = 1)
        }
    )
    private int expandLightLevelArrays(int original) {
        return original * 3;
    }
}
