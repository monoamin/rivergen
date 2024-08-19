package net.monoamin.erosion;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "erosion")
public class RenderHandler {
    public static Map<String, Tuple<Vec3, Vec3>> solutionSet = new HashMap<>();

    @SubscribeEvent
    public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        PoseStack pose = event.getPoseStack();
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        MultiBufferSource.BufferSource bufSource = Minecraft.getInstance().renderBuffers().bufferSource();
        pose.pushPose();

        // Translate to align with the camera
        pose.translate(-camera.x, -camera.y, -camera.z);
        Matrix4f matrix = pose.last().pose();

        VertexConsumer consumer = bufSource.getBuffer(RenderType.LINES);  // Move outside the loop

        for (Map.Entry<String, Tuple<Vec3, Vec3>> entry : solutionSet.entrySet()) {
            Tuple<Vec3, Vec3> points = entry.getValue();
            Vec3 start = points.getA();
            Vec3 end = points.getB();

            // Define the two points for the line
            definePoint(consumer, matrix, start);
            definePoint(consumer, matrix, end);
        }

        bufSource.endBatch();  // Ensure this is called after all lines are defined

        pose.popPose();
    }

    public static void AddLineIfAbsent(String label, Vec3 a, Vec3 b) {
        solutionSet.putIfAbsent(label, new Tuple<Vec3, Vec3>(a, b));
    }

    public static void definePoint(VertexConsumer consumer, Matrix4f matrix, Vec3 p) {
        consumer.vertex(matrix, (float) p.x + 0.5f, (float) p.y + 0.5f, (float) p.z + 0.5f)
                .color(255, 10, 10, 255)
                .normal(0, 1, 0)
                .endVertex();
    }
}
