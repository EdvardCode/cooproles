package com.edvardcode.cooproles;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class SyncRolePacket {
    public final String role;
    public final boolean isModActive;

    public SyncRolePacket(String role, boolean isModActive) {
        this.role = role;
        this.isModActive = isModActive;
    }

    public SyncRolePacket(FriendlyByteBuf buf) {
        this.role = buf.readUtf();
        this.isModActive = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(role);
        buf.writeBoolean(isModActive);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            RoleHudOverlay.currentClientRole = this.role;
            RoleHudOverlay.isModActiveClient = this.isModActive;
        });
        context.setPacketHandled(true);
        return true;
    }
}