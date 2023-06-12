/*
 * SpawnChecker
 * Copyright (C) 2019 alalwww
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.awairo.minecraft.spawnchecker.mode;

//import com.mojang.blaze3d.matrix.MatrixStack;
//
//import net.minecraft.client.renderer.LevelRenderer;
//import net.minecraft.client.renderer.WorldRenderer;
//import net.minecraft.client.renderer.entity.EntityRendererManager;
//import net.minecraft.client.renderer.texture.TextureManager;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.math.vector.Quaternion;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.awairo.minecraft.spawnchecker.api.Color;
import net.awairo.minecraft.spawnchecker.api.MarkerRenderer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

@Value
final class MyMarkerRendererImpl implements MarkerRenderer {
    private final LevelRenderer worldRenderer;
    private final float partialTicks;
    @Getter(AccessLevel.PRIVATE)
    private final PoseStack matrixStack;
    private final TextureManager textureManager;
    private final EntityRenderDispatcher renderManager;

    @Override
    public void bindTexture(ResourceLocation texture) {
        textureManager.bindForSetup(texture);
    }

    @Override
    public void addVertex(double x, double y, double z) {
        buffer()
            .vertex(matrixStack.last().pose(), (float) x, (float) y, (float) z)
            .endVertex();
    }

    @Override
    public void addVertex(double x, double y, double z, float u, float v) {
        buffer()
            .vertex(matrixStack.last().pose(), (float) x, (float) y, (float) z)
            .uv(u, v)
            .endVertex();
    }

    @Override
    public void addVertex(double x, double y, double z, Color color) {
        buffer()
            .vertex(matrixStack.last().pose(), (float) x, (float) y, (float) z)
            .color(color.red(), color.green(), color.blue(), color.alpha())
            .endVertex();
    }

    @Override
    public void addVertex(double x, double y, double z, float u, float v, Color color) {
        buffer()
            .vertex(matrixStack.last().pose(), (float) x, (float) y, (float) z)
            .uv(u, v)
            .color(color.red(), color.green(), color.blue(), color.alpha())
            .endVertex();
    }

    @Override
    public void push() {
        matrixStack.pushPose();
    }

    @Override
    public void pop() {
        matrixStack.popPose();
    }

    @Override
    public void translate(double x, double y, double z) {
        matrixStack.translate(x, y, z);
    }

    @Override
    public void scale(float m00, float m11, float m22) {
        matrixStack.scale(m00, m11, m22);
    }

    @Override
    public void rotate(Quaternion quaternion) {
        matrixStack.mulPose(quaternion);
    }

    @Override
    public void clear() {
        matrixStack.clear();
    }
}
