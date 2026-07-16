package com.edvardcode.cooproles;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    public static SimpleChannel INSTANCE;

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(CoopRoles.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;
        net.messageBuilder(SyncRolePacket.class, 0)
                .decoder(SyncRolePacket::new)
                .encoder(SyncRolePacket::toBytes)
                .consumerMainThread(SyncRolePacket::handle)
                .add();
    }

    public static void sendToPlayer(SyncRolePacket message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToAll(SyncRolePacket message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}