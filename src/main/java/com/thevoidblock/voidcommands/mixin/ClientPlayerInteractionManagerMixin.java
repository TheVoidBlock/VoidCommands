package com.thevoidblock.voidcommands.mixin;

import com.thevoidblock.voidcommands.TempConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow
    private void syncSelectedSlot() {}

    @Shadow
    private ActionResult interactBlockInternal(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) { return null; }

    @Shadow
    private void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator) {}

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
            method = "interactBlock",
            at = @At("HEAD"),
            cancellable = true
    )

    // This is for block placing, it checks if ghost placing is enabled in the config and chooses if it should send the packet
    public void interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> info) {
        this.syncSelectedSlot();
        if (!this.client.world.getWorldBorder().contains(hitResult.getBlockPos())) {
            info.setReturnValue(ActionResult.FAIL);
            return;
        } else {
            MutableObject<ActionResult> mutableObject = new MutableObject();
            if(TempConfig.ghostPlacement)
                mutableObject.setValue(this.interactBlockInternal(player, hand, hitResult));
            else
                this.sendSequencedPacket(this.client.world, (sequence) -> {
                    mutableObject.setValue(this.interactBlockInternal(player, hand, hitResult));
                    return new PlayerInteractBlockC2SPacket(hand, hitResult, sequence);
                });
            info.setReturnValue((ActionResult) mutableObject.getValue());
            return;
        }
    }
}
