package nl.eetgeenappels.ssssv.client.render

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import nl.eetgeenappels.ssssv.client.SuperSimpleServerSideVeinminerClient
import org.joml.Matrix4f
import java.util.*

// credit goes to the Veinminer mod's highlight renderer is still don't understand how rendering works D:
object BlockOutlineRenderer {
    /**
     * Render type for block highlighting.
     *
     * RenderType.create(namespace, bufferSize, pipeline, state)
     */
    private val renderHighlighting
        get() = RenderType.create(
            "ssssv:outline",
            512,
            RenderPipelines.register(
                RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                    .withLocation(ResourceLocation.fromNamespaceAndPath("ssssv", "pipeline/outline"))
                    .withDepthWrite(false)
                    .withCull(false)
                    .withColorWrite(true, true)
                    .build()
            ),
            RenderType.CompositeState.builder()
                .setLineState(RenderStateShard.LineStateShard(OptionalDouble.of(1.0)))
                .setLayeringState(RenderStateShard.LayeringStateShard.NO_LAYERING)
                .createCompositeState(false)
        )

    private val renderHighlightingTranslucent
        get() = RenderType.create(
            "ssssv:highlight_translucent",
            512,
            RenderPipelines.register(
                RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                    .withLocation(ResourceLocation.fromNamespaceAndPath("veinminer-client", "pipeline/highlight_translucent"))
                    .withDepthWrite(true)
                    .withCull(false)
                    .withColorWrite(true, true)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST) // Render always on top
                    .build()
            ),
            RenderType.CompositeState.builder()
                .setLineState(RenderStateShard.LineStateShard(OptionalDouble.of(1.0)))
                .setLayeringState(RenderStateShard.LayeringStateShard.NO_LAYERING)
                .createCompositeState(false)
        )


    // OrderedSubmitNodeCollector
    fun render(stack: PoseStack,
               source: MultiBufferSource.BufferSource,
               camPos: Vec3,
               isTranslucentPass: Boolean) {

        stack.pushPose()

        val matrix = stack.last().pose()

        val voxelShape = setShape(SuperSimpleServerSideVeinminerClient.cubes, camPos)

        renderBlocks(source, renderHighlighting, matrix, voxelShape, 255)


        // Force draw the lines immediately
        if (!isTranslucentPass) {
            source.endBatch(renderHighlighting)
        }

        stack.popPose()
    }

    private fun renderBlocks(source: MultiBufferSource.BufferSource, renderer: RenderType, matrix: Matrix4f, shape: VoxelShape, transparency: Int) {
        val buffer = source.getBuffer(renderer)
        shape.forAllEdges { x, y, z, dx, dy, dz ->
            val x = x.toFloat()
            val y = y.toFloat()
            val z = z.toFloat()
            val dx = dx.toFloat()
            val dy = dy.toFloat()
            val dz = dz.toFloat()
            val relX = dx - x
            val relY = dy - y
            val relZ = dz - z

            // Outline
            buffer.addVertex(matrix, x, y, z)
                .setColor(255, 255, 255, transparency)
                .setNormal(relX, relY, relZ)
            buffer.addVertex(matrix, dx, dy, dz)
                .setColor(255, 255, 255, transparency)
                .setNormal(relX, relY, relZ)
        }
        source.endLastBatch()
    }

    fun setShape(positions: List<BlockPos>, camPos: Vec3): VoxelShape {
        if (positions.isEmpty()) {
            return Shapes.empty()
        }

        val splines = positions.map {
            val box = Shapes.box(-0.010, -0.010, -0.010, 1.010, 1.010, 1.010) // Outline
            //val box = Shapes.box(0.35, 0.35, 0.35, 0.65, 0.65, 0.65) // Inline Box (more clutter)
            val dx = it.x - camPos.x
            val dy = it.y - camPos.y
            val dz = it.z - camPos.z
            if (dx.toInt() == 0 && dy.toInt() == 0 && dz.toInt() == 0) box
            else box.move(dx, dy, dz)
        }
        return Shapes.or(splines.first(), *splines.toTypedArray())
    }
}