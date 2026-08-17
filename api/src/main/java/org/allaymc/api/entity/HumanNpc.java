package org.allaymc.api.entity;

import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.entity.type.EntityTypes;
import org.allaymc.api.math.location.Location3dc;
import org.allaymc.api.math.location.Location3ic;

import java.util.Objects;

/**
 * Factory for reusable player-shaped NPC entities.
 * <p>
 * A human NPC is backed by Allay's normal {@link EntityPlayer} implementation but has no
 * {@link org.allaymc.api.player.Player} controller. This keeps the full player model, skin,
 * equipment and 3D movement/physics APIs without creating a network session or attaching AI.
 * Callers remain responsible for spawning, movement and persistence policy.
 */
public final class HumanNpc {

    private HumanNpc() {
    }

    /**
     * Creates an unbound player entity that can be used as a human NPC.
     *
     * @param initInfo entity initialization data
     * @return a player-shaped NPC with no client controller
     */
    public static EntityPlayer create(EntityInitInfo initInfo) {
        Objects.requireNonNull(initInfo, "initInfo");
        var playerType = Objects.requireNonNull(EntityTypes.PLAYER, "EntityTypes.PLAYER is not initialized");
        var npc = playerType.createEntity(initInfo);
        if (npc.isActualPlayer()) {
            throw new IllegalStateException("HumanNpc must not have a Player controller");
        }
        return npc;
    }

    /**
     * Creates a human NPC at the supplied floating-point location.
     */
    public static EntityPlayer create(Location3dc location) {
        Objects.requireNonNull(location, "location");
        return create(EntityInitInfo.builder().loc(location).build());
    }

    /**
     * Creates a human NPC at the supplied block location.
     */
    public static EntityPlayer create(Location3ic location) {
        Objects.requireNonNull(location, "location");
        return create(EntityInitInfo.builder().loc(location).build());
    }

    /**
     * Returns whether an entity player is an unbound/simulated player suitable for use as a human NPC.
     */
    public static boolean isHumanNpc(EntityPlayer entity) {
        return entity != null && !entity.isActualPlayer();
    }
}
