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

package net.awairo.minecraft.spawnchecker.api;

//import net.minecraft.client.renderer.WorldRenderer;
//import net.minecraft.client.renderer.entity.EntityRendererManager;
//import net.minecraft.util.math.vector.Quaternion;

import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

public interface MarkerRenderer extends Renderer {
    EntityRenderDispatcher renderManager();
    LevelRenderer worldRenderer();

    void push();
    void pop();
    void translate(double x, double y, double z);
    void scale(float m00, float m11, float m22);
    void rotate(Quaternion quaternion);
    void clear();
}
