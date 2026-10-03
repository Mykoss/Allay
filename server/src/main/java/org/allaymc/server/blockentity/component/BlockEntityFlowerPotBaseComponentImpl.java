package org.allaymc.server.blockentity.component;

import lombok.Getter;
import org.allaymc.api.block.data.BlockTags;
import org.allaymc.api.block.property.type.BlockPropertyTypes;
import org.allaymc.api.block.type.BlockState;
import org.allaymc.api.block.type.BlockTypes;
import org.allaymc.api.blockentity.BlockEntityInitInfo;
import org.allaymc.api.blockentity.component.BlockEntityFlowerPotBaseComponent;
import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.math.MathUtils;
import org.allaymc.api.utils.NBTIO;
import org.allaymc.server.block.component.event.CBlockOnReplaceEvent;
import org.cloudburstmc.nbt.NbtMap;

/**
 * @author Cdm2883
 */
public class BlockEntityFlowerPotBaseComponentImpl extends BlockEntityBaseComponentImpl implements BlockEntityFlowerPotBaseComponent {

    protected static final String TAG_PLANT_BLOCK = "PlantBlock";

    @Getter
    private BlockState plantBlock;
    private NbtMap unrecognizedPlant;

    public BlockEntityFlowerPotBaseComponentImpl(BlockEntityInitInfo initInfo) {
        super(initInfo);
    }

    @Override
    public boolean trySetPlantBlock(BlockState block) {
        if (block != null && !isValidPlant(block)) {
            return false;
        }

        plantBlock = block;
        unrecognizedPlant = null;

        var position = getPosition();
        var dimension = position.dimension();
        dimension.updateBlockProperty(BlockPropertyTypes.UPDATE_BIT, block != null, position);
        sendBlockEntityToViewers();
        return true;
    }

    private boolean isValidPlant(BlockState block) {
        return block.getBlockType().hasBlockTag(BlockTags.POTTABLE_PLANT);
    }

    @EventHandler
    protected void onBlockReplace(CBlockOnReplaceEvent event) {
        if (plantBlock == null) {
            return;
        }

        var current = event.getCurrentBlock();
        current.getDimension().dropItem(plantBlock.toItemStack(), MathUtils.center(current.getPosition()));
        plantBlock = null;
        unrecognizedPlant = null;
    }

    @Override
    public NbtMap saveNBT() {
        var savedNbt = super.saveNBT();
        if (plantBlock == null) {
            return savedNbt;
        }

        return savedNbt
                .toBuilder()
                .putCompound(TAG_PLANT_BLOCK, unrecognizedPlant != null ? unrecognizedPlant : this.plantBlock.getBlockStateNBT())
                .build();
    }

    @Override
    public void loadNBT(NbtMap nbt) {
        super.loadNBT(nbt);
        plantBlock = null;
        unrecognizedPlant = null;
        nbt.listenForCompound(TAG_PLANT_BLOCK, value -> {
            plantBlock = NBTIO.getAPI().fromBlockStateNBT(value);
            // Retain the original tag so saving a world cannot erase an unsupported plant.
            if (plantBlock.getBlockType() == BlockTypes.UNKNOWN) {
                unrecognizedPlant = value;
            }
        });
    }
}
