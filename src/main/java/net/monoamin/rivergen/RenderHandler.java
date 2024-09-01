package net.monoamin.rivergen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "rivergen")
public class RenderHandler {
    public static Map<String, LineData> solutionSet = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        PoseStack pose = event.getPoseStack();
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        MultiBufferSource.BufferSource bufSource = Minecraft.getInstance().renderBuffers().bufferSource();
        pose.pushPose();

        // Translate to align with the camera
        pose.translate(-camera.x, -camera.y, -camera.z);
        Matrix4f matrix = pose.last().pose();

        VertexConsumer consumer = bufSource.getBuffer(RenderType.lines());  // Move outside the loop

        for (Map.Entry<String, LineData> entry : solutionSet.entrySet()) {
            LineData lineData = entry.getValue();
            Vec3 start = lineData.getStart();
            Vec3 end = lineData.getEnd();

            // Define the two points for the line with the specified color
            definePoint(consumer, matrix, start, lineData);
            definePoint(consumer, matrix, end, lineData);
        }

        bufSource.endBatch();  // Ensure this is called after all lines are defined

        pose.popPose();
    }

    public static void AddLineIfAbsent(String label, Vec3 a, Vec3 b, int red, int green, int blue, int alpha) {
        solutionSet.putIfAbsent(label, new LineData(a, b, red, green, blue, alpha));
    }

    public static void definePoint(VertexConsumer consumer, Matrix4f matrix, Vec3 p, LineData lineData) {
        consumer.vertex(matrix, (float) p.x + 0.5f, (float) p.y + 0.5f, (float) p.z + 0.5f)
                .color(lineData.getRed(), lineData.getGreen(), lineData.getBlue(), lineData.getAlpha())
                .normal(0, 1, 0)
                .endVertex();
    }
}
