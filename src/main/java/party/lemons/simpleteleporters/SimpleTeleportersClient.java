package party.lemons.simpleteleporters;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import party.lemons.simpleteleporters.init.SimpleTeleportersItems;

public class SimpleTeleportersClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ClientTickCallback.EVENT.register((client) ->
		{
			if(client.world != null && client.player != null)
			{
				for(Hand hand : Hand.values())
				{
					ItemStack stack = client.player.getStackInHand(hand);
					if(!stack.isEmpty() && stack.getItem() == SimpleTeleportersItems.TELE_CRYSTAL)
					{
						CompoundTag tags = stack.getTag();
						if(tags != null)
						{
							if(tags.getInt("dim") == client.player.dimension.getRawId())
							{
								BlockPos telePos = new BlockPos(tags.getInt("x"), tags.getInt("y"), tags.getInt("z"));
								if(telePos.distanceTo(client.player.getPos()) < 15)
								{
									client.world.method_8406(ParticleTypes.MYCELIUM,
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
	}
}
