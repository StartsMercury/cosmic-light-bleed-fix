package io.github.startsmercury.cosmic_light_bleed_fix.impl.client;

import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

import static finalforeach.cosmicreach.rendering.ChunkMeshGroup.getMaxBlockLight;

/**
 * Utility class for Cosmic Light Bleed Fix.
 */
public final class CosmicLightBleedFix {
    /**
     * Vertex index towards the {@code (-x, -y, -z)} corner.
     */
    public static final int CORNER_NxNyNz = 0;

    /**
     * Vertex index towards the {@code (-x, -y, +z)} corner.
     */
    public static final int CORNER_NxNyPz = 1;

    /**
     * Vertex index towards the {@code (+x, -y, -z)} corner.
     */
    public static final int CORNER_PxNyNz = 2;

    /**
     * Vertex index towards the {@code (+x, -y, +z)} corner.
     */
    public static final int CORNER_PxNyPz = 3;

    /**
     * Vertex index towards the {@code (-x, +y, -z)} corner.
     */
    public static final int CORNER_NxPyNz = 4;

    /**
     * Vertex index towards the {@code (-x, +y, +z)} corner.
     */
    public static final int CORNER_NxPyPz = 5;

    /**
     * Vertex index towards the {@code (+x, +y, -z)} corner.
     */
    public static final int CORNER_PxPyNz = 6;

    /**
     * Vertex index towards the {@code (Px, +y, +z)} corner.
     */
    public static final int CORNER_PxPyPz = 7;

    /**
     * Index offset on the x-axis for vertices that accounts for face direction.
     */
    public static final int AXIS_X = 0;

    /**
     * Index offset on the y-axis for vertices that accounts for face direction.
     */
    public static final int AXIS_Y = 1;

    /**
     * Index offset on the z-axis for vertices that accounts for face direction.
     */
    public static final int AXIS_Z = 2;

