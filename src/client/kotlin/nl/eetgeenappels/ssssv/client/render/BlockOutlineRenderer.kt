package nl.eetgeenappels.ssssv.client.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import nl.eetgeenappels.ssssv.client.SuperSimpleServerSideVeinminerClient
import nl.eetgeenappels.ssssv.client.SuperSimpleServerSideVeinminerClient.cubes
import nl.eetgeenappels.ssssv.client.config.ClientConfigs
import nl.eetgeenappels.ssssv.client.config.SSSSVRenderConfig
import org.joml.Matrix4f
import java.util.function.Supplier


object BlockOutlineRenderer {

    fun renderBlockOutlines(ctx: WorldRenderContext) {
        // Get camera position for relative rendering
        val camera = ctx.camera().position
        val poseStack = ctx.matrixStack()

        poseStack!!.pushPose()

        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        RenderSystem.lineWidth(ClientConfigs.ssssvRenderConfig.renderPreviewLineWidth.get().toFloat())
        if (ClientConfigs.ssssvRenderConfig.renderPreviewThroughBlocks) {
            RenderSystem.disableDepthTest()
            RenderSystem.disableBlend()
        } else {
            RenderSystem.enableDepthTest()
            RenderSystem.enableBlend()
        }
        RenderSystem.disableCull()
        RenderSystem.defaultBlendFunc()

        val bufferBuilder = Tesselator.getInstance().begin(
            VertexFormat.Mode.DEBUG_LINES,
            DefaultVertexFormat.POSITION_COLOR
        )

        poseStack.mulPose(ctx.camera().rotation().invert())

        // Get the matrix AFTER pushing
        val matrix = poseStack.last().pose()

        for (blockPos in cubes) {
            drawCube(bufferBuilder, blockPos, matrix, camera)
        }
        // Adjust coordinates relative to camera


        val buffer = bufferBuilder.build() ?: return

        BufferUploader.drawWithShader(buffer)

        poseStack.popPose()

        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()
        RenderSystem.enableCull()
    }

    fun drawCube(bufferBuilder: BufferBuilder ,blockPos: BlockPos, matrix: Matrix4f, camera: Vec3) {

        val cube = AABB(
            blockPos.x.toDouble(),
            blockPos.y.toDouble(),
            blockPos.z.toDouble(),
            (blockPos.x + 1).toDouble(),
            (blockPos.y + 1).toDouble(),
            (blockPos.z + 1).toDouble()
        )

        val minX = (cube.minX - camera.x).toFloat()
        val minY = (cube.minY - camera.y).toFloat()
        val minZ = (cube.minZ - camera.z).toFloat()
        val maxX = (cube.maxX - camera.x).toFloat()
        val maxY = (cube.maxY - camera.y).toFloat()
        val maxZ = (cube.maxZ - camera.z).toFloat()

        val red = ClientConfigs.ssssvRenderConfig.renderPreviewColor.r()
        val green = ClientConfigs.ssssvRenderConfig.renderPreviewColor.g()
        val blue = ClientConfigs.ssssvRenderConfig.renderPreviewColor.b()
        val alpha = ClientConfigs.ssssvRenderConfig.renderPreviewColor.a()

        // Bottom face (4 lines)
        bufferBuilder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha)
        bufferBuilder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha)

        bufferBuilder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha)
        bufferBuilder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha)

        bufferBuilder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha)
        bufferBuilder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha)

        bufferBuilder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha)
        bufferBuilder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha)

        // Top face (4 lines)
        bufferBuilder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha)
        bufferBuilder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha)

        bufferBuilder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha)
        bufferBuilder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha)

        bufferBuilder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha)
        bufferBuilder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha)

        bufferBuilder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha)
        bufferBuilder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha)

        // Vertical edges (4 lines)
        bufferBuilder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha)
        bufferBuilder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha)

        bufferBuilder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha)
        bufferBuilder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha)

        bufferBuilder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha)
        bufferBuilder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha)

        bufferBuilder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha)
        bufferBuilder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha)
    }
}
