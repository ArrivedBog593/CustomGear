package com.github.arrivedbog593.items.fluids;

import com.github.arrivedbog593.data.FluidData;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.function.Supplier;

/**
 * Source and Flowing fluid pair generated from FluidData.
 * <p>
 * NeoForge requires two separate BaseFlowingFluid instances:
 *  - Source → static fluid block
 *  - Flowing → moving fluid
 * <p>
 * Both share the same FluidData and are linked via
 * BaseFlowingFluid.Properties in FluidRegistry.
 */
public class CustomFluid {

    // ── Source (still block) ─────────────────────────────────────────────────

    public static class Source extends BaseFlowingFluid.Source {

        public Source(BaseFlowingFluid.Properties properties) {
            super(properties);
        }

    }

    // ── Flowing (moving fluid) ───────────────────────────────────────────────

    public static class Flowing extends BaseFlowingFluid.Flowing {

        public Flowing(BaseFlowingFluid.Properties properties) {
            super(properties);
        }

    }

    // ── Fluid block (with optional light emission) ───────────────────────────

    public static LiquidBlock createBlock(Supplier<? extends FlowingFluid> fluid, FluidData data) {
        BlockBehaviour.Properties props = BlockBehaviour.Properties.of()
                .noCollission()
                .strength(100f)
                .noLootTable()
                .pushReaction(PushReaction.DESTROY);

        if (data.lightLevel > 0) {
            int clamped = Math.clamp(data.lightLevel, 0, 15);
            props = props.lightLevel(state -> clamped);
        }

        return new LiquidBlock(fluid.get(), props);
    }
}