package it.fru.muod;

import java.util.UUID;

import it.fru.muod.client.MudEffects;
import it.fru.muod.entity.MudballEntity;
import it.fru.muod.util.SpawnPacketHelper.PacketBufUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.registry.Registries;

@Environment(EnvType.CLIENT)
public class MuodClient implements ClientModInitializer {

    public static final Identifier MUD_EFFECT = new Identifier(Muod.MODID, "textures/gui/mud_effect.png");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Muod.MUDBALL_ENTITY, context -> new FlyingItemEntityRenderer<MudballEntity>(context));

        ClientPlayNetworking.registerGlobalReceiver(Muod.MUDBALL_PACKET_ID, (client, handler, buf, responseSender) -> {
            handlePacketAndSpawnEntity(client, buf);
        });

        ClientPlayNetworking.registerGlobalReceiver(Muod.ADD_MUD_EFFECT, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                client.player.playSound(Muod.MUDBALL_IMPACT, 1.0f, 1.0f);
                MudEffects.addEffect();
            });
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MudEffects.updateAndRender(drawContext);
        });
    }

    private void handlePacketAndSpawnEntity(MinecraftClient client, PacketByteBuf buf) {
        EntityType<?> type = Registries.ENTITY_TYPE.get(buf.readVarInt());
        UUID uuid = buf.readUuid();
        int entityId = buf.readVarInt();
        Vec3d pos = PacketBufUtil.readVec3d(buf);
        float pitch = PacketBufUtil.readAngle(buf);
        float yaw = PacketBufUtil.readAngle(buf);
            
        client.execute(() -> {
            if (client.world == null)
                throw new IllegalStateException("Tried to spawn entity in a null world!");

            Entity entity = type.create(client.world);
            if (entity == null)
                throw new IllegalStateException("Failed to create instance of entity \"" + Registries.ENTITY_TYPE.getId(type) + "\"!");

            entity.updateTrackedPosition(pitch, entityId, yaw);
            entity.setPos(pos.x, pos.y, pos.z);
            entity.setPitch(pitch);
            entity.setYaw(yaw);
            entity.setId(entityId);
            entity.setUuid(uuid);
            client.world.addEntity(entityId, entity);
        });
    }
}
