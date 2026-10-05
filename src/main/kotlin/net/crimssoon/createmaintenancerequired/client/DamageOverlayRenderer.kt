package net.crimssoon.createmaintenancerequired.client

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.crimssoon.createmaintenancerequired.CreateMaintenanceRequired
import net.crimssoon.createmaintenancerequired.RustDurability
import net.crimssoon.createmaintenancerequired.RustTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.MipmapGenerator
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.client.model.data.ModelData
import org.joml.Matrix4f
import org.joml.Vector3f
import java.util.IdentityHashMap

internal class OverlayQuad(
    val quad: BakedQuad,
    val nx: Float, val ny: Float, val nz: Float,
    val cx: Float, val cy: Float, val cz: Float
)

internal typealias Grouped = Map<ResourceLocation, List<OverlayQuad>>

@EventBusSubscriber(
    modid = CreateMaintenanceRequired.MOD_ID,
    value = [Dist.CLIENT]
)
object DamageOverlayRenderer {

    private const val OFFSET = 0.001f

    private val RUST_TEXTURE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(
        CreateMaintenanceRequired.MOD_ID,
        "textures/block/rust.png"
    )

    private val sides: List<Direction?> = Direction.entries + listOf(null)

    private val baseCache = HashMap<BlockState, Grouped>()
    private val modelCache = IdentityHashMap<BakedModel, Grouped>()

    private val maskedTextures = HashMap<ResourceLocation, ResourceLocation?>()
    private var rustImage: NativeImage? = null

    private val visible = ArrayList<BlockEntity>()
    private val usedTypes = HashSet<RenderType>()
    private val lightCache = HashMap<Long, IntArray>()

    private val inverse = Matrix4f()
    private val localCamera = Vector3f()

    private val sableLoaded: Boolean by lazy { ModList.get().isLoaded("sable") }

    private fun lightAt(level: Level, pos: BlockPos): Int {
        val key = pos.asLong()
        val now = level.gameTime.toInt()
        val entry = lightCache[key]
        if (entry != null && now - entry[0] < 10) return entry[1]
        val light = LevelRenderer.getLightColor(level, pos)
        if (entry != null) {
            entry[0] = now
            entry[1] = light
        } else {
            if (lightCache.size > 4096) lightCache.clear()
            lightCache[key] = intArrayOf(now, light)
        }
        return light
    }

    private fun cameraInCurrentSpace(poseStack: PoseStack): Vector3f {
        inverse.set(poseStack.last().pose()).invert()
        localCamera.set(0f, 0f, 0f)
        inverse.transformPosition(localCamera)
        return localCamera
    }

    @JvmStatic
    @SubscribeEvent
    fun onBakingCompleted(event: ModelEvent.BakingCompleted) {
        baseCache.clear()
        modelCache.clear()
        lightCache.clear()

        val textureManager = Minecraft.getInstance().textureManager
        for (tex in maskedTextures.values) {
            if (tex != null) textureManager.release(tex)
        }
        maskedTextures.clear()
        rustImage?.close()
        rustImage = null
    }

    private fun loadRust(): NativeImage? {
        rustImage?.let { return it }
        return try {
            Minecraft.getInstance().resourceManager.getResource(RUST_TEXTURE).get().open().use {
                NativeImage.read(it)
            }.also { rustImage = it }
        } catch (e: Exception) {
            null
        }
    }

