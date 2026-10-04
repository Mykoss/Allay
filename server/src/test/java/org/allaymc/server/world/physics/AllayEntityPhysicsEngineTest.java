package org.allaymc.server.world.physics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AllayEntityPhysicsEngineTest {

    @Test
    void normalVanillaJumpDecayIsNotFlight() {
        var deltas = new double[]{0.42, 0.3332, 0.2481, 0.1648, 0.0831};
        long tick = 100;

        for (int i = 1; i < deltas.length; i++) {
            assertFalse(AllayEntityPhysicsEngine.isSuspiciousAirborneUpwardBoost(
                    tick + i - 1,
                    deltas[i - 1],
                    tick + i,
                    deltas[i]
            ));
        }
    }

    @Test
    void packetGapIsNotComparedAsEquivalentVelocity() {
        assertFalse(AllayEntityPhysicsEngine.isSuspiciousAirborneUpwardBoost(
                200,
                0.235,
                202,
                0.5156
        ));
    }

    @Test
    void duplicateOrOutOfOrderTicksAreIgnored() {
        assertFalse(AllayEntityPhysicsEngine.isSuspiciousAirborneUpwardBoost(
                300,
                0.20,
                300,
                0.40
        ));
        assertFalse(AllayEntityPhysicsEngine.isSuspiciousAirborneUpwardBoost(
                300,
                0.20,
                299,
                0.40
        ));
    }

    @Test
    void fallingOrApexTransitionIsNotTreatedAsThisDetectorSignal() {
        assertFalse(AllayEntityPhysicsEngine.isSuspiciousAirborneUpwardBoost(
                400,
                -0.02,
                401,
                0.42
        ));
        assertFalse(AllayEntityPhysicsEngine.isSuspiciousAirborneUpwardBoost(
                500,
                0.00,
                501,
                0.42
        ));
    }

    @Test
    void consecutiveMidAirUpwardResetIsDetected() {
        assertTrue(AllayEntityPhysicsEngine.isSuspiciousAirborneUpwardBoost(
                600,
                0.18,
                601,
                0.42
        ));
    }

    @Test
    void floatNoiseWithinToleranceIsAccepted() {
        assertFalse(AllayEntityPhysicsEngine.isSuspiciousAirborneUpwardBoost(
                700,
                0.235,
                701,
                0.284
        ));
    }
}
