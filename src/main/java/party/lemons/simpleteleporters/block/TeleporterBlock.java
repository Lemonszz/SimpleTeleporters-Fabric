package party.lemons.simpleteleporters.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.minecraft.world.WorldAccess;
import party.lemons.simpleteleporters.block.entity.TeleporterBlockEntity;
import party.lemons.simpleteleporters.init.SimpleTeleportersItems;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Random;

public class TeleporterBlock extends BlockWithEntity {
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape TELE_AABB = VoxelShapes.cuboid(0D, 0.0D, 0D, 1D, 0.3D, 1D);
	public static BooleanProperty ON = BooleanProperty.of("on");
	
	public TeleporterBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState().with(ON, false).with(WATERLOGGED, false));
	}
	
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity.isSneaking() && entity instanceof PlayerEntity) {
			TeleporterBlockEntity teleporter = (TeleporterBlockEntity) world.getBlockEntity(pos);
			if (teleporter == null) return;
			if (!teleporter.isCoolingDown() && teleporter.hasCrystal() && teleporter.isInDimension(entity)) {
				if (entity instanceof ServerPlayerEntity && !world.isClient) {
					BlockPos teleporterPos = teleporter.getTeleportPosition();
					ServerPlayerEntity splayer = (ServerPlayerEntity) entity;
					
					if (teleporterPos == null) {
						splayer.sendMessage(new TranslatableText("text.teleporters.error.unlinked").setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
						return;
					} else if (world.getBlockState(teleporterPos).shouldSuffocate(world, teleporterPos)) {
						splayer.sendMessage(new TranslatableText("text.teleporters.error.invalid_position").setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
						return;
					}
					
					splayer.velocityModified = true;
					
					Vec3d playerPos = new Vec3d(teleporterPos.getX() + 0.5, teleporterPos.getY(), teleporterPos.getZ() + 0.5);
					splayer.networkHandler.teleportRequest(playerPos.getX(), playerPos.getY(), playerPos.getZ(), entity.yaw, entity.pitch, Collections.EMPTY_SET);
					
					splayer.setVelocity(0, 0, 0);
					splayer.velocityDirty = true;
					
					world.playSoundFromEntity(null, splayer, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
					teleporter.setCooldown(10);
					
					BlockEntity down = world.getBlockEntity(teleporterPos.down());
					if (down != null && down instanceof TeleporterBlockEntity) {
						((TeleporterBlockEntity) down).setCooldown(10);
					}
				}
			} else {
				if (!teleporter.hasCrystal()) {
					if (entity instanceof ServerPlayerEntity && !world.isClient) {
						ServerPlayerEntity splayer = (ServerPlayerEntity) entity;
						splayer.sendMessage(new TranslatableText("text.teleporters.error.no_crystal").setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
					}
				} else if (!teleporter.isCoolingDown()) {
					if (entity instanceof ServerPlayerEntity && !world.isClient) {
						ServerPlayerEntity splayer = (ServerPlayerEntity) entity;
						splayer.sendMessage(new TranslatableText("text.teleporters.error.wrong_dimension").setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
					}
				}
			}
		}
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
		TeleporterBlockEntity tele = (TeleporterBlockEntity) world.getBlockEntity(pos);
		if (tele.hasCrystal()) {
			ItemStack crystalStack = tele.getCrystal();
			player.giveItemStack(crystalStack);
			player.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
			tele.setCrystal(ItemStack.EMPTY);
			
			return ActionResult.SUCCESS;
		} else {
			ItemStack stack = player.getStackInHand(hand);
			if (!stack.isEmpty()) {
				if (stack.getItem() == SimpleTeleportersItems.TELE_CRYSTAL && stack.getTag() != null) {
					player.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
					ItemStack setstack = stack.copy();
					setstack.setCount(1);
					tele.setCrystal(setstack);
					stack.decrement(1);
					return ActionResult.CONSUME;
				}
			}
		}
		return ActionResult.PASS;
	}
	
	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		TeleporterBlockEntity tele = (TeleporterBlockEntity) world.getBlockEntity(blockPos);
		ItemScatterer.spawn(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), tele.getCrystal());
		super.onBreak(world, blockPos, blockState, playerEntity);
	}
	
	
	@Override
	public FluidState getFluidState(BlockState var1) {
		return var1.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(var1);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world)); // getTickRate == method_15789?
		}
		
		return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}
	
	@Override
	public VoxelShape getRayTraceShape(BlockState state, BlockView world, BlockPos pos) {
		return TELE_AABB;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return TELE_AABB;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return TELE_AABB;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> st) {
		st.add(ON).add(WATERLOGGED);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new TeleporterBlockEntity();
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState var1) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (state.get(ON)) {
			for (int i = 0; i < 15; i++) {
				world.addParticle(ParticleTypes.PORTAL, pos.getX() + 0.2F + (random.nextFloat() / 2), pos.getY() + 0.4F, pos.getZ() + 0.2F + (random.nextFloat() / 2), 0, random.nextFloat(), 0);    // originally method_8406
			}
		}
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fs = ctx.getWorld().getFluidState(ctx.getBlockPos());
		boolean isWater = fs.getFluid().equals(Fluids.WATER);
		return this.getDefaultState().with(WATERLOGGED, isWater);
	}
}
