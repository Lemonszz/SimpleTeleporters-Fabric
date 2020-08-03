package party.lemons.simpleteleporters;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.registry.Registry;
import party.lemons.simpleteleporters.init.SimpleTeleportersBlocks;
import party.lemons.simpleteleporters.init.SimpleTeleportersItems;
import party.lemons.simpleteleporters.item.BaseTeleportCrystalItem;

public class SimpleTeleportersClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTickCallback.EVENT.register((client) ->
		{
			if (client.world != null && client.player != null) {
				for (Hand hand : Hand.values()) {
					ItemStack stack = client.player.getStackInHand(hand);
					if (!stack.isEmpty() && stack.getItem() instanceof BaseTeleportCrystalItem) {
						CompoundTag tags = stack.getTag();
						if (tags != null) {
							String s = tags.getString("dim");
							Identifier id = new Identifier(s);
							if (client.player.world.getDimensionRegistryKey().getValue().equals(id)) {
								BlockPos telePos = new BlockPos(tags.getInt("x"), tags.getInt("y"), tags.getInt("z"));
								if (distanceBetween(client.player.getBlockPos(), telePos) < 15) {
									client.world.addParticle(ParticleTypes.MYCELIUM, // originally
											telePos.getX() + (1.1 - client.world.random.nextFloat()),
											telePos.getY() + (1.1 - client.world.random.nextFloat()),
											telePos.getZ() + (1.1 - client.world.random.nextFloat()),
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
	
	// TODO: replace with distanceTo or distanceSq as exists in BlockPos Or Vec3d API when it becomes evident
	private double distanceBetween(BlockPos pos1, BlockPos pos2) {
		return Math.abs(Math.abs(pos1.getX() - pos2.getX()) + Math.abs(pos1.getY() - pos2.getY()) + Math.abs(pos1.getZ() - pos2.getZ()));
	}
}
