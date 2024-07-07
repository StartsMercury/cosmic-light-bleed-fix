package io.github.startsmercury.cosmic_light_bleed_fix.impl.client;

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
     * Updates block light level for a vertex towards all three axes. Calculations
     * account for light possibly being blocked, especially ones at the corner.
     *
     * @param lightLevels the block light levels
     * @param corner the corner vertex index
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
     * @see #updateBlockLightLevel
     */
    public static void updateBlockLightLevelForCorner(
        final short[] lightLevels,
        final int corner,
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
        updateBlockLightLevel(lightLevels, corner, AXIS_X, diaphanousBxAyAz, diaphanousBxAyBz, diaphanousBxByAz, lightBxAyAz, lightBxAyBz, lightBxByAz, lightBxByBz);
        updateBlockLightLevel(lightLevels, corner, AXIS_Y, diaphanousAxByAz, diaphanousAxByBz, diaphanousBxByAz, lightAxByAz, lightAxByBz, lightBxByAz, lightBxByBz);
        updateBlockLightLevel(lightLevels, corner, AXIS_Z, diaphanousAxAyBz, diaphanousAxByBz, diaphanousBxAyBz, lightAxAyBz, lightAxByBz, lightBxAyBz, lightBxByBz);
    }

    /**
     * Updates block light level for a vertex towards the given axis. Calculations
     * account for light possibly being blocked, especially ones at the corner.
     *
     * @param lightLevels the block light levels
     * @param corner the corner vertex index
     * @param axis the axis index offset
     * @param diaphanousCenter can light pass through the block at the face
     * @param diaphanousEdge1 can light from the corner pass through
     * @param diaphanousEdge2 can light from the corner pass through
     * @param lightCenter the light level at the face
     * @param lightEdge1 the light level before the corner
     * @param lightEdge2 the light level before the corner
     * @param lightCorner the light level at the corner
     */
    public static void updateBlockLightLevel(
        final short[] lightLevels,
        final int corner,
        final int axis,
        final boolean diaphanousCenter,
        final boolean diaphanousEdge1,
        final boolean diaphanousEdge2,
        final int lightCenter,
        final int lightEdge1,
        final int lightEdge2,
        final int lightCorner
    ) {
        final var index = corner * 3 + axis;
        var light = getMaxBlockLight(lightLevels[index], lightCenter);
        if (diaphanousCenter) {
            light = getMaxBlockLight(light, lightEdge1);
            light = getMaxBlockLight(light, lightEdge2);
            if (diaphanousEdge1 || diaphanousEdge2) {
                light = getMaxBlockLight(light, lightCorner);
            }
        }
        lightLevels[index] = light;
    }

    /**
     * Updates sky-light level for a vertex towards all three axes. Calculations
     * account for light possibly being blocked, especially ones at the corner.
     *
     * @param lightLevels the sky-light levels
     * @param corner the corner vertex index
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
     * @see #updateSkyLightLevel
     */
    public static void updateSkyLightLevelForCorner(
        final int[] lightLevels,
        final int corner,
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
        updateSkyLightLevel(lightLevels, corner, AXIS_X, diaphanousBxAyAz, diaphanousBxAyBz, diaphanousBxByAz, lightBxAyAz, lightBxAyBz, lightBxByAz, lightBxByBz);
        updateSkyLightLevel(lightLevels, corner, AXIS_Y, diaphanousAxByAz, diaphanousAxByBz, diaphanousBxByAz, lightAxByAz, lightAxByBz, lightBxByAz, lightBxByBz);
        updateSkyLightLevel(lightLevels, corner, AXIS_Z, diaphanousAxAyBz, diaphanousAxByBz, diaphanousBxAyBz, lightAxAyBz, lightAxByBz, lightBxAyBz, lightBxByBz);
    }

    /**
     * Updates sky-light level for a vertex towards the given axis. Calculations
     * account for light possibly being blocked, especially ones at the corner.
     *
     * @param lightLevels the sky-light levels
     * @param corner the corner vertex index
     * @param axis the axis index offset
     * @param diaphanousCenter can light pass through the block at the face
     * @param diaphanousEdge1 can light from the corner pass through
     * @param diaphanousEdge2 can light from the corner pass through
     * @param lightCenter the light level at the face
     * @param lightEdge1 the light level before the corner
     * @param lightEdge2 the light level before the corner
     * @param lightCorner the light level at the corner
     */
    public static void updateSkyLightLevel(
        final int[] lightLevels,
        final int corner,
        final int axis,
        final boolean diaphanousCenter,
        final boolean diaphanousEdge1,
        final boolean diaphanousEdge2,
        final int lightCenter,
        final int lightEdge1,
        final int lightEdge2,
        final int lightCorner
    ) {
        final var index = corner * 3 + axis;
        var light = getMaxBlockLight(lightLevels[index], lightCenter);
        if (diaphanousCenter) {
            light = getMaxBlockLight(light, lightEdge1);
            light = getMaxBlockLight(light, lightEdge2);
            if (diaphanousEdge1 || diaphanousEdge2) {
                light = getMaxBlockLight(light, lightCorner);
            }
        }
        lightLevels[index] = light;
    }
}
