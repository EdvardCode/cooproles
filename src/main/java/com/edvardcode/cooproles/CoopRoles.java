package com.edvardcode.cooproles;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(CoopRoles.MODID)
public class CoopRoles {
    public static final String MODID = "cooproles";

    public static boolean isModActive = false;

    public CoopRoles() {
        MinecraftForge.EVENT_BUS.register(RoleEventHandler.class);
        ModMessages.register();
    }
}