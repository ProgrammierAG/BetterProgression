package better_progression;

import better_progression.Items.ModItems;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterProgression implements ModInitializer {
	public static final String MOD_ID = "better_progression";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItems.initialize();

		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			// Nur eingebaute (Vanilla/Mod) Tabellen ändern, keine User-Datapacks
			if (!source.isBuiltin()) return;

			// Beispiel: Etwas in die Wüstentempel-Truhe legen
			if (BuiltInLootTables.ANCIENT_CITY.equals(key)) {
				LootPool pool = LootPool.lootPool().build();


				tableBuilder.pool(pool);
			}
		});

		LOGGER.info("Hello Fabric world!");
	}
}