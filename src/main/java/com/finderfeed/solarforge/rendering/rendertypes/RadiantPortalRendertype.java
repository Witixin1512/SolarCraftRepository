package com.finderfeed.solarforge.rendering.rendertypes;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public class RadiantPortalRendertype extends RenderType {

    //waves
    public static ShaderInstance WATER_SHADER;
    public static final ShaderStateShard RENDERTYPE_WATER_SHADER = new ShaderStateShard(() -> WATER_SHADER);

    public RadiantPortalRendertype(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable one, Runnable two) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, one, two);
    }

    public static RenderType textWithWaterShader(ResourceLocation loc){
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_WATER_SHADER)
                .setTextureState(new TextureStateShard(loc, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false);
        return create("textWithWaterShader", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true,state);
    }

    //create("text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_TEXT_SHADER)
    // .setTextureState(new TextureStateShard(p_173251_, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(false))
}