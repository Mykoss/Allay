package org.allaymc.server.block.component.container;

import org.allaymc.api.block.component.BlockBlockEntityHolderComponent;
import org.allaymc.api.block.data.BlockFace;
import org.allaymc.api.block.dto.Block;
import org.allaymc.api.block.type.BlockState;
import org.allaymc.api.block.type.BlockTypes;
import org.allaymc.api.blockentity.interfaces.BlockEntityChest;
import org.allaymc.server.component.ComponentManager;
import org.allaymc.testutils.AllayTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(AllayTestExtension.class)
class ChestPairingTest {
    @Test
    void ordinaryChestsDoNotPairWithCopperOrTrappedChests() {
        assertPairing(false, BlockTypes.CHEST.getDefaultState(), BlockTypes.COPPER_CHEST.getDefaultState());
        assertPairing(false, BlockTypes.CHEST.getDefaultState(), BlockTypes.TRAPPED_CHEST.getDefaultState());
        assertPairing(true, BlockTypes.CHEST.getDefaultState(), BlockTypes.CHEST.getDefaultState());
    }

    @Test
    void copperChestsPairOnlyWithinTheirFamily() {
        assertPairing(false, BlockTypes.COPPER_CHEST.getDefaultState(), BlockTypes.CHEST.getDefaultState());
        assertPairing(false, BlockTypes.COPPER_CHEST.getDefaultState(), BlockTypes.TRAPPED_CHEST.getDefaultState());
        assertPairing(true, BlockTypes.COPPER_CHEST.getDefaultState(), BlockTypes.OXIDIZED_COPPER_CHEST.getDefaultState());
    }

    @SuppressWarnings("unchecked")
    private static void assertPairing(boolean expected, BlockState placed, BlockState adjacent) {
        var chest = mock(BlockEntityChest.class);
        var neighborChest = mock(BlockEntityChest.class);
        var holder = mock(BlockBlockEntityHolderComponent.class);
        when(holder.getBlockEntity(any())).thenReturn(chest);
        when(neighborChest.getBlockState()).thenReturn(adjacent);
        when(neighborChest.tryPairWith(chest)).thenReturn(true);

        var oldBlock = mock(Block.class);
        when(oldBlock.getBlockState()).thenReturn(BlockTypes.AIR.getDefaultState());
        var neighbor = mock(Block.class);
        when(oldBlock.offsetPos(any(BlockFace.class))).thenReturn(neighbor);
        when(neighbor.getBlockState()).thenReturn(adjacent);
        when(neighbor.getBlockEntity()).thenReturn(neighborChest);

        BlockChestBaseComponentImpl component = placed.getBlockType() == BlockTypes.CHEST
                ? new PlainChest(holder) : new CopperChest(holder);
        component.afterPlaced(oldBlock, placed, null);

        verify(neighborChest, expected ? times(1) : never()).tryPairWith(chest);
        verify(chest, expected ? times(1) : never()).tryPairWith(neighborChest);
    }

    private static class PlainChest extends BlockChestBaseComponentImpl {
        PlainChest(BlockBlockEntityHolderComponent<BlockEntityChest> holder) {
            super(BlockTypes.CHEST);
            manager = mock(ComponentManager.class);
            blockEntityHolderComponent = holder;
        }
    }

    private static class CopperChest extends BlockCopperChestBaseComponentImpl {
        CopperChest(BlockBlockEntityHolderComponent<BlockEntityChest> holder) {
            super(BlockTypes.COPPER_CHEST);
            manager = mock(ComponentManager.class);
            blockEntityHolderComponent = holder;
        }
    }
}
