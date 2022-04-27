package io.github.mdrscorp.muod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.mdrscorp.muod.command.Data;
import io.github.mdrscorp.muod.command.HomeCommand;
import io.github.mdrscorp.muod.entity.MudballEntity;
import io.github.mdrscorp.muod.item.MudballItem;

public class Muod implements ModInitializer {
	
    public static final String MODID = "muod";
	public static final Logger LOGGER = LoggerFactory.getLogger(Muod.MODID);

	public static final Identifier ADD_MUD_EFFECT = new Identifier(Muod.MODID, "add_mud_effect_packet");
	public static final Identifier MUDBALL_PACKET_ID = new Identifier(Muod.MODID, "mudball_spawn_packet");
	public static final Identifier MUDBALL_IMPACT_ID = new Identifier(Muod.MODID, "mudball_impact");
	public static final Identifier QOMP_ID = new Identifier(Muod.MODID, "qomp");

	public static SoundEvent MUDBALL_IMPACT = new SoundEvent(MUDBALL_IMPACT_ID);
	public static SoundEvent QOMP = new SoundEvent(QOMP_ID);

    public static final MudballItem MUDBALL = new MudballItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(16));
	public static final MusicDisc QOMP_DISC = new MusicDisc(1, QOMP, new FabricItemSettings().rarity(Rarity.RARE).group(ItemGroup.MISC).maxCount(1));
	
	public static final EntityType<MudballEntity> MUDBALL_ENTITY = Registry.register(
		Registry.ENTITY_TYPE, 
		new Identifier(Muod.MODID, "mudball_entity"), 
		FabricEntityTypeBuilder.<MudballEntity>create(SpawnGroup.MISC, MudballEntity::new)
			.dimensions(EntityDimensions.fixed(0.25f, 0.25f))
			.trackRangeBlocks(4).trackedUpdateRate(10)
			.build()
	);

	@Override
	public void onInitialize() {
		Data.load();

		Registry.register(Registry.SOUND_EVENT, MUDBALL_IMPACT_ID, MUDBALL_IMPACT);
		Registry.register(Registry.SOUND_EVENT, QOMP_ID, QOMP);

		Registry.register(Registry.ITEM, new Identifier(Muod.MODID, "mudball"), MUDBALL);
		Registry.register(Registry.ITEM, new Identifier(Muod.MODID, "qomp_disc"), QOMP_DISC);

		LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, table, setter) -> {
			if (LootTables.END_CITY_TREASURE_CHEST.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
					.with(ItemEntry.builder(Muod.QOMP_DISC)
						.weight(1));
				table.pool(poolBuilder);
			}
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			HomeCommand.register(dispatcher);
		});

		ServerPlayConnectionEvents.INIT.register((handler, server) -> {
			Data.register(handler.player.getUuidAsString(), handler.player.getName().toString());
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			Data.save();
		});
	}

	private static class MusicDisc extends MusicDiscItem {
		public MusicDisc(int comparatorOutput, SoundEvent sound, Settings settings) {
			super(comparatorOutput, sound, settings);
		}
	}
}