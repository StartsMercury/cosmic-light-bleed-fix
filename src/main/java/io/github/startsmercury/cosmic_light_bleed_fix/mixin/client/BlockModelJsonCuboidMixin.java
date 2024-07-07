package io.github.startsmercury.cosmic_light_bleed_fix.mixin.client;

import static io.github.startsmercury.cosmic_light_bleed_fix.impl.client.CosmicLightBleedFix.AXIS_X;
import static io.github.startsmercury.cosmic_light_bleed_fix.impl.client.CosmicLightBleedFix.AXIS_Y;
import static io.github.startsmercury.cosmic_light_bleed_fix.impl.client.CosmicLightBleedFix.AXIS_Z;

import finalforeach.cosmicreach.rendering.blockmodels.BlockModelJsonCuboid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(BlockModelJsonCuboid.class)
public abstract class BlockModelJsonCuboidMixin {
    /**
     * Modifies vertex indices to differentiate with the direction it is facing.
     *
     * @param callback the injector callback
     */
    @Inject(
        method = "initialize(Lcom/badlogic/gdx/utils/OrderedMap;Lcom/badlogic/gdx/utils/Array;)V",
        at = @At("RETURN")
    )
    private void modifyVertexIndices(final CallbackInfo callback) {
        for (final var kf : ((BlockModelJsonCuboid) (Object) this).faces) {
            final var offset = switch (kf.key) {
                case "localNegX", "localPosX" -> AXIS_X;
                case "localNegY", "localPosY" -> AXIS_Y;
                case "localNegZ", "localPosZ" -> AXIS_Z;
                default ->
                    throw new RuntimeException("Unexpected face direction: " + kf.key);
            };

            final var f = kf.value;
            f.vertexIndexA = f.vertexIndexA * 3 + offset;
            f.vertexIndexB = f.vertexIndexB * 3 + offset;
            f.vertexIndexC = f.vertexIndexC * 3 + offset;
            f.vertexIndexD = f.vertexIndexD * 3 + offset;
        }
    }
}
