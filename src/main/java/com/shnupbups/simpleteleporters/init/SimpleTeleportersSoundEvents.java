package com.shnupbups.simpleteleporters.init;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.shnupbups.simpleteleporters.SimpleTeleporters;

public class SimpleTeleportersSoundEvents {
	public static SoundEvent TELEPORTER_TELEPORT;
	public static SoundEvent TELEPORTER_CRYSTAL_INSERTED;
	public static SoundEvent TELEPORTER_CRYSTAL_REMOVED;
	public static SoundEvent ENDER_SHARD_LINK;

	public static void init() {
		TELEPORTER_TELEPORT = register("block.teleporter.teleport");
		TELEPORTER_CRYSTAL_INSERTED = register("block.teleporter.crystal_inserted");
		TELEPORTER_CRYSTAL_REMOVED = register("block.teleporter.crystal_removed");
		ENDER_SHARD_LINK = register("item.ender_shard.link");
	}

	public static SoundEvent register(String path) {
		Identifier id = SimpleTeleporters.id(path);
		return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
	}
}
