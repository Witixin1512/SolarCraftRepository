package com.finderfeed.solarforge.magic_items.blocks.blockentities;

import com.finderfeed.solarforge.Helpers;
import com.finderfeed.solarforge.events.other_events.event_handler.EventHandler;
import com.finderfeed.solarforge.multiblocks.Multiblocks;
import com.finderfeed.solarforge.registries.blocks.BlocksRegistry;
import com.finderfeed.solarforge.registries.tile_entities.TileEntitiesRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.function.Function;

public class RadiantPortalCreatorTile extends BlockEntity {

    private AABB TP_AABB;


    public RadiantPortalCreatorTile( BlockPos p_155229_, BlockState p_155230_) {
        super(TileEntitiesRegistry.PORTAL_CREATOR.get(), p_155229_, p_155230_);
    }


    public boolean isActive(){
        return Helpers.checkStructure(level,worldPosition.offset(-3,-1,-3), Multiblocks.RADIANT_LAND_PORTAL.getM());
    }



    public static void tick(RadiantPortalCreatorTile tile, BlockState state, BlockPos pos, Level world){

        if (!world.isClientSide && tile.isActive()){
            if (tile.TP_AABB == null){
                tile.TP_AABB = new AABB(0.25f,0,0.25f,0.75f,2.5,0.75f).move(tile.worldPosition);
            }
            world.getEntitiesOfClass(Entity.class,tile.TP_AABB, Entity::canChangeDimensions).forEach((entity)->{
                if (world.getServer() != null) {
                    ServerLevel destination;
                    if (entity.level.dimension() == Level.OVERWORLD){
                        destination = world.getServer().getLevel(EventHandler.RADIANT_LAND_KEY);
                    }else{
                        destination = world.getServer().getLevel(Level.OVERWORLD);
                    }
                    if (destination != null){
                        entity.changeDimension(destination, RadiantTeleporter.INSTANCE);
                    }
                }
            });

        }

    }



}
class RadiantTeleporter implements ITeleporter {

    public static RadiantTeleporter INSTANCE = new RadiantTeleporter();

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {

        return repositionEntity.apply(true);
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {

        BlockPos pos;
        if (destWorld.dimension() == Level.OVERWORLD){
            pos = entity.getOnPos();
        }else{
            pos = BlockPos.ZERO;
        }
        destWorld.getChunkAt(pos).markUnsaved();
        return this.isVanilla() ? defaultPortalInfo.apply(destWorld) :
                new PortalInfo(new Vec3(pos.getX(),destWorld.getHeight(Heightmap.Types.WORLD_SURFACE,pos.getX(),pos.getZ()),pos.getZ()),
                        Vec3.ZERO,
                        entity.getYRot(),
                        entity.getXRot());
    }
}