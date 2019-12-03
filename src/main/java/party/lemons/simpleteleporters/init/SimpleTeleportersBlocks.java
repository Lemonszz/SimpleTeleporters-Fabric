package party.lemons.simpleteleporters.init;

import net.fabricmc.fabric.api.block.FabricBlockSettings;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

import party.lemons.simpleteleporters.block.TeleporterBlock;

import static party.lemons.simpleteleporters.SimpleTeleporters.MODID;

public class SimpleTeleportersBlocks {
	public static Block TELEPORTER;
	
	public static void init() {
		TELEPORTER = registerBlock(new TeleporterBlock(FabricBlockSettings.of(Material.STONE).hardness(1).resistance(1).lightLevel(15).build()), "teleporter");
	}
	
	private static Block registerBlock(Block block, String name) {
		return registerBlock(block, name, true);
	}
	
	private static Block registerBlock(Block block, String name, boolean doItem) {
		Registry.register(Registry.BLOCK, MODID + ":" + name, block);
		
		if (doItem) {
			BlockItem item = new BlockItem(block, new Item.Settings().group(ItemGroup.TRANSPORTATION));
			item.appendBlocks(Item.BLOCK_ITEMS, item);
			SimpleTeleportersItems.registerItem(item, name);
		}
		return block;
	}
}
