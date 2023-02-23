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

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public interface Renderer {
  void bindTexture(ResourceLocation texture);

  default Tesselator tessellator() {
    return Tesselator.getInstance();
  }

  default BufferBuilder buffer() {
    return tessellator().getBuilder();
  }

  default void beginPoints(VertexFormat format) {
    begin(VertexFormat.Mode.DEBUG_LINES, format);
  }

  default void beginLines(VertexFormat format) {
    begin(VertexFormat.Mode.LINES, format);
  }

  default void beginLineLoop(VertexFormat format) {
    begin(VertexFormat.Mode.LINES, format);
  }

  default void beginTriangles(VertexFormat format) {
    begin(VertexFormat.Mode.TRIANGLES, format);
  }

  default void beginTriangleStrip(VertexFormat format) {
    begin(VertexFormat.Mode.TRIANGLE_STRIP, format);
  }

  default void beginTriangleFan(VertexFormat format) {
    begin(VertexFormat.Mode.TRIANGLE_FAN, format);
  }

  default void beginQuads(VertexFormat format) {
    begin(VertexFormat.Mode.QUADS, format);
  }

  default void beginQuadStrip(VertexFormat format) {
    begin(VertexFormat.Mode.LINE_STRIP, format);
  }

  default void beginPolygon(VertexFormat format) {
    begin(VertexFormat.Mode.DEBUG_LINES, format);
  }

  default void begin(VertexFormat.Mode glMode, VertexFormat format) {
    buffer().begin(glMode, format);
  }

  void addVertex(double x, double y, double z);

  void addVertex(double x, double y, double z, float u, float v);

  void addVertex(double x, double y, double z, Color color);

  void addVertex(double x, double y, double z, float u, float v, Color color);

  default void draw() {
    tessellator().end();
  }

  float partialTicks();
}
