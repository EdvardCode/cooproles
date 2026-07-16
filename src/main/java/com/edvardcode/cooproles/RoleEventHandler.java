package com.edvardcode.cooproles;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RoleEventHandler {

    public static String getPlayerRole(Player player) {
        if (!player.getPersistentData().contains("TeamRole")) return "NONE";
        return player.getPersistentData().getString("TeamRole");
    }

    public static MutableComponent getRoleComponent(String role) {
        return Component.translatable("role.cooproles." + role.toLowerCase());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player original = event.getOriginal();
            Player newPlayer = event.getEntity();
            if (original.getPersistentData().contains("TeamRole")) {
                newPlayer.getPersistentData().putString("TeamRole", original.getPersistentData().getString("TeamRole"));
            }
        }
    }

    private static boolean shouldBypass(Player player) {
        if (player.isCreative()) return true;
        if (!CoopRoles.isModActive) return true;
        if (getPlayerRole(player).equals("NONE")) return true;
        return false;
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (shouldBypass(player)) return;

        if (!getPlayerRole(player).equals("MINER")) {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable("warning.cooproles.miner").withStyle(ChatFormatting.RED), true);
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (shouldBypass(player)) return;

            if (!getPlayerRole(player).equals("BUILDER")) {
                event.setCanceled(true);
                player.displayClientMessage(Component.translatable("warning.cooproles.builder").withStyle(ChatFormatting.RED), true);
            }
        }
    }

    @SubscribeEvent
    public static void onInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (shouldBypass(player)) return;

        var block = event.getLevel().getBlockState(event.getPos()).getBlock();
        boolean isCraftingBlock = block == Blocks.CRAFTING_TABLE || block == Blocks.FURNACE || block == Blocks.ANVIL;

        if (isCraftingBlock && !getPlayerRole(player).equals("CRAFTER")) {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable("warning.cooproles.crafter").withStyle(ChatFormatting.RED), true);
        }
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (shouldBypass(player)) return;

        if (!getPlayerRole(player).equals("WARRIOR")) {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable("warning.cooproles.warrior").withStyle(ChatFormatting.RED), true);
        }
    }
}