    /**
     * Updates light level for all vertices. Calculations account for light possibly
     * being blocked, especially ones at the corner.
     *
     * @param lightLevelGetter the block light getter
     * @param lightLevelSetter the block light setter
     * @param mixLightFunction the function used to combine light data for a vertex
     * @param diaphanous0x0yNz can light pass through {@code ( 0,  0, -1)}
     * @param diaphanous0xNy0z can light pass through {@code ( 0, -1,  0)}
     * @param diaphanous0xNyNz can light pass through {@code ( 0, -1, -1)}
     * @param diaphanousNx0y0z can light pass through {@code (-1,  0,  0)}
     * @param diaphanousNx0yNz can light pass through {@code (-1,  0, -1)}
     * @param diaphanousNxNy0z can light pass through {@code (-1, -1,  0)}
     * @param diaphanous0x0yPz can light pass through {@code ( 0,  0, +1)}
     * @param diaphanous0xNyPz can light pass through {@code ( 0, -1, +1)}
     * @param diaphanousNx0yPz can light pass through {@code (-1,  0, +1)}
     * @param diaphanous0xPy0z can light pass through {@code ( 0, +1,  0)}
     * @param diaphanous0xPyNz can light pass through {@code ( 0, +1, -1)}
     * @param diaphanousNxPy0z can light pass through {@code (-1, +1,  0)}
     * @param diaphanous0xPyPz can light pass through {@code ( 0, +1, +1)}
     * @param diaphanousPx0y0z can light pass through {@code (+1,  0,  0)}
     * @param diaphanousPx0yNz can light pass through {@code (+1,  0, -1)}
     * @param diaphanousPxNy0z can light pass through {@code (+1, -1,  0)}
     * @param diaphanousPx0yPz can light pass through {@code (+1,  0, +1)}
     * @param diaphanousPxPy0z can light pass through {@code (+1, +1,  0)}
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
     * @see #updateLightLevelForCorner
     */
    public static void updateLightLevels(
        final IntUnaryOperator lightLevelGetter,
        final IntBinaryOperator lightLevelSetter,
        final IntBinaryOperator mixLightFunction,
        final boolean diaphanous0x0yNz,
        final boolean diaphanous0xNy0z,
        final boolean diaphanous0xNyNz,
        final boolean diaphanousNx0y0z,
        final boolean diaphanousNx0yNz,
        final boolean diaphanousNxNy0z,
        final boolean diaphanous0x0yPz,
        final boolean diaphanous0xNyPz,
        final boolean diaphanousNx0yPz,
        final boolean diaphanous0xPy0z,
        final boolean diaphanous0xPyNz,
        final boolean diaphanousNxPy0z,
        final boolean diaphanous0xPyPz,
        final boolean diaphanousPx0y0z,
        final boolean diaphanousPx0yNz,
        final boolean diaphanousPxNy0z,
        final boolean diaphanousPx0yPz,
        final boolean diaphanousPxPy0z,
        final int lightNxNyNz,
        final int lightNxNy0z,
        final int lightNxNyPz,
        final int lightNx0yNz,
        final int lightNx0y0z,
        final int lightNx0yPz,
        final int lightNxPyNz,
        final int lightNxPy0z,
        final int lightNxPyPz,
        final int light0xNyNz,
        final int light0xNy0z,
        final int light0xNyPz,
        final int light0x0yNz,
        final int light0x0yPz,
        final int light0xPyNz,
        final int light0xPy0z,
        final int light0xPyPz,
        final int lightPxNyNz,
        final int lightPxNy0z,
        final int lightPxNyPz,
        final int lightPx0yNz,
        final int lightPx0y0z,
        final int lightPx0yPz,
        final int lightPxPyNz,
        final int lightPxPy0z,
        final int lightPxPyPz
    ) {
        updateLightLevelForCorner(lightLevelGetter, lightLevelSetter, CORNER_NxNyNz, mixLightFunction, diaphanous0x0yNz, diaphanous0xNy0z, diaphanous0xNyNz, diaphanousNx0y0z, diaphanousNx0yNz, diaphanousNxNy0z, light0x0yNz, light0xNy0z, light0xNyNz, lightNx0y0z, lightNx0yNz, lightNxNy0z, lightNxNyNz);
        updateLightLevelForCorner(lightLevelGetter, lightLevelSetter, CORNER_NxNyPz, mixLightFunction, diaphanous0x0yPz, diaphanous0xNy0z, diaphanous0xNyPz, diaphanousNx0y0z, diaphanousNx0yPz, diaphanousNxNy0z, light0x0yPz, light0xNy0z, light0xNyPz, lightNx0y0z, lightNx0yPz, lightNxNy0z, lightNxNyPz);
        updateLightLevelForCorner(lightLevelGetter, lightLevelSetter, CORNER_NxPyNz, mixLightFunction, diaphanous0x0yNz, diaphanous0xPy0z, diaphanous0xPyNz, diaphanousNx0y0z, diaphanousNx0yNz, diaphanousNxPy0z, light0x0yNz, light0xPy0z, light0xPyNz, lightNx0y0z, lightNx0yNz, lightNxPy0z, lightNxPyNz);
        updateLightLevelForCorner(lightLevelGetter, lightLevelSetter, CORNER_NxPyPz, mixLightFunction, diaphanous0x0yPz, diaphanous0xPy0z, diaphanous0xPyPz, diaphanousNx0y0z, diaphanousNx0yPz, diaphanousNxPy0z, light0x0yPz, light0xPy0z, light0xPyPz, lightNx0y0z, lightNx0yPz, lightNxPy0z, lightNxPyPz);
        updateLightLevelForCorner(lightLevelGetter, lightLevelSetter, CORNER_PxNyNz, mixLightFunction, diaphanous0x0yNz, diaphanous0xNy0z, diaphanous0xNyNz, diaphanousPx0y0z, diaphanousPx0yNz, diaphanousPxNy0z, light0x0yNz, light0xNy0z, light0xNyNz, lightPx0y0z, lightPx0yNz, lightPxNy0z, lightPxNyNz);
        updateLightLevelForCorner(lightLevelGetter, lightLevelSetter, CORNER_PxNyPz, mixLightFunction, diaphanous0x0yPz, diaphanous0xNy0z, diaphanous0xNyPz, diaphanousPx0y0z, diaphanousPx0yPz, diaphanousPxNy0z, light0x0yPz, light0xNy0z, light0xNyPz, lightPx0y0z, lightPx0yPz, lightPxNy0z, lightPxNyPz);
        updateLightLevelForCorner(lightLevelGetter, lightLevelSetter, CORNER_PxPyNz, mixLightFunction, diaphanous0x0yNz, diaphanous0xPy0z, diaphanous0xPyNz, diaphanousPx0y0z, diaphanousPx0yNz, diaphanousPxPy0z, light0x0yNz, light0xPy0z, light0xPyNz, lightPx0y0z, lightPx0yNz, lightPxPy0z, lightPxPyNz);
        updateLightLevelForCorner(lightLevelGetter, lightLevelSetter, CORNER_PxPyPz, mixLightFunction, diaphanous0x0yPz, diaphanous0xPy0z, diaphanous0xPyPz, diaphanousPx0y0z, diaphanousPx0yPz, diaphanousPxPy0z, light0x0yPz, light0xPy0z, light0xPyPz, lightPx0y0z, lightPx0yPz, lightPxPy0z, lightPxPyPz);
    }

