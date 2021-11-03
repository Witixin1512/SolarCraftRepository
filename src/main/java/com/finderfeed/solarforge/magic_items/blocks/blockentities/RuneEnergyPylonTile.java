package com.finderfeed.solarforge.magic_items.blocks.blockentities;

import com.finderfeed.solarforge.Helpers;
import com.finderfeed.solarforge.SolarCraftTags;
import com.finderfeed.solarforge.config.SolarcraftConfig;
import com.finderfeed.solarforge.magic_items.blocks.blockentities.runic_energy.RunicEnergyGiver;
import com.finderfeed.solarforge.magic_items.items.solar_lexicon.achievements.Progression;
import com.finderfeed.solarforge.magic_items.items.solar_lexicon.unlockables.ProgressionHelper;
import com.finderfeed.solarforge.misc_things.*;
import com.finderfeed.solarforge.packet_handler.SolarForgePacketHandler;
import com.finderfeed.solarforge.packet_handler.packets.UpdateTypeOnClientPacket;
import com.finderfeed.solarforge.registries.tile_entities.TileEntitiesRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.List;


public class RuneEnergyPylonTile extends BlockEntity implements  DebugTarget, RunicEnergyGiver {

    private RunicEnergy.Type type = null;
    private float currentEnergy = 0;
    private float energyPerTick = 0f;
    private float maxEnergy = 100000;
    private int updateTick = 40;

    public RuneEnergyPylonTile(BlockPos p_155229_, BlockState p_155230_) {
        super(TileEntitiesRegistry.RUNE_ENERGY_PYLON.get(), p_155229_, p_155230_);
    }


    public void setType(RunicEnergy.Type type) {
        this.type = type;
    }


    public static void tick(Level world, BlockPos pos, BlockState blockState, RuneEnergyPylonTile tile) {
        if (!tile.level.isClientSide){
            assignEnergyAndGainIt(tile);
            doUpdate(tile);
            doProgression(tile);



        }
        imbueItemsNear(tile);


    }

    public void addEnergy(RunicEnergy.Type type,double amount){
        this.currentEnergy += amount;
    }


    public static void imbueItemsNear(RuneEnergyPylonTile tile){
        AABB bb = new AABB(tile.worldPosition.offset(-8,-10,-8),tile.worldPosition.offset(8,0,8));
        tile.level.getEntitiesOfClass(ItemEntity.class,bb, (entity)-> entity.getItem().getItem() instanceof IImbuableItem).forEach(entity->{
            if (!entity.level.isClientSide) {
                int flag = updateEntityTime(entity);
                IImbuableItem item = (IImbuableItem) entity.getItem().getItem();
                int maxTime = item.getImbueTime();
                double neededEnergy = item.getCost();

                if (flag >= maxTime) {
                    if (ProgressionHelper.RUNES_MAP == null) {
                        ProgressionHelper.initRunesMap();
                    }
                    ItemStack stack = entity.getItem();
                    int maxRunes = (int) Math.floor(tile.getCurrentEnergy() / neededEnergy);
                    if (maxRunes > stack.getCount()) {
                        tile.currentEnergy -= stack.getCount() * neededEnergy;
                        ItemEntity entity1 = new ItemEntity(tile.level, entity.position().x, entity.position().y, entity.position().z,
                                new ItemStack(ProgressionHelper.RUNES_MAP.get(tile.getEnergyType()), stack.getCount()));
                        tile.level.addFreshEntity(entity1);
                        entity.remove(Entity.RemovalReason.DISCARDED);

                    } else {
                        tile.currentEnergy -= maxRunes * neededEnergy;
                        ItemEntity entity1 = new ItemEntity(tile.level, entity.position().x, entity.position().y, entity.position().z,
                                new ItemStack(ProgressionHelper.RUNES_MAP.get(tile.getEnergyType()), maxRunes));
                        tile.level.addFreshEntity(entity1);
                        entity.getItem().setCount(stack.getCount()-maxRunes);
                        entity.getPersistentData().putInt(SolarCraftTags.IMBUE_TIME_TAG,0);
                    }
                    if (entity.getThrower() != null){
                        Player player =entity.level.getPlayerByUUID(entity.getThrower());
                        if (player != null){
                            Helpers.fireProgressionEvent(player, Progression.SOLAR_RUNE);
                        }
                    }
                }
            }else{
                if (entity.level.getGameTime()%5 == 1) {

                    double rndX = entity.level.random.nextDouble()*0.6-0.3;
                    double rndY = entity.level.random.nextDouble()*0.6-0.3;
                    double rndZ = entity.level.random.nextDouble()*0.6-0.3;

                    entity.level.addParticle(ParticlesList.SMALL_SOLAR_STRIKE_PARTICLE.get(),
                            entity.position().x+rndX, entity.position().y+rndY, entity.position().z+rndZ, 0, 0.1, 0
                    );
                }
            }
        });
    }

