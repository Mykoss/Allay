package org.allaymc.server.network.processor.ingame;

import org.allaymc.api.container.ContainerTypes;
import org.allaymc.api.eventbus.event.player.PlayerInteractEntityEvent;
import org.allaymc.api.player.Player;
import org.allaymc.server.network.NetworkHelper;
import org.allaymc.server.network.processor.PacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketType;
import org.cloudburstmc.protocol.bedrock.packet.InteractPacket;

/**
 * @author Cool_Loong
 */
public class InteractPacketProcessor extends PacketProcessor<InteractPacket> {
    @Override
    public void handleSync(Player player, InteractPacket packet, long receiveTime) {
        if (packet.getAction() == InteractPacket.Action.OPEN_INVENTORY) {
            player.getControlledEntity().getContainer(ContainerTypes.INVENTORY).addViewer(player);
            return;
        }

        if (packet.getAction() != InteractPacket.Action.NPC_OPEN) {
            return;
        }

        var entity = player.getControlledEntity();
        var target = entity.getDimension().getEntityManager().getEntity(packet.getRuntimeEntityId());
        if (target == null || !entity.canReach(target.getLocation())) {
            return;
        }

        var itemInHand = entity.getItemInHand();
        var event = new PlayerInteractEntityEvent(
                entity,
                target,
                itemInHand,
                NetworkHelper.fromNetwork(packet.getMousePosition())
        );
        if (!event.call()) {
            return;
        }

        if (!itemInHand.interactEntity(entity, target)) {
            target.onInteract(entity, itemInHand);
        }
    }

    @Override
    public BedrockPacketType getPacketType() {
        return BedrockPacketType.INTERACT;
    }
}
