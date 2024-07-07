package io.github.startsmercury.cosmic_light_bleed_fix.mixin.client;

import static io.github.startsmercury.cosmic_light_bleed_fix.impl.client.CosmicLightBleedFix.*;

import com.badlogic.gdx.utils.Array;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import finalforeach.cosmicreach.rendering.ChunkMeshGroup;
import finalforeach.cosmicreach.rendering.MeshData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(ChunkMeshGroup.class)
public abstract class ChunkMeshGroupMixin {
    /**
     * Triples block light level array allocation size. Each vertex needs separate
     * lighting data coming from three axes.
     *
     * @param original the original allocation size (usually eight)
     * @return the new allocation size
     */
    @ModifyExpressionValue(
        method = "getMeshData(Lfinalforeach/cosmicreach/savelib/ISavedChunk;)Lcom/badlogic/gdx/utils/Array;",
        at = @At(value = "CONSTANT", args = "intValue=8", ordinal = 0)
    )
    private static int expandBlockLightLevelArray(int original) {
        return original * 3;
    }

    /**
     * Triples sky-light level array allocation size. Each vertex needs separate
     * lighting data coming from three axes.
     *
     * @param original the original allocation size (usually eight)
     * @return the new allocation size
     * @implNote This method allocates an extra one to store a bit mask for the
     *     preceding cells if they are opaque or not.
     */
    @ModifyExpressionValue(
        method = "getMeshData(Lfinalforeach/cosmicreach/savelib/ISavedChunk;)Lcom/badlogic/gdx/utils/Array;",
        at = @At(value = "CONSTANT", args = "intValue=8", ordinal = 1)
    )
    private static int expandSkyLightLevelArray(int original) {
        // Adds an extra slot to store `opaqueBitMask`.
        return original * 3 + 1;
    }

