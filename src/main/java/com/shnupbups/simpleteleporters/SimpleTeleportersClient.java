package com.shnupbups.simpleteleporters;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import com.shnupbups.simpleteleporters.init.SimpleTeleportersBlocks;
import com.shnupbups.simpleteleporters.init.SimpleTeleportersItems;
import com.shnupbups.simpleteleporters.item.TeleportCrystalItem;

public class SimpleTeleportersClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register((client) ->
		{
			if (client.world != null && client.player != null) {
				for (Hand hand : Hand.values()) {
					ItemStack stack = client.player.getStackInHand(hand);
					if (!stack.isEmpty() && stack.getItem() == SimpleTeleportersItems.ENDER_SHARD) {
						NbtCompound nbt = stack.getNbt();
						if (TeleportCrystalItem.hasPosition(nbt)) {
							RegistryKey<World> dimension = TeleportCrystalItem.getDimensionKey(nbt);
							if (client.player.getWorld().getRegistryKey().equals(dimension)) {
								BlockPos telePos = TeleportCrystalItem.getPosition(nbt);
								if(client.world.getBlockState(telePos.down()).isOf(SimpleTeleportersBlocks.TELEPORTER)) {
									telePos = telePos.down();
								}
								if (Math.sqrt(client.player.getBlockPos().getSquaredDistance(telePos)) < 15) {
									client.world.addParticle(ParticleTypes.MYCELIUM, // originally
											telePos.getX() + (1.1 - client.world.getRandom().nextFloat()),
											telePos.getY() + (1.1 - client.world.getRandom().nextFloat()),
											telePos.getZ() + (1.1 - client.world.getRandom().nextFloat()),
											0, 0, 0);
								}
							}
						}
					}
				}
			}
		});

		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), SimpleTeleportersBlocks.TELEPORTER);
	}
}
