package it.fru.muod.item;

import it.fru.muod.entity.MudballEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class MudballItem extends Item {
    
    public MudballItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!world.isClient) {
            MudballEntity mudballEntity = new MudballEntity(world, player);
            mudballEntity.setItem(itemStack);
            mudballEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, 1.5f, 1.0f);
            world.spawnEntity(mudballEntity);
        }

        player.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!player.getAbilities().creativeMode)
            itemStack.decrement(1);

        return TypedActionResult.success(itemStack, world.isClient());
    }
}   