    private fun maskedTextureFor(sprite: TextureAtlasSprite): ResourceLocation? {
        val name = sprite.contents().name()
        if (maskedTextures.containsKey(name)) return maskedTextures[name]

        val rust = loadRust()
        val original = sprite.contents().originalImage
        if (rust == null) {
            maskedTextures[name] = null
            return null
        }

        val w = sprite.contents().width()
        val h = sprite.contents().height()
        val image = NativeImage(w, h, false)
        for (y in 0 until h) {
            for (x in 0 until w) {
                val origAlpha = (original.getPixelRGBA(x, y) ushr 24) and 0xFF
                val rustPixel = rust.getPixelRGBA(x * rust.width / w, y * rust.height / h)
                val rustAlpha = (rustPixel ushr 24) and 0xFF
                val alpha = rustAlpha * origAlpha / 255
                image.setPixelRGBA(x, y, (alpha shl 24) or (rustPixel and 0xFFFFFF))
            }
        }

        val location = ResourceLocation.fromNamespaceAndPath(
            CreateMaintenanceRequired.MOD_ID,
            "dynamic/rust_" + name.namespace + "_" + name.path.replace('/', '_')
        )
        val mipLevels = Minecraft.getInstance().options.mipmapLevels().get()
        val levels = MipmapGenerator.generateMipLevels(arrayOf(image), mipLevels)
        Minecraft.getInstance().textureManager.register(location, MippedTexture(levels))
        maskedTextures[name] = location
        return location
    }

    private fun overlayQuads(model: BakedModel, state: BlockState?): Grouped {
        val random = RandomSource.create()
        val out = HashMap<ResourceLocation, MutableList<OverlayQuad>>()
        for (side in sides) {
            for (quad in model.getQuads(state, side, random, ModelData.EMPTY, null)) {
                val texture = maskedTextureFor(quad.sprite) ?: continue
                out.getOrPut(texture) { ArrayList() }.add(buildOverlayQuad(quad))
            }
        }
        return out
    }

    internal fun baseQuadsFor(state: BlockState): Grouped = baseCache.getOrPut(state) {
        overlayQuads(Minecraft.getInstance().blockRenderer.getBlockModel(state), state)
    }

    internal fun quadsForModel(model: BakedModel): Grouped =
        modelCache.getOrPut(model) { overlayQuads(model, null) }

    private fun buildOverlayQuad(quad: BakedQuad): OverlayQuad {
        val vertices = quad.vertices.copyOf()
        val sprite = quad.sprite

        fun px(i: Int) = Float.fromBits(vertices[i * 8])
        fun py(i: Int) = Float.fromBits(vertices[i * 8 + 1])
        fun pz(i: Int) = Float.fromBits(vertices[i * 8 + 2])

        val e1x = px(1) - px(0); val e1y = py(1) - py(0); val e1z = pz(1) - pz(0)
        val e2x = px(2) - px(0); val e2y = py(2) - py(0); val e2z = pz(2) - pz(0)
        var nx = e1y * e2z - e1z * e2y
        var ny = e1z * e2x - e1x * e2z
        var nz = e1x * e2y - e1y * e2x
        val nl = Math.sqrt((nx * nx + ny * ny + nz * nz).toDouble()).toFloat()
        if (nl > 1e-6f) { nx /= nl; ny /= nl; nz /= nl }

        val du = sprite.u1 - sprite.u0
        val dv = sprite.v1 - sprite.v0

        var cx = 0f
        var cy = 0f
        var cz = 0f

        for (i in 0 until 4) {
            val o = i * 8
            val x = px(i) + nx * OFFSET
            val y = py(i) + ny * OFFSET
            val z = pz(i) + nz * OFFSET
            vertices[o] = x.toBits()
            vertices[o + 1] = y.toBits()
            vertices[o + 2] = z.toBits()
            cx += x * 0.25f
            cy += y * 0.25f
            cz += z * 0.25f

            val u = Float.fromBits(vertices[o + 4])
            val v = Float.fromBits(vertices[o + 5])
            vertices[o + 4] = (if (du != 0f) (u - sprite.u0) / du else 0f).toBits()
            vertices[o + 5] = (if (dv != 0f) (v - sprite.v0) / dv else 0f).toBits()
        }
        return OverlayQuad(BakedQuad(vertices, -1, quad.direction, sprite, false), nx, ny, nz, cx, cy, cz)
    }

