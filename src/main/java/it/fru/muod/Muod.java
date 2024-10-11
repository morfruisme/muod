package it.fru.muod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.fru.muod.entity.MudballEntity;
import it.fru.muod.item.MudballItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
//import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.MusicDiscItem;
//import net.minecraft.loot.LootPool;
//import net.minecraft.loot.LootTables;
//import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class Muod implements ModInitializer {
	
    public static final String MODID = "muod";
	public static final Logger LOGGER = LoggerFactory.getLogger(Muod.MODID);

	public static final Identifier ADD_MUD_EFFECT = new Identifier(Muod.MODID, "add_mud_effect_packet");
	public static final Identifier MUDBALL_PACKET_ID = new Identifier(Muod.MODID, "mudball_spawn_packet");
	public static final Identifier MUDBALL_IMPACT_ID = new Identifier(Muod.MODID, "mudball_impact");
	public static final Identifier QOMP_ID = new Identifier(Muod.MODID, "qomp");

	public static SoundEvent MUDBALL_IMPACT = SoundEvent.of(MUDBALL_IMPACT_ID);
	public static SoundEvent QOMP = SoundEvent.of(QOMP_ID);

    public static final MudballItem MUDBALL = new MudballItem(new FabricItemSettings().maxCount(16));
	public static final MusicDiscItem QOMP_DISC = new MusicDiscItem(1, QOMP, new FabricItemSettings().rarity(Rarity.RARE).maxCount(1), 79);
	
	public static final EntityType<MudballEntity> MUDBALL_ENTITY = Registry.register(
		Registries.ENTITY_TYPE, 
		new Identifier(Muod.MODID, "mudball_entity"), 
		EntityType.Builder.<MudballEntity>create(MudballEntity::new, SpawnGroup.MISC)
			.setDimensions(0.25f, 0.25f)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
			.build("mudball_entity")
	);

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier(Muod.MODID, "mudball"), MUDBALL);
		//Registry.register(Registries.ITEM, new Identifier(Muod.MODID, "qomp_disc"), QOMP_DISC);
		
		Registry.register(Registries.SOUND_EVENT, MUDBALL_IMPACT_ID, MUDBALL_IMPACT);
		Registry.register(Registries.SOUND_EVENT, QOMP_ID, QOMP);


		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(itemGroup -> itemGroup.add(Muod.MUDBALL));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(itemGroup -> itemGroup.add(Muod.MUDBALL));
		//ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(itemGroup -> itemGroup.add(Muod.QOMP_DISC));

		/*LootTableEvents.MODIFY.register((manager, lootManager, id, tableBuilder, source) -> {
			if (LootTables.END_CITY_TREASURE_CHEST.equals(id) && source.isBuiltin()) {
				LootPool.Builder poolBuilder = LootPool.builder()
					.with(ItemEntry.builder(Muod.QOMP_DISC)
						.weight(1));
				tableBuilder.pool(poolBuilder);
			}
		});*/
	}
}