package org.allaymc.server.network.protocol.v2193;

import org.allaymc.server.network.protocol.ProtocolData;
import org.allaymc.server.network.protocol.v2169.PacketEncoder_v2169;

/**
 * Encoder for Bedrock protocol 2193.
 *
 * Protocol-specific wire changes are handled by the codec; server packet
 * construction can be specialized here when 2193-only fields are required.
 */
public class PacketEncoder_v2193 extends PacketEncoder_v2169 {

    public PacketEncoder_v2193(ProtocolData data) {
        super(data);
    }
}
