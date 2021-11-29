package com.shnupbups.simpleteleporters.init;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import com.shnupbups.simpleteleporters.SimpleTeleporters;
import com.shnupbups.simpleteleporters.block.entity.TeleporterBlockEntity;

public class SimpleTeleportersBlockEntities {
	public static BlockEntityType<TeleporterBlockEntity> TELEPORTER;

	public static void init() {
		TELEPORTER = registerBlockEntityType("teleporter", TeleporterBlockEntity::new, SimpleTeleportersBlocks.TELEPORTER);
	}

	public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntityType(String name, FabricBlockEntityTypeBuilder.Factory<T> be, Block... blocks) {
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, SimpleTeleporters.id(name), FabricBlockEntityTypeBuilder.create(be, blocks).build(null));
	}
}
