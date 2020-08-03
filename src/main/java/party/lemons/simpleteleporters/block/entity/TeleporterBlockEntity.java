package party.lemons.simpleteleporters.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;

import net.minecraft.world.World;
import party.lemons.simpleteleporters.init.SimpleTeleportersBlockEntities;
import party.lemons.simpleteleporters.item.BaseTeleportCrystalItem;
import party.lemons.simpleteleporters.item.DimensionalTeleportCrystalItem;
import party.lemons.simpleteleporters.item.TeleportCrystalItem;

public class TeleporterBlockEntity extends BlockEntity implements Tickable {
	private ItemStack stack = ItemStack.EMPTY;
	private int cooldown = 0;

	public TeleporterBlockEntity() {
		super(SimpleTeleportersBlockEntities.TELE_BE);
	}

	@Override
	public void tick() {
		if (isCoolingDown()) {
			this.setCooldown(this.getCooldown() - 1);
		}
	}

	public boolean hasCrystal() {
		return !getCrystal().isEmpty();
	}

	public boolean isInDimension(Entity entityIn) {
		if (getCrystal().isEmpty())
			return false;

		CompoundTag tags = getCrystal().getTag();
		if (tags == null)
			return false;

		return tags.getString("dim").equals(entityIn.world.getDimensionRegistryKey().getValue().toString());
	}

	public boolean canTeleportTo(Entity entityIn) {
		if (getCrystal().isEmpty())
			return false;

		Item crystal = getCrystal().getItem();
		if (crystal instanceof TeleportCrystalItem)
			return isInDimension(entityIn);

		return crystal instanceof DimensionalTeleportCrystalItem;
	}

	public ItemStack getCrystal() {
		return stack;
	}

	public void setCrystal(ItemStack stack) {
		this.stack = stack;
		markDirty();
		if (getWorld() != null) {
			BlockState state = getWorld().getBlockState(getPos());
			getWorld().updateListeners(getPos(), state, state, 3);
		}
	}

	public BlockPos getTeleportPosition() {
		if (!hasCrystal())
			return null;

		CompoundTag tags = getCrystal().getTag();
		if (tags == null)
			return null;

		int xx = tags.getInt("x");
		int yy = tags.getInt("y");
		int zz = tags.getInt("z");

		return new BlockPos(xx, yy, zz);
	}

	public String getTeleportWorld() {
		if (!hasCrystal())
			return null;

		CompoundTag tags = getCrystal().getTag();
		if (tags == null)
			return null;

		return tags.getString("dim");
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		super.fromTag(state, tag);

		if (tag.contains("item")) {
			stack = ItemStack.fromTag(tag.getCompound("item"));
		} else {
			stack = ItemStack.EMPTY;
		}
		if (tag.contains("cooldown")) {
			cooldown = tag.getInt("cooldown");
		} else {
			cooldown = 0;
		}
	}


	@Override
	public CompoundTag toTag(CompoundTag compound) {
		compound = super.toTag(compound);

		if (!stack.isEmpty()) {
			CompoundTag tagCompound = stack.toTag(new CompoundTag());
			compound.put("item", tagCompound);
		}
		compound.putInt("cooldown", cooldown);
		return compound;
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
}
