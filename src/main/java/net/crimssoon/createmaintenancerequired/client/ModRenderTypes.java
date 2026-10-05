package net.crimssoon.createmaintenancerequired.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ModRenderTypes extends RenderType {

    private static final Map<ResourceLocation, RenderType> CACHE = new HashMap<>();

    private ModRenderTypes() {
        super("createmaintenancerequired_unused", DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS, 256, false, false, () -> {}, () -> {});
    }

    public static RenderType rust(ResourceLocation texture) {
        return CACHE.computeIfAbsent(texture, tex -> create(
                "createmaintenancerequired_rust",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
                        .setTextureState(new TextureStateShard(tex, false, true))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .createCompositeState(true)
        ));
    }
}