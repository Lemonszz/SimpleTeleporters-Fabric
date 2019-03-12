package party.lemons.simpleteleporters.init;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.block.BlockItem;
import net.minecraft.util.registry.Registry;
import party.lemons.simpleteleporters.block.TeleporterBlock;

import static party.lemons.simpleteleporters.SimpleTeleporters.MODID;

public class SimpleTeleportersBlocks
{
	public static Block TELEPORTER;

	public static void init()
	{
		TELEPORTER = registerBlock(new TeleporterBlock(FabricBlockSettings.create(Material.STONE).setHardness(1).setResistance(1).setLuminance(15).build()), "teleporter");
	}

	private static Block registerBlock(Block block, String name)
	{
		return registerBlock(block, name, true);
	}

	private static Block registerBlock(Block block, String name, boolean doItem)
	{
		Registry.register(Registry.BLOCKS, MODID + ":" + name, block);

		if(doItem)
		{
			BlockItem item = new BlockItem(block, new Item.Settings().itemGroup(ItemGroup.TRANSPORTATION));
			item.registerBlockItemMap(Item.BLOCK_ITEM_MAP, item);
			SimpleTeleportersItems.registerItem(item, name);
		}
		return block;
	}
}
