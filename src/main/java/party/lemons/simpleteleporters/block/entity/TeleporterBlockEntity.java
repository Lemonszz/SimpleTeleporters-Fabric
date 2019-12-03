package party.lemons.simpleteleporters.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;

import party.lemons.simpleteleporters.init.SimpleTeleportersBlockEntities;

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
		
		return tags.getInt("dim") == entityIn.dimension.getRawId();
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
	
	@Override
	public void fromTag(CompoundTag compound) {
		super.fromTag(compound);
		
		if (compound.contains("item")) {
			stack = ItemStack.fromTag(compound.getCompound("item"));
		} else {
			stack = ItemStack.EMPTY;
		}
		if (compound.contains("cooldown")) {
			cooldown = compound.getInt("cooldown");
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
