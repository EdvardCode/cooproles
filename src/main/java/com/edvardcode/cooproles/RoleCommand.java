package com.edvardcode.cooproles;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CoopRoles.MODID)
public class RoleCommand {

    private static final String[] AVAILABLE_ROLES = {"MINER", "BUILDER", "CRAFTER", "WARRIOR", "NONE"};

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("cooproles");

        command.then(Commands.literal("on").requires(source -> source.hasPermission(2)).executes(context -> {
            CoopRoles.isModActive = true;
            context.getSource().sendSuccess(() -> Component.translatable("command.cooproles.on").withStyle(ChatFormatting.GREEN), true);
            ModMessages.sendToAll(new SyncRolePacket("NONE", true));
            return 1;
        }));

        command.then(Commands.literal("off").requires(source -> source.hasPermission(2)).executes(context -> {
            CoopRoles.isModActive = false;
            context.getSource().sendSuccess(() -> Component.translatable("command.cooproles.off").withStyle(ChatFormatting.RED), true);
            ModMessages.sendToAll(new SyncRolePacket("NONE", false));
            return 1;
        }));

        command.then(Commands.literal("role").executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            String role = RoleEventHandler.getPlayerRole(player);

            player.sendSystemMessage(Component.translatable("command.cooproles.your_role",
                    RoleEventHandler.getRoleComponent(role).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.AQUA));
            return 1;
        }));

        command.then(Commands.literal("setrole").requires(source -> source.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("role", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(AVAILABLE_ROLES, builder))
                                .executes(context -> {
                                    ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                    String roleName = StringArgumentType.getString(context, "role").toUpperCase();

                                    target.getPersistentData().putString("TeamRole", roleName);
                                    ModMessages.sendToPlayer(new SyncRolePacket(roleName, CoopRoles.isModActive), target);

                                    context.getSource().sendSuccess(() ->
                                            Component.translatable("command.cooproles.set_role_admin",
                                                    Component.literal(target.getName().getString()),
                                                    RoleEventHandler.getRoleComponent(roleName)).withStyle(ChatFormatting.GREEN), true);

                                    target.sendSystemMessage(Component.translatable("command.cooproles.set_role_player",
                                            RoleEventHandler.getRoleComponent(roleName).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GREEN));

                                    return 1;
                                })
                        )
                ));

        event.getDispatcher().register(command);
    }
}