package arrivedbog593.customgear.fabric;

import arrivedbog593.customgear.CustomGear;
import arrivedbog593.customgear.commands.CustomGearCommandHandler;
import arrivedbog593.customgear.events.ArrowDamageHandler;
import arrivedbog593.customgear.events.HeldEffectHandler;
import arrivedbog593.customgear.events.SetBonusHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

/**
 * The "main" entry point in fabric.mod.json. Loads the content through
 * common/, then wires Fabric API's events to the mod's handlers. Damage, mob
 * drops, recipe patches and the dynamic pack have no Fabric API event and go
 * through this module's mixins instead.
 */
public class CustomGearFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CustomGear.init();
        FabricNetworking.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) ->
                CustomGearCommandHandler.register(dispatcher, buildContext));

        // NeoForge has a per-player tick event; here every player is visited
        // once per server tick, which is the same thing.
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                SetBonusHandler.onPlayerTick(player);
                HeldEffectHandler.onPlayerTick(player);
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            SetBonusHandler.onPlayerLoggedOut(handler.getPlayer());
            HeldEffectHandler.onPlayerLoggedOut(handler.getPlayer());
        });
        ServerEntityEvents.ENTITY_LOAD.register(ArrowDamageHandler::onEntityJoinLevel);
    }
}
