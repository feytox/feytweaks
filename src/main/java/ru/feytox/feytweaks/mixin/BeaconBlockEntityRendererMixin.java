package ru.feytox.feytweaks.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.feytweaks.client.FTConfig;
import ru.feytox.feytweaks.client.FeytweaksClient;

@Mixin(BeaconBlockEntityRenderer.class)
public class BeaconBlockEntityRendererMixin {

    @Inject(method = "render(Lnet/minecraft/block/entity/BeaconBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At("HEAD"), cancellable = true)
    public void onRender(BeaconBlockEntity beaconBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        if (FeytweaksClient.shouldRenderBeam(beaconBlockEntity) || (!FeytweaksClient.isOnScreen(beaconBlockEntity)
                && FTConfig.beaconCulling)) {
            ci.cancel();
        }
    }

    @WrapWithCondition(method = "renderBeam(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/util/Identifier;FFJIIIFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BeaconBlockEntityRenderer;renderBeamLayer(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIIFFFFFFFFFFFF)V", ordinal = 1))
    private static boolean disableSecondBeamLayer(MatrixStack matrices, VertexConsumer vertices, int color, int yOffset, int height, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float u2, float v1, float v2) {
        return !FTConfig.optimizeBeam;
    }

    @Inject(method = "renderBeam(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/util/Identifier;FFJIIIFF)V",
            at = @At("HEAD"))
    private static void disableBeamRotation(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier textureId, float tickDelta, float heightScale, long worldTime, int yOffset, int maxY, int color, float innerRadius, float outerRadius, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalFloatRef tickDeltaRef, @Local(argsOnly = true) LocalLongRef worldTimeRef) {
        if (!FTConfig.optimizeBeam) return;
        worldTimeRef.set(0);
        tickDeltaRef.set(0);
    }
}
