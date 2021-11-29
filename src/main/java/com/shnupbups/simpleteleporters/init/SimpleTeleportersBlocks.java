package com.shnupbups.simpleteleporters.init;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import com.shnupbups.simpleteleporters.SimpleTeleporters;
import com.shnupbups.simpleteleporters.block.TeleporterBlock;

public class SimpleTeleportersBlocks {
	public static Block TELEPORTER;

	public static void init() {
		TELEPORTER = registerBlock(new TeleporterBlock(FabricBlockSettings.of(Material.STONE).nonOpaque().hardness(1).resistance(1).luminance(15)), "teleporter");
	}

	private static Block registerBlock(Block block, String name) {
		return registerBlock(block, name, true);
	}

	private static Block registerBlock(Block block, String name, boolean doItem) {
		Registry.register(Registry.BLOCK, SimpleTeleporters.id(name), block);

		if (doItem) {
			BlockItem item = new BlockItem(block, new Item.Settings().group(ItemGroup.TRANSPORTATION));
			item.appendBlocks(Item.BLOCK_ITEMS, item);
			SimpleTeleportersItems.registerItem(item, name);
		}
		return block;
	}
}
