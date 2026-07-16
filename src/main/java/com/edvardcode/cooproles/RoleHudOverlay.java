package com.edvardcode.cooproles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CoopRoles.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RoleHudOverlay {

    public static String currentClientRole = "NONE";
    public static boolean isModActiveClient = false;

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("coop_role", (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {

            if (!isModActiveClient || currentClientRole.equals("NONE")) return;

            Minecraft mc = Minecraft.getInstance();

            String translatedRole = I18n.get("role.cooproles." + currentClientRole.toLowerCase());
            String text = I18n.get("hud.cooproles.role", translatedRole);

            guiGraphics.drawString(mc.font, text, 10, 10, 0xFFD700, true);
        });
    }
}