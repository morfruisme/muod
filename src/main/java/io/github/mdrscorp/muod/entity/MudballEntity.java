package io.github.mdrscorp.muod.entity;

import io.github.mdrscorp.muod.Muod;
import io.github.mdrscorp.muod.util.SpawnPacketHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class MudballEntity extends ThrownItemEntity {
    
    public MudballEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public MudballEntity(World world, LivingEntity owner) {
        super(Muod.MUDBALL_ENTITY, owner, world);
    }

    public MudballEntity(World world, double x, double y, double z) {
        super(Muod.MUDBALL_ENTITY, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Muod.MUDBALL;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            ParticleEffect particleEffect = new ItemStackParticleEffect(ParticleTypes.ITEM, Muod.MUDBALL.getDefaultStack());    // Default particles (from item sprite)
            for (int i = 0; i < 8; ++i) {
                float x = world.getRandom().nextFloat() * 0.2f;
                float y = world.getRandom().nextFloat() * 0.2f;
                float z = world.getRandom().nextFloat() * 0.2f;
                this.world.addParticle(particleEffect, this.getX() + x, this.getY() + y, this.getZ() + z, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 0);

        if (entity instanceof ServerPlayerEntity player)
            ServerPlayNetworking.send(player, Muod.ADD_MUD_EFFECT, PacketByteBufs.empty());
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient()) {
            this.world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return SpawnPacketHelper.create(this, Muod.MUDBALL_PACKET_ID);
    }
}
