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

package net.awairo.minecraft.spawnchecker;

import lombok.RequiredArgsConstructor;
import net.minecraft.util.profiling.ProfilerFiller;

@RequiredArgsConstructor
final class WrappedProfiler {
  private final ProfilerFiller underlying;

  /*
  IProfiler#startSection(String p_i8581_) => ProfilerFiller#push(String p_i8581_)
  IProfiler#endSection() => ProfilerFiller#pop()
   */
  private void start(final String subSection) {
    underlying.push(SpawnChecker.MOD_ID);
    underlying.push(subSection);
  }

  private void end() {
    underlying.pop();
    underlying.pop();
  }

  // FIXME セクションの見直し

  void startClientTick() {
    start("clientTick");
  }

  void endClientTick() {
    end();
  }

  void startRenderHud() {
    start("renderHud");
  }

  void endRenderHud() {
    end();
  }

  void startRenderMarker() {
    start("renderMarker");
  }

  void endRenderMarker() {
    end();
  }

}
