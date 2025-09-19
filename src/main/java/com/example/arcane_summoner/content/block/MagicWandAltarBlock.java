package com.example.arcane_summoner.content.block;

import com.example.arcane_summoner.content.block.entity.MagicWandAltarBlockEntity;
import com.example.arcane_summoner.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

//public class MagicWandAltarBlock extends Block implements EntityBlock {
//    public MagicWandAltarBlock(Properties properties) {
//        super(properties);
//    }
//
//    @Override
//    public RenderShape getRenderShape(BlockState state) {
//        return RenderShape.MODEL;
//    }
//
//    @Nullable
//    @Override
//    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
//        return ModBlockEntities.MAGIC_WAND_ALTAR.get().create(pos, state);
//    }
//
//    @Override
//    public InteractionResult use(BlockState state, Level level, BlockPos pos,
//                                 Player player, InteractionHand hand, BlockHitResult hit) {
//        if (!level.isClientSide) {
//            BlockEntity be = level.getBlockEntity(pos);
//            if (be instanceof MagicWandAltarBlockEntity altar) {
//                NetworkHooks.openScreen((ServerPlayer) player, altar, buf -> {
//                    buf.writeBlockPos(pos);
//                });
//            }
//        }
//        return InteractionResult.sidedSuccess(level.isClientSide);
//    }
//}

public class MagicWandAltarBlock extends BaseEntityBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public MagicWandAltarBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        // só permite colocar se tiver espaço em cima
        return level.getBlockState(pos.above()).canBeReplaced(ctx)
                ? this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER)
                : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            DoubleBlockHalf half = state.getValue(HALF);
            BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();

            // Se o bloco de baixo for o que tem a BlockEntity, dropa os itens
            BlockPos basePos = (half == DoubleBlockHalf.LOWER) ? pos : pos.below();
            BlockEntity be = level.getBlockEntity(basePos);
            if (be instanceof MagicWandAltarBlockEntity altar) {
                Containers.dropContents(level, basePos, altar);
                level.removeBlockEntity(basePos);
            }

            // Remove o outro bloco parceiro
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.getBlock() == this) {
                level.destroyBlock(otherPos, false);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            // haste fina
            return Block.box(6, 0, 6, 10, 16, 10);
        } else {
            // topo quadrado (glowstone-like)
            return Block.box(2, 0, 2, 14, 14, 14);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER
                ? new MagicWandAltarBlockEntity(pos, state)
                : null; // só o bloco de baixo tem entidade
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockPos basePos = pos;
            // Se clicou no topo, redireciona para o de baixo
            if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
                basePos = pos.below();
            }

            BlockEntity be = level.getBlockEntity(basePos);
            if (be instanceof MagicWandAltarBlockEntity altar) {
                BlockPos finalPos = basePos;
                NetworkHooks.openScreen((ServerPlayer) player, altar, buf -> {
                    buf.writeBlockPos(finalPos);
                });
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        // Sempre retorna o item do bloco base, independente de clicar em cima ou
        // embaixo
        return new ItemStack(this);
    }

}
