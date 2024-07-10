package io.github.startsmercury.cosmic_light_bleed_fix.mixin.client;

import com.badlogic.gdx.utils.Array;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import finalforeach.cosmicreach.rendering.ChunkMeshGroup;
import finalforeach.cosmicreach.rendering.MeshData;
import io.github.startsmercury.cosmic_light_bleed_fix.impl.client.CosmicLightBleedFix;
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
        method = """
            getMeshData(\
                Lfinalforeach/cosmicreach/savelib/ISavedChunk;\
            )Lcom/badlogic/gdx/utils/Array;\
        """,
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
        method = """
            getMeshData(\
                Lfinalforeach/cosmicreach/savelib/ISavedChunk;\
            )Lcom/badlogic/gdx/utils/Array;\
        """,
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
//      final var diaphanousNxNyNz = 0 == (opaqueBitMask & 1 << 6);
        final var diaphanousNxNy0z = 0 == (opaqueBitMask & 1 << 7);
//      final var diaphanousNxNyPz = 0 == (opaqueBitMask & 1 << 8);
        final var diaphanousNx0yNz = 0 == (opaqueBitMask & 1 << 9);
        final var diaphanousNx0y0z = 0 == (opaqueBitMask & 1);
        final var diaphanousNx0yPz = 0 == (opaqueBitMask & 1 << 10);
//      final var diaphanousNxPyNz = 0 == (opaqueBitMask & 1 << 11);
        final var diaphanousNxPy0z = 0 == (opaqueBitMask & 1 << 12);
//      final var diaphanousNxPyPz = 0 == (opaqueBitMask & 1 << 13);
        final var diaphanous0xNyNz = 0 == (opaqueBitMask & 1 << 14);
        final var diaphanous0xNy0z = 0 == (opaqueBitMask & 1 << 2);
        final var diaphanous0xNyPz = 0 == (opaqueBitMask & 1 << 15);
        final var diaphanous0x0yNz = 0 == (opaqueBitMask & 1 << 4);
        final var diaphanous0x0yPz = 0 == (opaqueBitMask & 1 << 5);
        final var diaphanous0xPyNz = 0 == (opaqueBitMask & 1 << 16);
        final var diaphanous0xPy0z = 0 == (opaqueBitMask & 1 << 3);
        final var diaphanous0xPyPz = 0 == (opaqueBitMask & 1 << 17);
//      final var diaphanousPxNyNz = 0 == (opaqueBitMask & 1 << 18);
        final var diaphanousPxNy0z = 0 == (opaqueBitMask & 1 << 19);
//      final var diaphanousPxNyPz = 0 == (opaqueBitMask & 1 << 20);
        final var diaphanousPx0yNz = 0 == (opaqueBitMask & 1 << 21);
        final var diaphanousPx0y0z = 0 == (opaqueBitMask & 1 << 1);
        final var diaphanousPx0yPz = 0 == (opaqueBitMask & 1 << 22);
//      final var diaphanousPxPyNz = 0 == (opaqueBitMask & 1 << 23);
        final var diaphanousPxPy0z = 0 == (opaqueBitMask & 1 << 24);