    /**
     * Updates light level for a vertex towards all three axes. Calculations account
     * for light possibly being blocked, especially ones at the corner.
     *
     * @param lightLevelGetter the block light getter
     * @param lightLevelSetter the block light setter
     * @param corner the corner vertex index
     * @param mixLightFunction the function used to combine light data for a vertex
     * @param diaphanousAxAyBz can light pass through
     * @param diaphanousAxByAz can light pass through
     * @param diaphanousAxByBz can light pass through
     * @param diaphanousBxAyAz can light pass through
     * @param diaphanousBxAyBz can light pass through
     * @param diaphanousBxByAz can light pass through
     * @param lightAxAyBz the light level
     * @param lightAxByAz the light level
     * @param lightAxByBz the light level
     * @param lightBxAyAz the light level
     * @param lightBxAyBz the light level
     * @param lightBxByAz the light level
     * @param lightBxByBz the light level
     * @see #updateLightLevelForCornerPlane
     */
    public static void updateLightLevelForCorner(
        final IntUnaryOperator lightLevelGetter,
        final IntBinaryOperator lightLevelSetter,
        final int corner,
        final IntBinaryOperator mixLightFunction,
        final boolean diaphanousAxAyBz,
        final boolean diaphanousAxByAz,
        final boolean diaphanousAxByBz,
        final boolean diaphanousBxAyAz,
        final boolean diaphanousBxAyBz,
        final boolean diaphanousBxByAz,
        final int lightAxAyBz,
        final int lightAxByAz,
        final int lightAxByBz,
        final int lightBxAyAz,
        final int lightBxAyBz,
        final int lightBxByAz,
        final int lightBxByBz
    ) {
        updateLightLevelForCornerPlane(lightLevelGetter, lightLevelSetter, corner, AXIS_X, mixLightFunction, diaphanousBxAyAz, diaphanousBxAyBz, diaphanousBxByAz, lightBxAyAz, lightBxAyBz, lightBxByAz, lightBxByBz);
        updateLightLevelForCornerPlane(lightLevelGetter, lightLevelSetter, corner, AXIS_Y, mixLightFunction, diaphanousAxByAz, diaphanousAxByBz, diaphanousBxByAz, lightAxByAz, lightAxByBz, lightBxByAz, lightBxByBz);
        updateLightLevelForCornerPlane(lightLevelGetter, lightLevelSetter, corner, AXIS_Z, mixLightFunction, diaphanousAxAyBz, diaphanousAxByBz, diaphanousBxAyBz, lightAxAyBz, lightAxByBz, lightBxAyBz, lightBxByBz);
    }

    /**
     * Updates light level for a vertex towards the given axis. Calculations account
     * for light possibly being blocked, especially ones at the corner.
     *
     * @param lightLevelGetter the block light getter
     * @param lightLevelSetter the block light setter
     * @param corner the corner vertex index
     * @param axis the axis index offset
     * @param mixLightFunction the function used to combine light data for a vertex
     * @param diaphanousCenter can light pass through the block at the face
     * @param diaphanousEdge1 can light from the corner pass through
     * @param diaphanousEdge2 can light from the corner pass through
     * @param lightCenter the light level at the face
     * @param lightEdge1 the light level before the corner
     * @param lightEdge2 the light level before the corner
     * @param lightCorner the light level at the corner
     */
    public static void updateLightLevelForCornerPlane(
        final IntUnaryOperator lightLevelGetter,
        final IntBinaryOperator lightLevelSetter,
        final int corner,
        final int axis,
        final IntBinaryOperator mixLightFunction,
        final boolean diaphanousCenter,
        final boolean diaphanousEdge1,
        final boolean diaphanousEdge2,
        final int lightCenter,
        final int lightEdge1,
        final int lightEdge2,
        final int lightCorner
    ) {
        final var index = corner * 3 + axis;
        var light = mixLightFunction.applyAsInt(lightLevelGetter.applyAsInt(index), lightCenter);
        if (diaphanousCenter) {
            light = mixLightFunction.applyAsInt(light, lightEdge1);
            light = mixLightFunction.applyAsInt(light, lightEdge2);
            if (diaphanousEdge1 || diaphanousEdge2) {
                light = getMaxBlockLight(light, lightCorner);
            }
        }
        lightLevelSetter.applyAsInt(index, light);
    }
}
