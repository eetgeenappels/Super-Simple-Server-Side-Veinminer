package nl.eetgeenappels.ssssv.client.render

import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import nl.eetgeenappels.ssssv.client.SuperSimpleServerSideVeinminerClient
import nl.eetgeenappels.ssssv.client.config.ClientConfigs
import nl.eetgeenappels.ssssv.client.mixin.RenderTypeAccessor
import org.joml.Matrix4f

// credit goes to the Veinminer mod's highlight renderer is still don't understand how rendering works D:
object BlockOutlineRenderer {
    /**
     * Render type for block highlighting.
     *
     * RenderType.create(namespace, bufferSize, pipeline, state)
     */

    // create render type
    val renderType: RenderType
        get() = RenderTypeAccessor.callCreate("outline_lines", RenderSetup.builder(
            RenderPipeline.builder(*arrayOf(RenderPipelines.LINES_SNIPPET))
                .withoutBlend()
                .withLocation("pipeline/lines").build())
            .createRenderSetup());


    fun render(stack: PoseStack,
               source: MultiBufferSource.BufferSource,
               camPos: Vec3,
               isTranslucentPass: Boolean) {

        if (SuperSimpleServerSideVeinminerClient.cubes.isEmpty())
            return

        if (!ClientConfigs.ssssvRenderConfig.previewsEnabled)
            return

        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return



        stack.pushPose()

        val matrix = stack.last().pose()

        renderBlocks(source, renderType, matrix, SuperSimpleServerSideVeinminerClient.cubes, camPos)


        source.endBatch()
        stack.popPose()
    }


    private fun renderBlocks(
        source: MultiBufferSource.BufferSource,
        renderer: RenderType,
        matrix: Matrix4f,
        positions: List<BlockPos>,
        camPos: Vec3
    ) {
        val buffer = source.getBuffer(renderer)

        // Define the 12 edges of a cube once
        val edges = arrayOf(
            // Bottom face
            floatArrayOf(0f, 0f, 0f, 1f, 0f, 0f),
            floatArrayOf(1f, 0f, 0f, 1f, 0f, 1f),
            floatArrayOf(1f, 0f, 1f, 0f, 0f, 1f),
            floatArrayOf(0f, 0f, 1f, 0f, 0f, 0f),
            // Top face
            floatArrayOf(0f, 1f, 0f, 1f, 1f, 0f),
            floatArrayOf(1f, 1f, 0f, 1f, 1f, 1f),
            floatArrayOf(1f, 1f, 1f, 0f, 1f, 1f),
            floatArrayOf(0f, 1f, 1f, 0f, 1f, 0f),
            // Vertical edges
            floatArrayOf(0f, 0f, 0f, 0f, 1f, 0f),
            floatArrayOf(1f, 0f, 0f, 1f, 1f, 0f),
            floatArrayOf(1f, 0f, 1f, 1f, 1f, 1f),
            floatArrayOf(0f, 0f, 1f, 0f, 1f, 1f)
        )

        // Render each block's outline
        for (pos in positions) {
            val offsetX = (pos.x - camPos.x).toFloat()
            val offsetY = (pos.y - camPos.y).toFloat()
            val offsetZ = (pos.z - camPos.z).toFloat()

            // Draw all 12 edges
            for (edge in edges) {
                val x1 = edge[0]  + offsetX
                val y1 = edge[1]  + offsetY
                val z1 = edge[2]  + offsetZ
                val x2 = edge[3] + offsetX
                val y2 = edge[4] + offsetY
                val z2 = edge[5] + offsetZ

                val relX = x2 - x1
                val relY = y2 - y1
                val relZ = z2 - z1

                buffer.addVertex(matrix, x1, y1, z1)
                    .setColor(ClientConfigs.ssssvRenderConfig.renderPreviewColor.r(),
                        ClientConfigs.ssssvRenderConfig.renderPreviewColor.g(),
                        ClientConfigs.ssssvRenderConfig.renderPreviewColor.b(),
                        ClientConfigs.ssssvRenderConfig.renderPreviewColor.a())
                    .setLineWidth(ClientConfigs.ssssvRenderConfig.renderPreviewLineWidth.get())
                    .setNormal(relX, relY, relZ)
                buffer.addVertex(matrix, x2, y2, z2)
                    .setColor(ClientConfigs.ssssvRenderConfig.renderPreviewColor.r(),
                        ClientConfigs.ssssvRenderConfig.renderPreviewColor.g(),
                        ClientConfigs.ssssvRenderConfig.renderPreviewColor.b(),
                        ClientConfigs.ssssvRenderConfig.renderPreviewColor.a())
                    .setLineWidth(ClientConfigs.ssssvRenderConfig.renderPreviewLineWidth.get())
                    .setNormal(relX, relY, relZ)
            }
        }

        source.endLastBatch()
    }
}