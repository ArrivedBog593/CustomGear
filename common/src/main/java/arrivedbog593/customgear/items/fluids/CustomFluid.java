package arrivedbog593.customgear.items.fluids;

import arrivedbog593.customgear.data.FluidData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * The source and flowing pair generated from one FluidData.
 * <p>
 * Built on vanilla's FlowingFluid, so it runs on every loader. It behaves as
 * NeoForge's BaseFlowingFluid: the same tick rate, spread and drop-off, no
 * infinite sources. NeoForge additionally needs each
 * fluid to report a FluidType, which its loader module adds by subclassing
 * {@link Source} and {@link Flowing} (see IFluidHelper).
 */
public abstract class CustomFluid extends FlowingFluid {

    protected final Properties properties;

    protected CustomFluid(Properties properties) {
        this.properties = properties;
    }

    public Properties properties() {
        return properties;
    }

    @Override
    public @NotNull Fluid getFlowing() {
        return properties.flowing.get();
    }

    @Override
    public @NotNull Fluid getSource() {
        return properties.source.get();
    }

    @Override
    protected boolean canConvertToSource(@NotNull Level level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(@NotNull LevelAccessor level, @NotNull BlockPos pos,
                                         BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockEntity);
    }

    @Override
    protected int getSlopeFindDistance(@NotNull LevelReader level) {
        return 4;
    }

    @Override
    protected int getDropOff(@NotNull LevelReader level) {
        return properties.levelDecreasePerBlock;
    }

    @Override
    public @NotNull Item getBucket() {
        return properties.bucket != null ? properties.bucket.get() : Items.AIR;
    }

    @Override
    protected boolean canBeReplacedWith(@NotNull FluidState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull Fluid fluid,
                                        @NotNull Direction direction) {
        return direction == Direction.DOWN && !isSame(fluid);
    }

    @Override
    public int getTickDelay(@NotNull LevelReader level) {
        return properties.tickRate;
    }

    @Override
    protected float getExplosionResistance() {
        return 1.0F;
    }

    @Override
    protected @NotNull BlockState createLegacyBlock(@NotNull FluidState state) {
        return properties.block.get().defaultBlockState()
                .setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSame(@NotNull Fluid fluid) {
        return fluid == getSource() || fluid == getFlowing();
    }

    // ── Source (still block) ─────────────────────────────────────────────────

    public static class Source extends CustomFluid {

        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public int getAmount(@NotNull FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(@NotNull FluidState state) {
            return true;
        }
    }

    // ── Flowing (moving fluid) ───────────────────────────────────────────────

    public static class Flowing extends CustomFluid {

        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        protected void createFluidStateDefinition(
                @NotNull StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(@NotNull FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(@NotNull FluidState state) {
            return false;
        }
    }

    // ── Shared definition of one fluid ───────────────────────────────────────

    /**
     * Everything the source and the flowing variant share. The links to the
     * other registered objects are suppliers because they reference each other:
     * the source needs its block and bucket, which need the source.
     */
    public static final class Properties {

        public final FluidData data;
        public final Supplier<? extends Fluid> source;
        public final Supplier<? extends Fluid> flowing;
        public final Supplier<? extends LiquidBlock> block;
        public final Supplier<? extends Item> bucket;
        public final int tickRate;
        /**
         * Controls the spread distance: water uses 1 (reaches 8 blocks), lava
         * uses 4 (reaches 4 blocks). levelDecreasePerBlock = 8 - spreadDistance,
         * clamped to 1-7.
         */
        public final int levelDecreasePerBlock;

        /** Whatever the loader attaches to the fluid (NeoForge: its FluidType). */
        public Supplier<?> platformData = () -> null;

        public Properties(FluidData data,
                          Supplier<? extends Fluid> source,
                          Supplier<? extends Fluid> flowing,
                          Supplier<? extends LiquidBlock> block,
                          Supplier<? extends Item> bucket) {
            this.data = data;
            this.source = source;
            this.flowing = flowing;
            this.block = block;
            this.bucket = bucket;
            this.tickRate = Math.max(1, data.tickRate);
            this.levelDecreasePerBlock = Math.clamp(8 - data.spreadDistance, 1, 7);
        }
    }

    // ── Fluid block (with optional light emission) ───────────────────────────

    public static LiquidBlock createBlock(Supplier<? extends FlowingFluid> fluid, FluidData data) {
        BlockBehaviour.Properties props = BlockBehaviour.Properties.of()
                .replaceable()      // place blocks into the fluid, like water/lava
                .liquid()           // liquid material semantics
                .noCollission()
                .strength(100f)
                .noLootTable()
                .pushReaction(PushReaction.DESTROY);

        if (data.lightLevel > 0) {
            int clamped = Math.clamp(data.lightLevel, 0, 15);
            props = props.lightLevel(state -> clamped);
        }

        return new CustomLiquidBlock(fluid.get(), props, data.id);
    }
}
