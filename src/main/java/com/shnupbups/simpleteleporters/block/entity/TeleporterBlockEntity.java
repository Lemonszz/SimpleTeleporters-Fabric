package com.shnupbups.simpleteleporters.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.shnupbups.simpleteleporters.init.SimpleTeleportersBlockEntities;
import com.shnupbups.simpleteleporters.item.TeleportCrystalItem;

public class TeleporterBlockEntity extends BlockEntity {
	private ItemStack crystal = ItemStack.EMPTY;
	private int cooldown = 0;

	public TeleporterBlockEntity(BlockPos pos, BlockState state) {
		super(SimpleTeleportersBlockEntities.TELEPORTER, pos, state);
	}

	public static void tick(World world, BlockPos pos, BlockState state, TeleporterBlockEntity teleporter) {
		if (teleporter.isCoolingDown()) {
			teleporter.incrementCooldown();
		}
	}

	public boolean hasCrystal() {
		return !getCrystal().isEmpty();
	}

	public boolean isInDimension(Entity entity) {
		if (getCrystal().isEmpty())
			return false;

		NbtCompound nbt = getCrystal().getNbt();
		return TeleportCrystalItem.getDimensionKey(nbt).equals(entity.world.getRegistryKey());
	}

	public ItemStack getCrystal() {
		return crystal;
	}

	public void setCrystal(ItemStack crystal) {
		this.crystal = crystal;
		markDirty();
		if (getWorld() != null) {
			BlockState state = getWorld().getBlockState(getPos());
			getWorld().updateListeners(getPos(), state, state, 3);
		}
	}

	public BlockPos getTeleportPos() {
		if (!hasCrystal())
			return null;

		NbtCompound nbt = this.getCrystalNbt();
		return TeleportCrystalItem.getPosition(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);

		if (nbt.contains("crystal")) {
			this.setCrystal(ItemStack.fromNbt(nbt.getCompound("crystal")));
		} else {
			this.setCrystal(ItemStack.EMPTY);
		}
		if (nbt.contains("cooldown")) {
			this.setCooldown(nbt.getInt("cooldown"));
		} else {
			this.setCooldown(0);
		}
	}


	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);

		if (!crystal.isEmpty()) {
			nbt.put("crystal", this.crystal.writeNbt(new NbtCompound()));
		}
		nbt.putInt("cooldown", cooldown);
		return nbt;
	}

	public boolean isCoolingDown() {
		return getCooldown() > 0;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public void incrementCooldown() {
		this.setCooldown(this.getCooldown() - 1);
	}

	public NbtCompound getCrystalNbt() {
		if (!hasCrystal())
			return null;

		return getCrystal().getNbt();
	}
}
