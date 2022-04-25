package io.github.mdrscorp.muod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.mdrscorp.muod.command.Data.HomeInfo;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class HomeCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("home")
            .then(CommandManager.argument("name", StringArgumentType.word())
                .suggests(new HomeSuggestionProvider())
                    .executes(ctx -> tpHome(ctx.getSource(), StringArgumentType.getString(ctx, "name")))));

        dispatcher.register(CommandManager.literal("sethome")
            .then(CommandManager.argument("name", StringArgumentType.word())
                .executes(ctx -> setHome(ctx.getSource(), StringArgumentType.getString(ctx, "name")))));

        dispatcher.register(CommandManager.literal("delhome")
            .then(CommandManager.argument("name", StringArgumentType.word())
                .suggests(new HomeSuggestionProvider())
                    .executes(ctx -> delHome(ctx.getSource(), StringArgumentType.getString(ctx, "name")))));
    }

    public static int tpHome(ServerCommandSource source, String name) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        HomeInfo home = Data.DATA.get(player.getUuid().toString()).getHome(source, name);
        if (home != null)
            player.teleport(player.server.getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier(home.dim))), home.x, home.y, home.z, player.getYaw(), player.getPitch());
        return 1;
    }

    public static int setHome(ServerCommandSource source, String name) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        Data.DATA.get(player.getUuid().toString())
            .setHome(source, name, player.getBlockX(), (int) Math.round(player.getY()), player.getBlockZ(), player.getWorld().getRegistryKey().getValue().toString());
        return 1;
    }

    public static int delHome(ServerCommandSource source, String name) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        Data.DATA.get(player.getUuid().toString())
            .delHome(source, name);
        return 1;
    }
}