//      final var diaphanousPxPyPz = 0 == (opaqueBitMask & 1 << 25);

        CosmicLightBleedFix.updateLightLevels(
            i -> blockLightLevels[i],
            (i, ll) -> blockLightLevels[i] = (short) ll,
            ChunkMeshGroup::getMaxBlockLight,
            diaphanous0x0yNz,
            diaphanous0xNy0z,
            diaphanous0xNyNz,
            diaphanousNx0y0z,
            diaphanousNx0yNz,
            diaphanousNxNy0z,
            diaphanous0x0yPz,
            diaphanous0xNyPz,
            diaphanousNx0yPz,
            diaphanous0xPy0z,
            diaphanous0xPyNz,
            diaphanousNxPy0z,
            diaphanous0xPyPz,
            diaphanousPx0y0z,
            diaphanousPx0yNz,
            diaphanousPxNy0z,
            diaphanousPx0yPz,
            diaphanousPxPy0z,
            lightNxNyNz,
            lightNxNy0z,
            lightNxNyPz,
            lightNx0yNz,
            lightNx0y0z,
            lightNx0yPz,
            lightNxPyNz,
            lightNxPy0z,
            lightNxPyPz,
            light0xNyNz,
            light0xNy0z,
            light0xNyPz,
            light0x0yNz,
            light0x0yPz,
            light0xPyNz,
            light0xPy0z,
            light0xPyPz,
            lightPxNyNz,
            lightPxNy0z,
            lightPxNyPz,
            lightPx0yNz,
            lightPx0y0z,
            lightPx0yPz,
            lightPxPyNz,
            lightPxPy0z,
            lightPxPyPz
        );

        callback.setReturnValue(blockLightLevels);
        callback.cancel();
    }

    /**
     * Adds {@code opaqueBitMask} to sky-light levels array's end.
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
    private static void sendOpaqueBitMask(
        final CallbackInfoReturnable<Array<MeshData>> callback,
        final @Local(ordinal = 0) int[] skyLightLevels,
        final @Local(ordinal = 12) int opaqueBitMask
    ) {
        skyLightLevels[24] = opaqueBitMask;
    }

    /**
     * Retrieves {@code opaqueBitMask} from sky-light levels array's end.
     *
     * @param callback the injector callback
     * @param skyLightLevels the sky-light levels array to read from
     * @param opaqueBitMaskRef the opaque bit mask local ref to store to
     */
    @Inject(method = "calculateSkyLightLevels", at = @At("HEAD"))
    private static void receiveOpaqueBitMask(
        final CallbackInfoReturnable<int[]> callback,
        final @Local(ordinal = 0, argsOnly = true) int[] skyLightLevels,
        final @Share("opaqueBitMask") LocalIntRef opaqueBitMaskRef
    ) {
        opaqueBitMaskRef.set(skyLightLevels[24]);
    }

    /**
     * Replaces sky-light level calculations with one that separates light coming
     * from an axis.
     *
     * @param callback the injector callback
     * @param skyLightLevels the sky-light levels to modify
     * @param opaqueBitMaskRef the opaque bit mask local ref
     * @param lightNxNyNz the sky-light level at {@code (-1, -1, -1)}
     * @param lightNxNy0z the sky-light level at {@code (-1, -1,  0)}
     * @param lightNxNyPz the sky-light level at {@code (-1, -1, +1)}
     * @param lightNx0yNz the sky-light level at {@code (-1,  0, -1)}
     * @param lightNx0y0z the sky-light level at {@code (-1,  0,  0)}
     * @param lightNx0yPz the sky-light level at {@code (-1,  0, +1)}
     * @param lightNxPyNz the sky-light level at {@code (-1, +1, -1)}
     * @param lightNxPy0z the sky-light level at {@code (-1, +1,  0)}
     * @param lightNxPyPz the sky-light level at {@code (-1, +1, +1)}
     * @param light0xNyNz the sky-light level at {@code ( 0, -1, -1)}
     * @param light0xNy0z the sky-light level at {@code ( 0, -1,  0)}
     * @param light0xNyPz the sky-light level at {@code ( 0, -1, +1)}
     * @param light0x0yNz the sky-light level at {@code ( 0,  0, -1)}
     * @param light0x0yPz the sky-light level at {@code ( 0,  0, +1)}
     * @param light0xPyNz the sky-light level at {@code ( 0, +1, -1)}
     * @param light0xPy0z the sky-light level at {@code ( 0, +1,  0)}
     * @param light0xPyPz the sky-light level at {@code ( 0, +1, +1)}
     * @param lightPxNyNz the sky-light level at {@code (+1, -1, -1)}
     * @param lightPxNy0z the sky-light level at {@code (+1, -1,  0)}
     * @param lightPxNyPz the sky-light level at {@code (+1, -1, +1)}
     * @param lightPx0yNz the sky-light level at {@code (+1,  0, -1)}
     * @param lightPx0y0z the sky-light level at {@code (+1,  0,  0)}
     * @param lightPx0yPz the sky-light level at {@code (+1,  0, +1)}
     * @param lightPxPyNz the sky-light level at {@code (+1, +1, -1)}
     * @param lightPxPy0z the sky-light level at {@code (+1, +1,  0)}
     * @param lightPxPyPz the sky-light level at {@code (+1, +1, +1)}
     */
    @Inject(
        method = "calculateSkyLightLevels",
        at = @At(value = "CONSTANT", ordinal = 1, args = "intValue=0"),
        cancellable = true
    )
    private static void overwriteSkyLightCalculation(
        final CallbackInfoReturnable<int[]> callback,
        final @Local(ordinal = 0, argsOnly = true) int[] skyLightLevels,
        final @Share("opaqueBitMask") LocalIntRef opaqueBitMaskRef,
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
        final var opaqueBitMask = opaqueBitMaskRef.get();

//      final var diaphanousNxNyNz = 0 == (opaqueBitMask & 1 << 6);
        final var diaphanousNxNy0z = 0 == (opaqueBitMask & 1 << 7);
//      final var diaphanousNxNyPz = 0 == (opaqueBitMask & 1 << 8);
        final var diaphanousNx0yNz = 0 == (opaqueBitMask & 1 << 9);
        final var diaphanousNx0y0z = 0 == (opaqueBitMask & 1);
        final var diaphanousNx0yPz = 0 == (opaqueBitMask & 1 << 10);
//      final var diaphanousNxPyNz = 0 == (opaqueBitMask & 1 << 11);
        final var diaphanousNxPy0z = 0 == (opaqueBitMask & 1 << 12);
//      final var diaphanousNxPyPz = 0 == (opaqueBitMask & 1 << 13);
        final var diaphanous0xNyNz = 0 == (opaqueBitMask & 1 << 14);
        final var diaphanous0xNy0z = 0 == (opaqueBitMask & 1 << 2);
        final var diaphanous0xNyPz = 0 == (opaqueBitMask & 1 << 15);
        final var diaphanous0x0yNz = 0 == (opaqueBitMask & 1 << 4);
        final var diaphanous0x0yPz = 0 == (opaqueBitMask & 1 << 5);
        final var diaphanous0xPyNz = 0 == (opaqueBitMask & 1 << 16);
        final var diaphanous0xPy0z = 0 == (opaqueBitMask & 1 << 3);
        final var diaphanous0xPyPz = 0 == (opaqueBitMask & 1 << 17);
//      final var diaphanousPxNyNz = 0 == (opaqueBitMask & 1 << 18);
        final var diaphanousPxNy0z = 0 == (opaqueBitMask & 1 << 19);
//      final var diaphanousPxNyPz = 0 == (opaqueBitMask & 1 << 20);
        final var diaphanousPx0yNz = 0 == (opaqueBitMask & 1 << 21);
        final var diaphanousPx0y0z = 0 == (opaqueBitMask & 1 << 1);
        final var diaphanousPx0yPz = 0 == (opaqueBitMask & 1 << 22);
//      final var diaphanousPxPyNz = 0 == (opaqueBitMask & 1 << 23);
        final var diaphanousPxPy0z = 0 == (opaqueBitMask & 1 << 24);
//      final var diaphanousPxPyPz = 0 == (opaqueBitMask & 1 << 25);

        CosmicLightBleedFix.updateLightLevels(
            i -> skyLightLevels[i],
            (i, ll) -> skyLightLevels[i] = ll,
            Math::max,
            diaphanous0x0yNz,
            diaphanous0xNy0z,
            diaphanous0xNyNz,
            diaphanousNx0y0z,
            diaphanousNx0yNz,
            diaphanousNxNy0z,
            diaphanous0x0yPz,
            diaphanous0xNyPz,
            diaphanousNx0yPz,
            diaphanous0xPy0z,
            diaphanous0xPyNz,
            diaphanousNxPy0z,
            diaphanous0xPyPz,
            diaphanousPx0y0z,
            diaphanousPx0yNz,
            diaphanousPxNy0z,
            diaphanousPx0yPz,
            diaphanousPxPy0z,
            lightNxNyNz,
            lightNxNy0z,
            lightNxNyPz,
            lightNx0yNz,
            lightNx0y0z,
            lightNx0yPz,
            lightNxPyNz,
            lightNxPy0z,
            lightNxPyPz,
            light0xNyNz,
            light0xNy0z,
            light0xNyPz,
            light0x0yNz,
            light0x0yPz,
            light0xPyNz,
            light0xPy0z,
            light0xPyPz,
            lightPxNyNz,
            lightPxNy0z,
            lightPxNyPz,
            lightPx0yNz,
            lightPx0y0z,
            lightPx0yPz,
            lightPxPyNz,
            lightPxPy0z,
            lightPxPyPz
        );

        callback.setReturnValue(skyLightLevels);
        callback.cancel();
    }
}
