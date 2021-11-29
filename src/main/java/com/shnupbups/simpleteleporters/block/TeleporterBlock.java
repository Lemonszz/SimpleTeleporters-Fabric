package com.shnupbups.simpleteleporters.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
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
import net.minecraft.world.event.GameEvent;

import com.shnupbups.simpleteleporters.block.entity.TeleporterBlockEntity;
import com.shnupbups.simpleteleporters.init.SimpleTeleportersBlockEntities;
import com.shnupbups.simpleteleporters.init.SimpleTeleportersItems;
import com.shnupbups.simpleteleporters.init.SimpleTeleportersSoundEvents;
import com.shnupbups.simpleteleporters.item.TeleportCrystalItem;

public class TeleporterBlock extends BlockWithEntity {
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final BooleanProperty ON = BooleanProperty.of("on");

	protected static final VoxelShape SHAPE = VoxelShapes.cuboid(0D, 0.0D, 0D, 1D, 0.38D, 1D);

	public TeleporterBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState().with(ON, false).with(WATERLOGGED, false));
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(state, world, pos, entity);
		if (entity instanceof ServerPlayerEntity player && !world.isClient()) {
			if (entity.isSneaking()) {
				if (world.getBlockEntity(pos) instanceof TeleporterBlockEntity teleporter) {
					if (!teleporter.hasCrystal()) {
						player.sendMessage(new TranslatableText("text.simpleteleporters.error.no_crystal").setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
					} else if (!teleporter.isInDimension(entity)) {
						player.sendMessage(new TranslatableText("text.simpleteleporters.error.wrong_dimension").setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
					} else if (!teleporter.isCoolingDown()) {
						BlockPos teleportPos = teleporter.getTeleportPos();

						if (teleportPos == null) {
							player.sendMessage(new TranslatableText("text.simpleteleporters.error.unlinked_teleporter").setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
						} else if (world.getBlockState(teleportPos).shouldSuffocate(world, teleportPos)) {
							player.sendMessage(new TranslatableText("text.simpleteleporters.error.invalid_position").setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
						} else {
							player.velocityModified = true;

							Vec3d playerPos = new Vec3d(teleportPos.getX() + 0.5, teleportPos.getY(), teleportPos.getZ() + 0.5);
							player.networkHandler.requestTeleport(playerPos.getX(), playerPos.getY(), playerPos.getZ(), entity.getYaw(), entity.getPitch());

							player.setVelocity(0, 0, 0);
							player.velocityDirty = true;

							world.playSoundFromEntity(null, player, SimpleTeleportersSoundEvents.TELEPORTER_TELEPORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
							teleporter.setCooldown(10);

							BlockEntity down = world.getBlockEntity(teleportPos.down());
							if (down instanceof TeleporterBlockEntity tpDown) {
								tpDown.setCooldown(10);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
		if (world.getBlockEntity(pos) instanceof TeleporterBlockEntity teleporter) {
			if (teleporter.hasCrystal()) {
				ItemStack crystalStack = teleporter.getCrystal();

				if(!player.giveItemStack(crystalStack)) {
					player.dropItem(crystalStack, false);
				}

				player.playSound(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_REMOVED, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

				world.setBlockState(pos, state.with(ON, false));
				teleporter.setCrystal(ItemStack.EMPTY);

				world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
				return ActionResult.SUCCESS;
			} else {
				ItemStack stack = player.getStackInHand(hand);
				if (!stack.isEmpty()) {
					if (stack.getItem() == SimpleTeleportersItems.ENDER_SHARD) {
						if (TeleportCrystalItem.hasPosition(stack.getNbt())) {
							player.playSound(SimpleTeleportersSoundEvents.TELEPORTER_CRYSTAL_INSERTED, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
							world.setBlockState(pos, state.with(ON, true));
							ItemStack crystal = stack.copy();
							crystal.setCount(1);
							teleporter.setCrystal(crystal);
							stack.decrement(1);

							return ActionResult.SUCCESS;
						} else {
							player.sendMessage(new TranslatableText("text.simpleteleporters.error.unlinked_shard").setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
						}
					}
				}
			}
		}
		return ActionResult.PASS;
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (world.getBlockEntity(pos) instanceof TeleporterBlockEntity teleporter && teleporter.hasCrystal()) {
			ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), teleporter.getCrystal());
		}
		super.onBreak(world, pos, state, player);
	}


	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(ON).add(WATERLOGGED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new TeleporterBlockEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
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
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		boolean isWater = fluidState.getFluid().equals(Fluids.WATER);
		return this.getDefaultState().with(WATERLOGGED, isWater);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, SimpleTeleportersBlockEntities.TELEPORTER, world.isClient() ? null : TeleporterBlockEntity::tick);
	}
}
