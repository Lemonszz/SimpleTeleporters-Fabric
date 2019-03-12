package party.lemons.simpleteleporters.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;
import party.lemons.simpleteleporters.item.TeleportCrystalItem;

import static party.lemons.simpleteleporters.SimpleTeleporters.MODID;

public class SimpleTeleportersItems
{
	public static  Item TELE_CRYSTAL;

	public static void init()
	{
		TELE_CRYSTAL = registerItem(new TeleportCrystalItem(new Item.Settings().itemGroup(ItemGroup.TRANSPORTATION)), "tele_crystal");
	}

	public static Item registerItem(Item item, String name)
	{
		Registry.register(Registry.ITEM, MODID + ":" + name, item);

		return item;
	}
}
