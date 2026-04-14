package better_progression;

import better_progression.items.ModItems;
import better_progression.skillLogic.Networking;
import better_progression.skills.Skills;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;

import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
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
		LOGGER.info("Initializing BetterProgression Server");

		ModItems.initialize();
		Skills.initialize();
		Networking.registerServerReceiver();

		//spawnt SkillpointBottles als Item an verschiedenen Stellen
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

			if (source.isBuiltin() && (BuiltInLootTables.ANCIENT_CITY == key
					|| BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS == key
					|| BuiltInLootTables.STRONGHOLD_LIBRARY == key
					|| BuiltInLootTables.END_CITY_TREASURE == key)) {

				LootPool.Builder poolBuilder = LootPool.lootPool()
						.setRolls(UniformGenerator.between(1.0f, 5.0f))
						.add(LootItem.lootTableItem(ModItems.SKILLPOINT_BOTTLE)
								.setWeight(1))
						.add(EmptyLootItem.emptyItem()
								.setWeight(19)
						);

				tableBuilder.pool(poolBuilder.build());
			}
        });
	}

	public static Logger getLogger() {
		return LOGGER;
	}
}