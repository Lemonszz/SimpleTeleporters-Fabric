package party.lemons.simpleteleporters;

import net.fabricmc.api.ModInitializer;
// import net.fabricmc.fabric.api.block.BreakInteractable;
// import net.fabricmc.fabric.api.events.PlayerInteractionEvent;
// import net.fabricmc.fabric.api.events.client.ClientTickEvent;
// import net.minecraft.block.BlockState;
// import net.minecraft.util.ActionResult;
import party.lemons.simpleteleporters.init.SimpleTeleportersBlockEntities;
import party.lemons.simpleteleporters.init.SimpleTeleportersBlocks;
import party.lemons.simpleteleporters.init.SimpleTeleportersItems;

public class SimpleTeleporters implements ModInitializer
{
	public static final String MODID = "simpleteleporters";

	@Override
	public void onInitialize()
	{
		SimpleTeleportersBlocks.init();
		SimpleTeleportersItems.init();
		SimpleTeleportersBlockEntities.init();


	}
}
