package party.lemons.simpleteleporters.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import party.lemons.simpleteleporters.init.SimpleTeleportersBlockEntities;

public class TeleporterBlockEntity extends BlockEntity {
	private ItemStack stack = ItemStack.EMPTY;
	private int cooldown = 0;
	
	public TeleporterBlockEntity(BlockPos pos, BlockState state) {
		super(SimpleTeleportersBlockEntities.TELE_BE, pos, state);
	}

	public static void serverTick(World world, BlockPos pos, BlockState state, TeleporterBlockEntity blockEntity) {
		if (blockEntity.isCoolingDown()) {
			blockEntity.setCooldown(blockEntity.getCooldown() - 1);
		}
	}
	
	public boolean hasCrystal() {
		return !getCrystal().isEmpty();
	}
	
	public boolean isInDimension(Entity entityIn) {
		if (getCrystal().isEmpty())
			return false;
		
		NbtCompound tags = getCrystal().getTag();
		if (tags == null)
			return false;
		
		return tags.getString("dim").equals(entityIn.world.getRegistryKey().getValue().toString());
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
		
		NbtCompound tags = getCrystal().getTag();
		if (tags == null)
			return null;
		
		int xx = tags.getInt("x");
		int yy = tags.getInt("y");
		int zz = tags.getInt("z");
		
		return new BlockPos(xx, yy, zz);
	}

	@Override
	public void readNbt(NbtCompound tag)
	{
		if (tag.contains("item")) {
			stack = ItemStack.fromNbt(tag.getCompound("item"));
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
	public NbtCompound writeNbt(NbtCompound compound) {
		compound = super.writeNbt(compound);
		
		if (!stack.isEmpty()) {
			NbtCompound tagCompound = stack.writeNbt(new NbtCompound());
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
