package party.lemons.simpleteleporters.item;

import net.minecraft.ChatFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class TeleportCrystalItem extends Item
{
	public TeleportCrystalItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx)
	{
		PlayerEntity player = ctx.getPlayer();

		if(player.isSneaking())
		{
			ItemStack stack = ctx.getItemStack();
			CompoundTag tags = stack.getTag();
			if(tags == null)
			{
				stack.setTag(new CompoundTag());
				tags = stack.getTag();
			}

			BlockPos offPos = ctx.getBlockPos().offset(ctx.getFacing());
			tags.putInt("x", offPos.getX());
			tags.putInt("y", offPos.getY());
			tags.putInt("z", offPos.getZ());
			tags.putInt("dim", player.dimension.getRawId());
			tags.putFloat("direction", player.yaw);

			if(ctx.getWorld().getServer() != null && ctx.getWorld().getServer().isRemote())
			{
				TranslatableComponent msg = new TranslatableComponent("text.teleporters.crystalinfo", offPos.getX(), offPos.getY(), offPos.getZ());
				msg.setStyle(new Style().setColor(ChatFormat.GREEN));

				player.addChatMessage(msg, true);
			}
			player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.5F, 0.4F / (ctx.getWorld().random.nextFloat() * 0.4F + 0.8F));
			return ActionResult.PASS;
		}
		return null;
	}

	@Override
	public void buildTooltip(ItemStack stack, World world, List<Component> tooltip, TooltipContext options)
	{
		CompoundTag tags = stack.getTag();
		if(tags == null)
		{
			TranslatableComponent unlinked = new TranslatableComponent("text.teleporters.unlinked");
			unlinked.setStyle(new Style().setColor(ChatFormat.RED));

			TranslatableComponent info = new TranslatableComponent("text.teleporters.howtolink");
			info.setStyle(new Style().setColor(ChatFormat.BLUE));

			tooltip.add(unlinked);
			tooltip.add(info);

		}
		else
		{
			TranslatableComponent pos = new TranslatableComponent("text.teleporters.linked", tags.getInt("x"), tags.getInt("y"), tags.getInt("z"));
			pos.setStyle(new Style().setColor(ChatFormat.GREEN));

			tooltip.add(pos);
		}
	}
}
