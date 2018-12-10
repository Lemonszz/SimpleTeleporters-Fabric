package party.lemons.simpleteleporters.init;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import party.lemons.simpleteleporters.block.entity.TeleporterBlockEntity;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static party.lemons.simpleteleporters.SimpleTeleporters.MODID;

public class SimpleTeleportersBlockEntities
{
	public static BlockEntityType<TeleporterBlockEntity> TELE_BE;

	public static void init()
	{
		TELE_BE = registerBlockEntityType("teleporter", TeleporterBlockEntity::new);
	}

	public static BlockEntityType registerBlockEntityType(String name, Supplier<BlockEntity> be)
	{
		return Registry.register(Registry.BLOCK_ENTITIES, MODID + ":" + name, BlockEntityType.Builder.create(be).method_11034(null));
	}
}
