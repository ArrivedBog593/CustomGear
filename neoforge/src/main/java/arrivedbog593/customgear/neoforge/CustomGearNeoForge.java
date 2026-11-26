package arrivedbog593.customgear.neoforge;

import arrivedbog593.customgear.Constants;
import arrivedbog593.customgear.CustomGear;
import arrivedbog593.customgear.commands.CustomGearCommandHandler;
import arrivedbog593.customgear.events.*;
import arrivedbog593.customgear.items.containers.CustomContainerBlock;
import arrivedbog593.customgear.items.containers.CustomContainerBlockEntity;
import arrivedbog593.customgear.neoforge.client.NeoForgeClient;
import arrivedbog593.customgear.platform.registration.RegistryEntry;
import arrivedbog593.customgear.registry.ContainerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

/**
 * NeoForge finds this class by its @Mod annotation. Loads the content through
 * common/, then wires NeoForge's events to the mod's handlers.
 */
@Mod(Constants.MOD_ID)
public class CustomGearNeoForge {

    /** RecipeManager of the datapack load in progress, until its tags are bound. */
    private static volatile RecipeManager loadingRecipes;

    public CustomGearNeoForge(IEventBus modBus) {
        CustomGear.init();

        modBus.addListener(AddPackFindersEvent.class, CustomGearNeoForge::onAddPackFinders);
        modBus.addListener(NeoForgeNetworking::registerPayloads);
        modBus.addListener(NeoForgeNetworking::registerConfigurationTasks);
        modBus.addListener(RegisterCapabilitiesEvent.class, CustomGearNeoForge::onRegisterCapabilities);
        if (FMLEnvironment.dist.isClient()) {
            NeoForgeClient.init(modBus);
        }

        IEventBus game = NeoForge.EVENT_BUS;
        game.addListener(RegisterCommandsEvent.class, event ->
                CustomGearCommandHandler.register(event.getDispatcher(), event.getBuildContext()));
        game.addListener(PlayerTickEvent.Post.class, event -> {
            SetBonusHandler.onPlayerTick(event.getEntity());
            HeldEffectHandler.onPlayerTick(event.getEntity());
        });
        game.addListener(PlayerEvent.PlayerLoggedOutEvent.class, event -> {
            SetBonusHandler.onPlayerLoggedOut(event.getEntity());
            HeldEffectHandler.onPlayerLoggedOut(event.getEntity());
        });
        game.addListener(EntityJoinLevelEvent.class, event ->
                ArrowDamageHandler.onEntityJoinLevel(event.getEntity(), event.getLevel()));
        game.addListener(LivingDropsEvent.class, event ->
                MobDropHandler.onLivingDrops(event.getEntity(), event.getSource(),
                        event.isRecentlyHit(), event.getDrops()::add));
        game.addListener(LivingIncomingDamageEvent.class, event ->
                event.setAmount(DamageResistanceHandler.modifyIncomingDamage(
                        event.getEntity(), event.getSource(), event.getAmount())));

        // recipe_patch: AddReloadListenerEvent hands over the new RecipeManager
        // before it is filled; TagsUpdatedEvent (SERVER_DATA_LOAD) fires once
        // the whole load has finished and before the recipes reach players.
        game.addListener(AddReloadListenerEvent.class, event ->
                loadingRecipes = event.getServerResources().getRecipeManager());
        game.addListener(TagsUpdatedEvent.class, event -> {
            if (event.getUpdateCause() != TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) return;
            RecipeManager recipes = loadingRecipes;
            loadingRecipes = null;
            if (recipes != null) RecipePatchHandler.onDataLoaded(recipes, event.getRegistryAccess());
        });
    }

    /**
     * Serves the dynamic pack as both client resources (textures, models,
     * lang) and server data (recipes, tags, loot).
     */
    private static void onAddPackFinders(AddPackFindersEvent event) {
        PackType type = event.getPackType();
        if (type != PackType.CLIENT_RESOURCES && type != PackType.SERVER_DATA) return;
        event.addRepositorySource(consumer -> {
            Pack pack = CustomGear.createPack(type);
            if (pack != null) consumer.accept(pack);
        });
    }

    /**
     * Item handler for every container block.
     * <p>
     * REDIRECTED FOR DOUBLE CHESTS. The shell half holds no inventory, so
     * without this a hopper feeding that side would push items into a container
     * nobody reads and that gets discarded when the pair splits — silent item
     * loss, not just a cosmetic gap. Jade and comparators go through the same
     * path and were showing the shell as empty for the same reason.
     */
    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        for (RegistryEntry<Block> entry : ContainerRegistry.BLOCKS.getEntries()) {
            event.registerBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, be, side) -> {
                BlockPos target = state.getBlock() instanceof CustomContainerBlock block
                        ? block.inventoryPos(state, pos)
                        : pos;
                return level.getBlockEntity(target) instanceof CustomContainerBlockEntity c
                        ? new InvWrapper(c)
                        : null;
            }, entry.get());
        }
    }
}
