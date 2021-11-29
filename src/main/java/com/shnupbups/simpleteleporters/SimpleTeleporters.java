package com.shnupbups.simpleteleporters;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;

import com.shnupbups.simpleteleporters.init.SimpleTeleportersBlockEntities;
import com.shnupbups.simpleteleporters.init.SimpleTeleportersBlocks;
import com.shnupbups.simpleteleporters.init.SimpleTeleportersItems;
import com.shnupbups.simpleteleporters.init.SimpleTeleportersSoundEvents;

public class SimpleTeleporters implements ModInitializer {
	public static final String MOD_ID = "simpleteleporters";

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		SimpleTeleportersBlocks.init();
		SimpleTeleportersItems.init();
		SimpleTeleportersBlockEntities.init();
		SimpleTeleportersSoundEvents.init();
	}
}