    /**
     * Replaces block light level calculations with one that separates light coming
     * from an axis.
     * 
     * @param callback the injector callback
     * @param blockLightLevels the block light levels to modify
     * @param opaqueBitMask the opaque bit mask identifies positions blocking light
     * @param lightNxNyNz the block light level at {@code (-1, -1, -1)}
     * @param lightNxNy0z the block light level at {@code (-1, -1,  0)}
     * @param lightNxNyPz the block light level at {@code (-1, -1, +1)}
     * @param lightNx0yNz the block light level at {@code (-1,  0, -1)}
     * @param lightNx0y0z the block light level at {@code (-1,  0,  0)}
     * @param lightNx0yPz the block light level at {@code (-1,  0, +1)}
     * @param lightNxPyNz the block light level at {@code (-1, +1, -1)}
     * @param lightNxPy0z the block light level at {@code (-1, +1,  0)}
     * @param lightNxPyPz the block light level at {@code (-1, +1, +1)}
     * @param light0xNyNz the block light level at {@code ( 0, -1, -1)}
     * @param light0xNy0z the block light level at {@code ( 0, -1,  0)}
     * @param light0xNyPz the block light level at {@code ( 0, -1, +1)}
     * @param light0x0yNz the block light level at {@code ( 0,  0, -1)}
     * @param light0x0yPz the block light level at {@code ( 0,  0, +1)}
     * @param light0xPyNz the block light level at {@code ( 0, +1, -1)}
     * @param light0xPy0z the block light level at {@code ( 0, +1,  0)}
     * @param light0xPyPz the block light level at {@code ( 0, +1, +1)}
     * @param lightPxNyNz the block light level at {@code (+1, -1, -1)}
     * @param lightPxNy0z the block light level at {@code (+1, -1,  0)}
     * @param lightPxNyPz the block light level at {@code (+1, -1, +1)}
     * @param lightPx0yNz the block light level at {@code (+1,  0, -1)}
     * @param lightPx0y0z the block light level at {@code (+1,  0,  0)}
     * @param lightPx0yPz the block light level at {@code (+1,  0, +1)}
     * @param lightPxPyNz the block light level at {@code (+1, +1, -1)}
     * @param lightPxPy0z the block light level at {@code (+1, +1,  0)}
     * @param lightPxPyPz the block light level at {@code (+1, +1, +1)}
     */
    @Inject(
        method = "calculateBlockLightLevels(Lfinalforeach/cosmicreach/world/Chunk;[SZIIII)[S",
        at = @At(value = "CONSTANT", ordinal = 1, args = "intValue=0"),
        cancellable = true
    )
    private static void overwriteBlockLightCalculation(
        final CallbackInfoReturnable<short[]> callback,
        final @Local(ordinal = 0, argsOnly = true) short[] blockLightLevels,
        final @Local(ordinal = 0, argsOnly = true) int opaqueBitMask,
        final @Local(ordinal = 7) int lightNxNyNz,
        final @Local(ordinal = 8) int lightNxNy0z,
        final @Local(ordinal = 9) int lightNxNyPz,
        final @Local(ordinal = 10) int lightNx0yNz,
        final @Local(ordinal = 11) int lightNx0y0z,
        final @Local(ordinal = 12) int lightNx0yPz,
        final @Local(ordinal = 13) int lightNxPyNz,
        final @Local(ordinal = 14) int lightNxPy0z,
        final @Local(ordinal = 15) int lightNxPyPz,
        final @Local(ordinal = 16) int light0xNyNz,
        final @Local(ordinal = 17) int light0xNy0z,
        final @Local(ordinal = 18) int light0xNyPz,
        final @Local(ordinal = 19) int light0x0yNz,
        final @Local(ordinal = 20) int light0x0yPz,
        final @Local(ordinal = 21) int light0xPyNz,
        final @Local(ordinal = 22) int light0xPy0z,
        final @Local(ordinal = 23) int light0xPyPz,
        final @Local(ordinal = 24) int lightPxNyNz,
        final @Local(ordinal = 25) int lightPxNy0z,
        final @Local(ordinal = 26) int lightPxNyPz,
        final @Local(ordinal = 27) int lightPx0yNz,
        final @Local(ordinal = 28) int lightPx0y0z,
        final @Local(ordinal = 29) int lightPx0yPz,
        final @Local(ordinal = 30) int lightPxPyNz,
        final @Local(ordinal = 31) int lightPxPy0z,
        final @Local(ordinal = 32) int lightPxPyPz
    ) {
        // Order: negative-positive x, y, and z then binary count with digits
        //        negative-zero-positive in the placement orders z, y, and x.
//      final var diaphanousNxNyNz = 0 == (diaphanousBitMask & 1 << 6);
        final var diaphanousNxNy0z = 0 == (opaqueBitMask & 1 << 7);
//      final var diaphanousNxNyPz = 0 == (diaphanousBitMask & 1 << 8);
        final var diaphanousNx0yNz = 0 == (opaqueBitMask & 1 << 9);
        final var diaphanousNx0y0z = 0 == (opaqueBitMask & 1);
        final var diaphanousNx0yPz = 0 == (opaqueBitMask & 1 << 10);
//      final var diaphanousNxPyNz = 0 == (diaphanousBitMask & 1 << 11);
        final var diaphanousNxPy0z = 0 == (opaqueBitMask & 1 << 12);
//      final var diaphanousNxPyPz = 0 == (diaphanousBitMask & 1 << 13);
        final var diaphanous0xNyNz = 0 == (opaqueBitMask & 1 << 14);
        final var diaphanous0xNy0z = 0 == (opaqueBitMask & 1 << 2);
        final var diaphanous0xNyPz = 0 == (opaqueBitMask & 1 << 15);
        final var diaphanous0x0yNz = 0 == (opaqueBitMask & 1 << 4);
        final var diaphanous0x0yPz = 0 == (opaqueBitMask & 1 << 5);
        final var diaphanous0xPyNz = 0 == (opaqueBitMask & 1 << 16);
        final var diaphanous0xPy0z = 0 == (opaqueBitMask & 1 << 3);
        final var diaphanous0xPyPz = 0 == (opaqueBitMask & 1 << 17);
//      final var diaphanousPxNyNz = 0 == (diaphanousBitMask & 1 << 18);
        final var diaphanousPxNy0z = 0 == (opaqueBitMask & 1 << 19);
//      final var diaphanousPxNyPz = 0 == (diaphanousBitMask & 1 << 20);
        final var diaphanousPx0yNz = 0 == (opaqueBitMask & 1 << 21);
        final var diaphanousPx0y0z = 0 == (opaqueBitMask & 1 << 1);
        final var diaphanousPx0yPz = 0 == (opaqueBitMask & 1 << 22);
//      final var diaphanousPxPyNz = 0 == (diaphanousBitMask & 1 << 23);
        final var diaphanousPxPy0z = 0 == (opaqueBitMask & 1 << 24);
//      final var diaphanousPxPyPz = 0 == (diaphanousBitMask & 1 << 25);

        updateBlockLightLevelForCorner(blockLightLevels, CORNER_NxNyNz, diaphanous0x0yNz, diaphanous0xNy0z, diaphanous0xNyNz, diaphanousNx0y0z, diaphanousNx0yNz, diaphanousNxNy0z, light0x0yNz, light0xNy0z, light0xNyNz, lightNx0y0z, lightNx0yNz, lightNxNy0z, lightNxNyNz);
        updateBlockLightLevelForCorner(blockLightLevels, CORNER_NxNyPz, diaphanous0x0yPz, diaphanous0xNy0z, diaphanous0xNyPz, diaphanousNx0y0z, diaphanousNx0yPz, diaphanousNxNy0z, light0x0yPz, light0xNy0z, light0xNyPz, lightNx0y0z, lightNx0yPz, lightNxNy0z, lightNxNyPz);
        updateBlockLightLevelForCorner(blockLightLevels, CORNER_NxPyNz, diaphanous0x0yNz, diaphanous0xPy0z, diaphanous0xPyNz, diaphanousNx0y0z, diaphanousNx0yNz, diaphanousNxPy0z, light0x0yNz, light0xPy0z, light0xPyNz, lightNx0y0z, lightNx0yNz, lightNxPy0z, lightNxPyNz);
        updateBlockLightLevelForCorner(blockLightLevels, CORNER_NxPyPz, diaphanous0x0yPz, diaphanous0xPy0z, diaphanous0xPyPz, diaphanousNx0y0z, diaphanousNx0yPz, diaphanousNxPy0z, light0x0yPz, light0xPy0z, light0xPyPz, lightNx0y0z, lightNx0yPz, lightNxPy0z, lightNxPyPz);
        updateBlockLightLevelForCorner(blockLightLevels, CORNER_PxNyNz, diaphanous0x0yNz, diaphanous0xNy0z, diaphanous0xNyNz, diaphanousPx0y0z, diaphanousPx0yNz, diaphanousPxNy0z, light0x0yNz, light0xNy0z, light0xNyNz, lightPx0y0z, lightPx0yNz, lightPxNy0z, lightPxNyNz);
        updateBlockLightLevelForCorner(blockLightLevels, CORNER_PxNyPz, diaphanous0x0yPz, diaphanous0xNy0z, diaphanous0xNyPz, diaphanousPx0y0z, diaphanousPx0yPz, diaphanousPxNy0z, light0x0yPz, light0xNy0z, light0xNyPz, lightPx0y0z, lightPx0yPz, lightPxNy0z, lightPxNyPz);
        updateBlockLightLevelForCorner(blockLightLevels, CORNER_PxPyNz, diaphanous0x0yNz, diaphanous0xPy0z, diaphanous0xPyNz, diaphanousPx0y0z, diaphanousPx0yNz, diaphanousPxPy0z, light0x0yNz, light0xPy0z, light0xPyNz, lightPx0y0z, lightPx0yNz, lightPxPy0z, lightPxPyNz);
        updateBlockLightLevelForCorner(blockLightLevels, CORNER_PxPyPz, diaphanous0x0yPz, diaphanous0xPy0z, diaphanous0xPyPz, diaphanousPx0y0z, diaphanousPx0yPz, diaphanousPxPy0z, light0x0yPz, light0xPy0z, light0xPyPz, lightPx0y0z, lightPx0yPz, lightPxPy0z, lightPxPyPz);

        callback.setReturnValue(blockLightLevels);
        callback.cancel();
    }

