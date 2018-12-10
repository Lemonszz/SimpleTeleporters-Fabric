package party.lemons.simpleteleporters.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.RenderTypeBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.network.packet.PlayerPositionLookClientPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sortme.ItemScatterer;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.InventoryUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Facing;
import net.minecraft.util.shape.VoxelShapeContainer;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.Parameters;
import party.lemons.simpleteleporters.block.entity.TeleporterBlockEntity;
import party.lemons.simpleteleporters.init.SimpleTeleportersItems;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class TeleporterBlock extends BlockWithEntity
{
	public static BooleanProperty ON = BooleanProperty.create("on");
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShapeContainer TELE_AABB = VoxelShapes.cube(0D, 0.0D, 0D, 1D, 0.3D, 1D);


	public TeleporterBlock(Settings settings)
	{
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(ON, false).with(WATERLOGGED, false));
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if(entity.isSneaking() && entity instanceof PlayerEntity)
		{
			TeleporterBlockEntity teleporter = (TeleporterBlockEntity) world.getBlockEntity(pos);
			if(teleporter.hasCrystal() && teleporter.isInDimension(entity))
			{
				entity.playSoundAtEntity(Sounds.ENTITY_ENDERMAN_TELEPORT, 1, 1);
				if(!world.isRemote)
				{
					BlockPos teleporterPos = teleporter.getTeleportPosition();
					ServerPlayerEntity splayer = (ServerPlayerEntity) entity;

					splayer.networkHandler.method_14360(teleporterPos.getX() + 0.5, teleporterPos.getY(), teleporterPos.getZ() + 0.5, entity.yaw, entity.pitch, EnumSet.noneOf(PlayerPositionLookClientPacket.Flag.class));
					splayer.velocityY = 0.5F;
					splayer.velocityDirty = true;
				}
				entity.playSoundAtEntity(Sounds.ENTITY_ENDERMAN_TELEPORT, 1, 1);
			}

		}
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, Facing facing, float v, float v1, float v2)
	{
		TeleporterBlockEntity tele = (TeleporterBlockEntity) world.getBlockEntity(pos);
		if(tele.hasCrystal())
		{
			ItemStack crystalStack = tele.getCrystal();
			playerEntity.inventory.insertStack(crystalStack);
			playerEntity.playSoundAtEntity(Sounds.ENTITY_ARROW_SHOOT, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

			world.setBlockState(pos, state.with(ON, false));
			tele.setCrystal(ItemStack.EMPTY);

			return true;
		}
		else
		{
			ItemStack stack = playerEntity.getStackInHand(hand);
			if(!stack.isEmpty())
			{
				if(stack.getItem() == SimpleTeleportersItems.TELE_CRYSTAL && stack.getTag() != null)
				{
					playerEntity.playSoundAtEntity(Sounds.ENTITY_ARROW_SHOOT, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
					world.setBlockState(pos, state.with(ON, true));
					ItemStack setstack = stack.copy();
					setstack.setAmount(1);
					tele.setCrystal(setstack);
					stack.subtractAmount(1);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity)
	{
		TeleporterBlockEntity tele = (TeleporterBlockEntity) world.getBlockEntity(blockPos);
		ItemScatterer.spawn(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), tele.getCrystal());
		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	public FluidState getFluidState(BlockState var1)
	{
		return var1.get(WATERLOGGED) ? Fluids.WATER.method_15729(false) : super.getFluidState(var1);
	}

	@Override
	public BlockState getRenderingState(BlockState state, Facing facing, BlockState anotherState, IWorld world, BlockPos pos, BlockPos anotherPos)
	{
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.method_15789(world));
		}

		return super.getRenderingState(state, facing, anotherState, world, pos, anotherPos);
	}

	@Override
	public VoxelShapeContainer getBoundingShape(BlockState state, BlockView world, BlockPos pos)
	{
		return TELE_AABB;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> st)
	{
		st.with(ON).with(WATERLOGGED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView)
	{
		return new TeleporterBlockEntity();
	}

	public RenderTypeBlock getRenderType(BlockState var1) {
		return RenderTypeBlock.MODEL;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
	{
		if(state.get(ON))
		{
			for(int i = 0; i < 15; i++)
			{
				world.method_8406(ParticleTypes.PORTAL, pos.getX() + 0.2F + (random.nextFloat()/2), pos.getY() + 0.4F, pos.getZ() + 0.2F + (random.nextFloat()/2), 0, random.nextFloat(), 0);
			}
		}
	}

	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		FluidState fs = ctx.getWorld().getFluidState(ctx.getPos());
		boolean isWater = fs.getFluid() == Fluids.WATER;
		return this.getDefaultState().with(WATERLOGGED, isWater);
	}
}
