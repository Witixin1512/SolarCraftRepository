package com.finderfeed.solarforge.client.rendering.rendertypes;

import com.finderfeed.solarforge.ClientHelpers;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.opengl.GL11;


public class SolarCraftRenderTypes extends RenderType{



    public SolarCraftRenderTypes(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }

    @Deprecated
    public static RenderType cutoutTranslucent(){
        return RenderType.create("cutout_translucent", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS,
                131072, true, false, RenderType.CompositeState.builder()
                .setLightmapState(RenderType.LIGHTMAP)
                .setShaderState(RenderType.RENDERTYPE_CUTOUT_SHADER)
                .setTextureState(RenderType.BLOCK_SHEET)
                .setTransparencyState(TRANSLUCENT)

                .createCompositeState(true));
    }
    protected static final RenderStateShard.TransparencyStateShard TRANSLUCENT =
            new RenderStateShard.TransparencyStateShard("translucent", RenderSystem::enableBlend, RenderSystem::disableBlend);


    public static RenderType shaderRendertype(ShaderStateShard shaderStateShard){
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(shaderStateShard)
                .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                .createCompositeState(false);
        return RenderType.create("shaderRendertype", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, true,state);
    }

    public static class ParticleRenderTypes {
        public static final ParticleRenderType SOLAR_STRIKE_PARTICLE_RENDER = new ParticleRenderType() {
            @Override
            public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);


                ClientHelpers.bindText(TextureAtlas.LOCATION_PARTICLES);
                textureManager.getTexture(TextureAtlas.LOCATION_PARTICLES).setBlurMipmap(true, false);
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            }

            @Override
            public void end(Tesselator tessellator) {
                tessellator.end();

                Minecraft.getInstance().textureManager.getTexture(TextureAtlas.LOCATION_PARTICLES).restoreLastBlurMipmap();


                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);


            }
            @Override
            public String toString() {
                return "solarforge:solar_strike_particle";
            }
        };
    }
}