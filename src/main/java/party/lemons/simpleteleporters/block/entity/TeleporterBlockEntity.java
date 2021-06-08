package party.lemons.simpleteleporters.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;

import party.lemons.simpleteleporters.block.TeleporterBlock;
import party.lemons.simpleteleporters.init.SimpleTeleportersBlockEntities;
import party.lemons.simpleteleporters.init.SimpleTeleportersItems;

public class TeleporterBlockEntity extends BlockEntity implements Inventory, Tickable {
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
	
	public ItemStack getCrystal() {
		return stack;
	}
	
	public void setCrystal(ItemStack stack) {
		this.stack = stack;
		markDirty();
		if (getWorld() != null) {
			BlockPos pos = getPos();
			BlockState state = getWorld().getBlockState(pos);
			getWorld().setBlockState(pos, state.with(TeleporterBlock.ON, hasCrystal()));
			getWorld().updateListeners(pos, state, state, 3);
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

	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		return getCrystal();
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack out = getCrystal();
		setCrystal(ItemStack.EMPTY);
		return out;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return removeStack(0, 1);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		setCrystal(stack);
	}

	@Override
	public int getMaxCountPerStack() {
		return 1;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if (!stack.isEmpty() && hasCrystal() || slot > 0) {
			return false;
		}
		return stack.getItem() == SimpleTeleportersItems.TELE_CRYSTAL && stack.getTag() != null;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {
		this.setCrystal(ItemStack.EMPTY);
	}
}
