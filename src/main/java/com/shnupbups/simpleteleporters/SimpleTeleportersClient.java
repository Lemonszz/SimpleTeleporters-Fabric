package com.shnupbups.simpleteleporters;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
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
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if (client.player != null) {
				ClientPlayerEntity player = client.player;
				World world = player.getWorld();
				Random random = world.getRandom();
				for (Hand hand : Hand.values()) {
					ItemStack stack = player.getStackInHand(hand);
					if (!stack.isEmpty() && stack.isOf(SimpleTeleportersItems.ENDER_SHARD)) {
						NbtCompound nbt = stack.getNbt();
						if (TeleportCrystalItem.hasPosition(nbt)) {
							RegistryKey<World> dimension = TeleportCrystalItem.getDimensionKey(nbt);
							if (world.getRegistryKey().equals(dimension)) {
								BlockPos telePos = TeleportCrystalItem.getPosition(nbt);
								BlockPos downPos = telePos.down();
								if(world.getBlockState(downPos).isOf(SimpleTeleportersBlocks.TELEPORTER)) {
									telePos = downPos;
								}
								if (player.getBlockPos().getManhattanDistance(telePos) < 15) {
									world.addParticle(ParticleTypes.MYCELIUM,
											random.nextTriangular(telePos.getX() + 0.5, 0.2),
											random.nextTriangular(telePos.getY() + 0.5, 0.2),
											random.nextTriangular(telePos.getZ() + 0.5, 0.2),
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
