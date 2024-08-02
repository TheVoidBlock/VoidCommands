package com.thevoidblock.voidcommands.mixin;

import com.thevoidblock.voidcommands.TempConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow
    private ActionResult interactBlockInternal(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) { return null; }

    @Inject(method = "interactBlock",
            at = @At(value = "INVOKE",
                    target = "Lorg/apache/commons/lang3/mutable/MutableObject;<init>()V",
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void injectAfterMutableObject(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if(TempConfig.ghostPlacement) {
            cir.setReturnValue(this.interactBlockInternal(player, hand, hitResult));
            return;
        }
    }
}
