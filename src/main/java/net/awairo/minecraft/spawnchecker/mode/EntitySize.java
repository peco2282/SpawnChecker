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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
enum EntitySize {
  ENDERMAN(0.6F, 2.9F),
  ZOMBIE(0.6F, 1.95F),
  SKELETON(0.6F, 1.99F), // zombie より微妙にでかいので判定ではzombieサイズを使うようにした
  SPIDER(1.4F, 0.9F),
  SLIME(0.51000005F, 0.51000005F), // smallest size
  GHAST(4.0F, 4.0F),

  ;

  private final float width;
  private final float height;

  // EntityLiving#isNotColliding(IWorldReaderBase)
  boolean isNotColliding(ClientLevel worldIn, BlockPos pos) {
    val bb = boundingBox(pos);
    return !worldIn.containsAnyLiquid(bb)
      && worldIn.noCollision(null, bb)
      && worldIn.isUnobstructed(null, Shapes.create(bb));
  }

  boolean isNotCollidingWithoutOtherEntityCollision(ClientLevel worldIn, BlockPos pos) {
    val bb = boundingBox(pos);
    return !worldIn.containsAnyLiquid(bb)
      && worldIn.noCollision(null, bb);
  }

  AABB boundingBox(BlockPos pos) {
    return new AABB(
      pos.getX(),
      pos.getY(),
      pos.getZ(),
      (double) pos.getX() + width,
      (double) pos.getY() + height,
      (double) pos.getZ() + width
    );
  }
}
