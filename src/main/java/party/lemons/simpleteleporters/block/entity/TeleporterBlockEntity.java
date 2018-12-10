package party.lemons.simpleteleporters.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import party.lemons.simpleteleporters.init.SimpleTeleportersBlockEntities;

public class TeleporterBlockEntity extends BlockEntity
{
	private ItemStack stack = ItemStack.EMPTY;

	public TeleporterBlockEntity()
	{
		super(SimpleTeleportersBlockEntities.TELE_BE);
	}

	public boolean hasCrystal()
	{
		return !getCrystal().isEmpty();
	}

	public boolean isInDimension(Entity entityIn)
	{
		if(getCrystal().isEmpty())
			return false;

		CompoundTag tags = getCrystal().getTag();
		if(tags == null)
			return false;

		return tags.getInt("dim") == entityIn.dimension.getRawId();
	}

	public ItemStack getCrystal()
	{
		return stack;
	}

	public void setCrystal(ItemStack stack)
	{
		this.stack = stack;
		markDirty();
		if (getWorld() != null)
		{
			BlockState state = getWorld().getBlockState(getPos());
			getWorld().updateListeners(getPos(), state, state, 3);
		}
	}

	public BlockPos getTeleportPosition()
	{
		if(getCrystal().isEmpty())
			return new BlockPos(0,0,0);

		CompoundTag tags = getCrystal().getTag();
		if(tags == null)
			return new BlockPos(0,0,0);

		int xx = tags.getInt("x");
		int yy = tags.getInt("y");
		int zz = tags.getInt("z");

		return new BlockPos(xx,yy, zz);
	}

	@Override
	public void fromTag(CompoundTag compound)
	{
		super.fromTag(compound);

		if (compound.containsKey("item"))
		{
			stack =  ItemStack.fromTag(compound.getCompound("item"));
		}
		else
		{
			stack = ItemStack.EMPTY;
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compound)
	{
		compound = super.toTag(compound);

		if (!stack.isEmpty())
		{
			CompoundTag tagCompound = stack.toTag(new CompoundTag());
			compound.put("item", tagCompound);
		}
		return compound;
	}
}
