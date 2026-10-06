package net.crimssoon.createmaintenancerequired.rust.helpers

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.platform.TextureUtil
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.server.packs.resources.ResourceManager

class MippedTexture(private val levels: Array<NativeImage>) : AbstractTexture() {

    init {
        RenderSystem.assertOnRenderThreadOrInit()
        val mipmap = levels.size > 1
        TextureUtil.prepareImage(getId(), levels.size - 1, levels[0].width, levels[0].height)
        for (i in levels.indices) {
            val image = levels[i]
            image.upload(i, 0, 0, 0, 0, image.width, image.height, false, true, mipmap, false)
        }
        setFilter(false, mipmap)
    }

    override fun load(resourceManager: ResourceManager) {}

    override fun close() {
        releaseId()
        for (image in levels) image.close()
    }
}