    internal fun drawGrouped(
        grouped: Grouped,
        poseStack: PoseStack,
        buffers: MultiBufferSource.BufferSource,
        usedTypes: MutableSet<RenderType>,
        alpha: Float,
        light: Int,
        lx: Float,
        ly: Float,
        lz: Float
    ) {
        for ((texture, quads) in grouped) {
            val type = ModRenderTypes.rust(texture)
            usedTypes.add(type)
            val consumer: VertexConsumer = buffers.getBuffer(type)
            for (oq in quads) {
                if (oq.nx * (lx - oq.cx) + oq.ny * (ly - oq.cy) + oq.nz * (lz - oq.cz) <= 0f) continue
                consumer.putBulkData(
                    poseStack.last(), oq.quad,
                    1f, 1f, 1f, alpha,
                    light, OverlayTexture.NO_OVERLAY
                )
            }
        }
    }

    internal fun drawGroupedTo(
        grouped: Grouped,
        poseStack: PoseStack,
        buffers: MultiBufferSource,
        alpha: Float,
        light: Int,
        overlay: Int
    ) {
        val source = buffers as? MultiBufferSource.BufferSource
        source?.endBatch()
        for ((texture, quads) in grouped) {
            val type = ModRenderTypes.rust(texture)
            val consumer: VertexConsumer = buffers.getBuffer(type)
            for (oq in quads) {
                consumer.putBulkData(poseStack.last(), oq.quad, 1f, 1f, 1f, alpha, light, overlay)
            }
            source?.endBatch(type)
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onRenderLevel(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return

        val mc = Minecraft.getInstance()
        val level = mc.level ?: return
        val cam = event.camera.position
        val poseStack: PoseStack = event.poseStack
        val bufferSource = mc.renderBuffers().bufferSource()
        val frustum = event.frustum
        val partial = event.partialTick.getGameTimeDeltaPartialTick(false)

        visible.clear()
        RustTracker.collect(level, visible)
        if (visible.isEmpty()) return

        usedTypes.clear()

        for (be in visible) {
            val pos = be.blockPos
            val t = if (sableLoaded) SableCompat.transformFor(level, pos) else null

            val cx = t?.gx ?: (pos.x + 0.5)
            val cy = t?.gy ?: (pos.y + 0.5)
            val cz = t?.gz ?: (pos.z + 0.5)

            val dx = cx - cam.x
            val dy = cy - cam.y
            val dz = cz - cam.z

            val alpha = (RustDurability.MAX - RustDurability.get(be)) /
                    RustDurability.MAX.toFloat()
            if (alpha < 0.02f) continue

            val visibleInFrustum = frustum.isVisible(
                AABB(
                    cx - 1.5, cy - 1.5, cz - 1.5,
                    cx + 1.5, cy + 1.5, cz + 1.5
                )
            )
            if (!visibleInFrustum) continue

            val state = be.blockState
            val light = lightAt(level, pos)

            poseStack.pushPose()
            if (t != null) {
                poseStack.translate(dx, dy, dz)
                poseStack.mulPose(t.rotation)
                poseStack.translate(-0.5, -0.5, -0.5)
            } else {
                poseStack.translate(pos.x - cam.x, pos.y - cam.y, pos.z - cam.z)
            }

            val baseCam = cameraInCurrentSpace(poseStack)
            drawGrouped(baseQuadsFor(state), poseStack, bufferSource, usedTypes, alpha, light, baseCam.x, baseCam.y, baseCam.z)

            for (part in RustClientTargets.partsFor(state.block)) {
                if (!part.enabled(be, state)) continue

                poseStack.pushPose()
                part.transform(poseStack, be, state, partial)
                val partCam = cameraInCurrentSpace(poseStack)
                drawGrouped(part.quads(), poseStack, bufferSource, usedTypes, alpha, light, partCam.x, partCam.y, partCam.z)
                poseStack.popPose()
            }

            poseStack.popPose()
        }

        for (type in usedTypes) bufferSource.endBatch(type)
    }
}