    /**
     * Adds `opaqueBitMask` to sky-light levels array's end.
     *
     * @param callback the injector callback
     * @param skyLightLevels the sky-light levels array to modify
     * @param opaqueBitMask the opaque bit mask
     * @implNote This method depends on the adjusted implementation of
     *     {@link #expandSkyLightLevelArray}.
     */
    @Inject(method = "getMeshData", at = @At(value = "INVOKE", target = """
        Lfinalforeach/cosmicreach/rendering/ChunkMeshGroup;\
        calculateSkyLightLevels(Lfinalforeach/cosmicreach/world/Chunk;[IIII)[I\
    """))
    private static void passOpaqueBitMask(
        final CallbackInfoReturnable<Array<MeshData>> callback,
        final @Local(ordinal = 0) int[] skyLightLevels,
        final @Local(ordinal = 12) int opaqueBitMask
    ) {
        skyLightLevels[24] = opaqueBitMask;
    }

    @Inject(
        method = "calculateSkyLightLevels",
        at = @At(value = "CONSTANT", ordinal = 1, args = "intValue=0"),
        cancellable = true
    )
    private static void overwriteSkyLightCalculation(
        final CallbackInfoReturnable<int[]> callback,
        final @Local(ordinal = 0, argsOnly = true) int[] skyLightLevels,
        final @Local(ordinal = 7) int lightNxNyNz,
        final @Local(ordinal = 8) int lightNxNy0z,
        final @Local(ordinal = 9) int lightNxNyPz,
        final @Local(ordinal = 10) int lightNx0yNz,
        final @Local(ordinal = 11) int lightNx0y0z,
        final @Local(ordinal = 12) int lightNx0yPz,
        final @Local(ordinal = 13) int lightNxPyNz,
        final @Local(ordinal = 14) int lightNxPy0z,
        final @Local(ordinal = 15) int lightNxPyPz,
        final @Local(ordinal = 16) int light0xNyNz,
        final @Local(ordinal = 17) int light0xNy0z,
        final @Local(ordinal = 18) int light0xNyPz,
        final @Local(ordinal = 19) int light0x0yNz,
        final @Local(ordinal = 20) int light0x0yPz,
        final @Local(ordinal = 21) int light0xPyNz,
        final @Local(ordinal = 22) int light0xPy0z,
        final @Local(ordinal = 23) int light0xPyPz,
        final @Local(ordinal = 24) int lightPxNyNz,
        final @Local(ordinal = 25) int lightPxNy0z,
        final @Local(ordinal = 26) int lightPxNyPz,
        final @Local(ordinal = 27) int lightPx0yNz,
        final @Local(ordinal = 28) int lightPx0y0z,
        final @Local(ordinal = 29) int lightPx0yPz,
        final @Local(ordinal = 30) int lightPxPyNz,
        final @Local(ordinal = 31) int lightPxPy0z,
        final @Local(ordinal = 32) int lightPxPyPz
    ) {
        final var diaphanousBitMask = skyLightLevels[24];

//      final var diaphanousNxNyNz = 0 == (diaphanousBitMask & 1 << 6);
        final var diaphanousNxNy0z = 0 == (diaphanousBitMask & 1 << 7);
//      final var diaphanousNxNyPz = 0 == (diaphanousBitMask & 1 << 8);
        final var diaphanousNx0yNz = 0 == (diaphanousBitMask & 1 << 9);
        final var diaphanousNx0y0z = 0 == (diaphanousBitMask & 1);
        final var diaphanousNx0yPz = 0 == (diaphanousBitMask & 1 << 10);
//      final var diaphanousNxPyNz = 0 == (diaphanousBitMask & 1 << 11);
        final var diaphanousNxPy0z = 0 == (diaphanousBitMask & 1 << 12);
//      final var diaphanousNxPyPz = 0 == (diaphanousBitMask & 1 << 13);
        final var diaphanous0xNyNz = 0 == (diaphanousBitMask & 1 << 14);
        final var diaphanous0xNy0z = 0 == (diaphanousBitMask & 1 << 2);
        final var diaphanous0xNyPz = 0 == (diaphanousBitMask & 1 << 15);
        final var diaphanous0x0yNz = 0 == (diaphanousBitMask & 1 << 4);
        final var diaphanous0x0yPz = 0 == (diaphanousBitMask & 1 << 5);
        final var diaphanous0xPyNz = 0 == (diaphanousBitMask & 1 << 16);
        final var diaphanous0xPy0z = 0 == (diaphanousBitMask & 1 << 3);
        final var diaphanous0xPyPz = 0 == (diaphanousBitMask & 1 << 17);
//      final var diaphanousPxNyNz = 0 == (diaphanousBitMask & 1 << 18);
        final var diaphanousPxNy0z = 0 == (diaphanousBitMask & 1 << 19);
//      final var diaphanousPxNyPz = 0 == (diaphanousBitMask & 1 << 20);
        final var diaphanousPx0yNz = 0 == (diaphanousBitMask & 1 << 21);
        final var diaphanousPx0y0z = 0 == (diaphanousBitMask & 1 << 1);
        final var diaphanousPx0yPz = 0 == (diaphanousBitMask & 1 << 22);
//      final var diaphanousPxPyNz = 0 == (diaphanousBitMask & 1 << 23);
        final var diaphanousPxPy0z = 0 == (diaphanousBitMask & 1 << 24);
//      final var diaphanousPxPyPz = 0 == (diaphanousBitMask & 1 << 25);

        updateSkyLightLevelForCorner(skyLightLevels, CORNER_NxNyNz, diaphanous0x0yNz, diaphanous0xNy0z, diaphanous0xNyNz, diaphanousNx0y0z, diaphanousNx0yNz, diaphanousNxNy0z, light0x0yNz, light0xNy0z, light0xNyNz, lightNx0y0z, lightNx0yNz, lightNxNy0z, lightNxNyNz);
        updateSkyLightLevelForCorner(skyLightLevels, CORNER_NxNyPz, diaphanous0x0yPz, diaphanous0xNy0z, diaphanous0xNyPz, diaphanousNx0y0z, diaphanousNx0yPz, diaphanousNxNy0z, light0x0yPz, light0xNy0z, light0xNyPz, lightNx0y0z, lightNx0yPz, lightNxNy0z, lightNxNyPz);
        updateSkyLightLevelForCorner(skyLightLevels, CORNER_NxPyNz, diaphanous0x0yNz, diaphanous0xPy0z, diaphanous0xPyNz, diaphanousNx0y0z, diaphanousNx0yNz, diaphanousNxPy0z, light0x0yNz, light0xPy0z, light0xPyNz, lightNx0y0z, lightNx0yNz, lightNxPy0z, lightNxPyNz);
        updateSkyLightLevelForCorner(skyLightLevels, CORNER_NxPyPz, diaphanous0x0yPz, diaphanous0xPy0z, diaphanous0xPyPz, diaphanousNx0y0z, diaphanousNx0yPz, diaphanousNxPy0z, light0x0yPz, light0xPy0z, light0xPyPz, lightNx0y0z, lightNx0yPz, lightNxPy0z, lightNxPyPz);
        updateSkyLightLevelForCorner(skyLightLevels, CORNER_PxNyNz, diaphanous0x0yNz, diaphanous0xNy0z, diaphanous0xNyNz, diaphanousPx0y0z, diaphanousPx0yNz, diaphanousPxNy0z, light0x0yNz, light0xNy0z, light0xNyNz, lightPx0y0z, lightPx0yNz, lightPxNy0z, lightPxNyNz);
        updateSkyLightLevelForCorner(skyLightLevels, CORNER_PxNyPz, diaphanous0x0yPz, diaphanous0xNy0z, diaphanous0xNyPz, diaphanousPx0y0z, diaphanousPx0yPz, diaphanousPxNy0z, light0x0yPz, light0xNy0z, light0xNyPz, lightPx0y0z, lightPx0yPz, lightPxNy0z, lightPxNyPz);
        updateSkyLightLevelForCorner(skyLightLevels, CORNER_PxPyNz, diaphanous0x0yNz, diaphanous0xPy0z, diaphanous0xPyNz, diaphanousPx0y0z, diaphanousPx0yNz, diaphanousPxPy0z, light0x0yNz, light0xPy0z, light0xPyNz, lightPx0y0z, lightPx0yNz, lightPxPy0z, lightPxPyNz);
        updateSkyLightLevelForCorner(skyLightLevels, CORNER_PxPyPz, diaphanous0x0yPz, diaphanous0xPy0z, diaphanous0xPyPz, diaphanousPx0y0z, diaphanousPx0yPz, diaphanousPxPy0z, light0x0yPz, light0xPy0z, light0xPyPz, lightPx0y0z, lightPx0yPz, lightPxPy0z, lightPxPyPz);

        callback.setReturnValue(skyLightLevels);
        callback.cancel();
    }
}
