package com.shnupbups.simpleteleporters.item;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import com.shnupbups.simpleteleporters.init.SimpleTeleportersBlocks;
import com.shnupbups.simpleteleporters.init.SimpleTeleportersSoundEvents;

public class TeleportCrystalItem extends Item {
	public TeleportCrystalItem(Settings settings) {
		super(settings);
	}

	public static boolean hasPosition(NbtCompound nbt) {
		return nbt != null && nbt.contains("pos");
	}

	public static int getX(NbtCompound nbt) {
		if (hasPosition(nbt)) return getPosition(nbt).getX();
		else return BlockPos.ORIGIN.getX();
	}

	public static int getY(NbtCompound nbt) {
		if (hasPosition(nbt)) return getPosition(nbt).getY();
		else return BlockPos.ORIGIN.getY();
	}

	public static int getZ(NbtCompound nbt) {
		if (hasPosition(nbt)) return getPosition(nbt).getZ();
		else return BlockPos.ORIGIN.getZ();
	}

	public static BlockPos getPosition(NbtCompound nbt) {
		if (!hasPosition(nbt)) return null;
		return BlockPos.fromLong(nbt.getLong("pos"));
	}

	public static String getDimensionName(NbtCompound nbt) {
		if (nbt != null && nbt.contains("dimension")) return nbt.getString("dimension");
		else return World.OVERWORLD.getValue().toString();
	}

	public static RegistryKey<World> getDimensionKey(NbtCompound nbt) {
		return RegistryKey.of(Registry.WORLD_KEY, new Identifier(getDimensionName(nbt)));
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		if (ctx.shouldCancelInteraction()) {
			PlayerEntity player = ctx.getPlayer();

			ItemStack stack = ctx.getStack().split(1);
			NbtCompound nbt = stack.getNbt();
			if (nbt == null) {
				stack.setNbt(new NbtCompound());
				nbt = stack.getNbt();
			}

			World world = ctx.getWorld();
			BlockPos pos = ctx.getBlockPos();
			BlockPos offsetPos;
			if (world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()) {
				offsetPos = pos;
			} else if (world.getBlockState(pos).isOf(SimpleTeleportersBlocks.TELEPORTER)) {
				offsetPos = pos.up();
			} else {
				offsetPos = pos.offset(ctx.getSide());
			}
			nbt.putLong("pos", offsetPos.asLong());
			String dimensionName = player.getWorld().getRegistryKey().getValue().toString();
			nbt.putString("dimension", dimensionName);

			if(!player.giveItemStack(stack)) {
				player.dropItem(stack, false);
			}

			MutableText msg = Text.translatable("text.simpleteleporters.crystal_info", offsetPos.getX(), offsetPos.getY(), offsetPos.getZ(), dimensionName);
			msg.setStyle(Style.EMPTY.withColor(Formatting.GREEN));

			player.sendMessage(msg, true);

			player.playSound(SimpleTeleportersSoundEvents.ENDER_SHARD_LINK, 0.5F, 0.4F / (ctx.getWorld().getRandom().nextFloat() * 0.4F + 0.8F));

			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext options) {
		NbtCompound nbt = stack.getNbt();
		if (!hasPosition(nbt)) {
			MutableText unlinked = Text.translatable("text.simpleteleporters.unlinked");
			unlinked.setStyle(Style.EMPTY.withColor(Formatting.RED));
			tooltip.add(unlinked);

			Text sneakKey = Text.literal("Sneak");
			Text useKey = Text.literal("Right Click");

			if (world != null && world.isClient()) {
				sneakKey = Text.keybind(MinecraftClient.getInstance().options.sneakKey.getTranslationKey());
				useKey = Text.keybind(MinecraftClient.getInstance().options.useKey.getTranslationKey());
			}

			MutableText info = Text.translatable("text.simpleteleporters.how_to_link", sneakKey, useKey);
			info.setStyle(Style.EMPTY.withColor(Formatting.BLUE));
			tooltip.add(info);
		} else {
			MutableText pos = Text.translatable("text.simpleteleporters.linked", getX(nbt), getY(nbt), getZ(nbt), getDimensionName(nbt));
			pos.setStyle(Style.EMPTY.withColor(Formatting.GREEN));

			tooltip.add(pos);
		}
	}
}
