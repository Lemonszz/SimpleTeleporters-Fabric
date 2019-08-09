package party.lemons.simpleteleporters.init;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import party.lemons.simpleteleporters.block.entity.TeleporterBlockEntity;

import java.util.function.Supplier;

import static party.lemons.simpleteleporters.SimpleTeleporters.MODID;

public class SimpleTeleportersBlockEntities {
	public static BlockEntityType<TeleporterBlockEntity> TELE_BE;

	public static void init() {
		TELE_BE = registerBlockEntityType("teleporter", TeleporterBlockEntity::new, SimpleTeleportersBlocks.TELEPORTER);
	}

	public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntityType(String name, Supplier<T> be, Block... blocks) {
		return Registry.register(Registry.BLOCK_ENTITY, new Identifier(MODID, name), BlockEntityType.Builder.create(be, blocks).build(null));
	}
}
