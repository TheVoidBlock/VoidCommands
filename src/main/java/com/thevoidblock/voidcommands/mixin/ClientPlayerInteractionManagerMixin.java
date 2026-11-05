package com.thevoidblock.voidcommands.mixin;

import com.thevoidblock.voidcommands.TempConfig;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Shadow
    protected abstract InteractionResult performUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit);

    @Inject(method = "useItemOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/apache/commons/lang3/mutable/MutableObject;<init>()V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void injectAfterMutableObject(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir) {
        if(TempConfig.ghostPlacement) {
            cir.setReturnValue(this.performUseItemOn(player, hand, blockHit));
            cir.cancel();
        }
    }
}
