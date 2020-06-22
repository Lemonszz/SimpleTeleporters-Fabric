package party.lemons.simpleteleporters.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.List;

public class TeleportCrystalItem extends Item {
	public TeleportCrystalItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		PlayerEntity player = ctx.getPlayer();
		
		if (player.isSneaking()) {
			ItemStack stack = ctx.getStack();
			CompoundTag tags = stack.getTag();
			if (tags == null) {
				stack.setTag(new CompoundTag());
				tags = stack.getTag();
			}
			
			BlockPos offPos = ctx.getBlockPos().offset(ctx.getSide());
			tags.putInt("x", offPos.getX());
			tags.putInt("y", offPos.getY());
			tags.putInt("z", offPos.getZ());
			tags.putString("dim", player.world.getDimensionRegistryKey().getValue().toString());
			tags.putFloat("direction", player.yaw);
			
			TranslatableText msg = new TranslatableText("text.teleporters.crystal_info", offPos.getX(), offPos.getY(), offPos.getZ());
			msg.setStyle(Style.EMPTY.withColor(Formatting.GREEN));
			
			player.sendMessage(msg, true);
			
			player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.5F, 0.4F / (ctx.getWorld().random.nextFloat() * 0.4F + 0.8F));
			return ActionResult.PASS;
		}
		return ActionResult.PASS;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext options) {
		CompoundTag tags = stack.getTag();
		if (tags == null) {
			TranslatableText unlinked = new TranslatableText("text.teleporters.unlinked");
			unlinked.setStyle(Style.EMPTY.withColor(Formatting.RED));
			
			TranslatableText info = new TranslatableText("text.teleporters.how_to_link");
			info.setStyle(Style.EMPTY.withColor(Formatting.BLUE));
			
			tooltip.add(unlinked);
			tooltip.add(info);
			
		} else {
			TranslatableText pos = new TranslatableText("text.teleporters.linked", tags.getInt("x"), tags.getInt("y"), tags.getInt("z"), tags.getString("dim"));
			pos.setStyle(Style.EMPTY.withColor(Formatting.GREEN));
			
			tooltip.add(pos);
		}
	}
}
