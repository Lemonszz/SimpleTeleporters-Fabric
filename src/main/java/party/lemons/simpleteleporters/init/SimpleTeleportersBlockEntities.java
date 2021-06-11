package party.lemons.simpleteleporters.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import party.lemons.simpleteleporters.block.entity.TeleporterBlockEntity;

import static party.lemons.simpleteleporters.SimpleTeleporters.MODID;

public class SimpleTeleportersBlockEntities {
	public static BlockEntityType<TeleporterBlockEntity> TELE_BE;
	
	public static void init() {
		TELE_BE = registerBlockEntityType("teleporter", TeleporterBlockEntity::new, SimpleTeleportersBlocks.TELEPORTER);
	}
	
	public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntityType(String name, FabricBlockEntityTypeBuilder.Factory<T> be, Block... blocks) {
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, name), FabricBlockEntityTypeBuilder.create(be, blocks).build(null));
	}
}