    private static int updateEntityTime(ItemEntity entity){
        int time = entity.getPersistentData().getInt(SolarCraftTags.IMBUE_TIME_TAG);
        entity.getPersistentData().putInt(SolarCraftTags.IMBUE_TIME_TAG,time+1);
        return time;
    }

    public static void assignEnergyAndGainIt(RuneEnergyPylonTile tile){
        if (tile.type == null){
            tile.type = RunicEnergy.Type.values()[tile.level.random.nextInt(RunicEnergy.Type.values().length)];
        }

        if (tile.currentEnergy+tile.energyPerTick+ SolarcraftConfig.RUNIC_ENERGY_PER_TICK_PYLON.get().floatValue() <= tile.maxEnergy){
            tile.currentEnergy+=tile.energyPerTick+SolarcraftConfig.RUNIC_ENERGY_PER_TICK_PYLON.get().floatValue();
        }else{
            tile.currentEnergy = tile.maxEnergy;
        }
    }

    public static void doUpdate(RuneEnergyPylonTile tile){
        tile.updateTick++;
        if (tile.updateTick >= 40){
            SolarForgePacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(tile.worldPosition.getX(), tile.worldPosition.getY(), tile.worldPosition.getZ(), 40, tile.level.dimension())),
                    new UpdateTypeOnClientPacket(tile.worldPosition, tile.type.id));
            tile.updateTick = 0;
        }
    }

    public static void doProgression(RuneEnergyPylonTile tile){
        AABB box = new AABB(tile.worldPosition.offset(-2,-2,-2),tile.worldPosition.offset(2,2,2));
        tile.level.getEntitiesOfClass(Player.class,box).forEach((player)->{
            Helpers.fireProgressionEvent(player, Progression.RUNE_ENERGY_DEPOSIT);
        });
    }


    @Override
    public CompoundTag save(CompoundTag nbt) {
        if (this.type != null) {
            nbt.putString("energy_type", type.id);
        }
        nbt.putFloat("energy",currentEnergy);
        nbt.putFloat("energy_tick",energyPerTick);
        nbt.putFloat("maxenergy",maxEnergy);
        return super.save(nbt);
    }

    @Override
    public void load( CompoundTag nbt) {

        this.type = RunicEnergy.Type.byId(nbt.getString("energy_type"));
        currentEnergy = nbt.getFloat("energy");
        energyPerTick = nbt.getFloat("energy_tick");
        maxEnergy = nbt.getFloat("maxenergy");
        super.load( nbt);
    }

    public void givePlayerEnergy(Player entity,float amount){
        if (amount <= getCurrentEnergy()){
            this.currentEnergy-=amount;
            float flag = RunicEnergy.givePlayerEnergy(entity,amount,type);
            this.currentEnergy+=flag;

        }
    }


    public void setCurrentEnergy(float currentEnergy) {
        this.currentEnergy = currentEnergy;
    }

    public void setEnergyPerTick(float energyPerTick) {
        this.energyPerTick = energyPerTick;
    }

    public void setMaxEnergy(float maxEnergy) {
        this.maxEnergy = maxEnergy;
    }



    public RunicEnergy.Type getEnergyType() {
        return type;
    }

    public float getEnergyPerTick() {
        return energyPerTick;
    }

    public float getCurrentEnergy() {
        return currentEnergy;
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }

    @Override
    public double extractEnergy(RunicEnergy.Type type, double maxAmount) {
        if (getCurrentEnergy() >= maxAmount){
            this.currentEnergy-=maxAmount;
            return maxAmount;
        }else{
            double b = getCurrentEnergy();
            this.currentEnergy = 0;
            return b;
        }
    }

    @Override
    public List<RunicEnergy.Type> getTypes() {
        return this.getEnergyType() != null ? List.of(this.getEnergyType()) : null;
    }




    @Override
    public BlockPos getPos() {
        return worldPosition;
    }

    @Override
    public double getRunicEnergy(RunicEnergy.Type type) {
        return this.currentEnergy;
    }

    @Override
    public List<String> getDebugStrings() {
        return List.of(getEnergyType().id.toUpperCase()+ " " + getCurrentEnergy());
    }
}
