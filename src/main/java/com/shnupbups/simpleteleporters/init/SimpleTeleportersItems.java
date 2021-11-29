package com.shnupbups.simpleteleporters.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import com.shnupbups.simpleteleporters.SimpleTeleporters;
import com.shnupbups.simpleteleporters.item.TeleportCrystalItem;

public class SimpleTeleportersItems {
	public static Item ENDER_SHARD;

	public static void init() {
		ENDER_SHARD = registerItem(new TeleportCrystalItem(new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(16)), "ender_shard");
	}

	public static Item registerItem(Item item, String name) {
		Registry.register(Registry.ITEM, SimpleTeleporters.id(name), item);

		return item;
	}
}
