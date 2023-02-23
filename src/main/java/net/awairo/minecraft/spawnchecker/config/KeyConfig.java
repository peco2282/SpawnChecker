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

package net.awairo.minecraft.spawnchecker.config;

import lombok.NonNull;
import net.awairo.minecraft.spawnchecker.keybinding.RepeatDelay;
import net.awairo.minecraft.spawnchecker.keybinding.RepeatRate;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import static net.awairo.minecraft.spawnchecker.config.SpawnCheckerConfig.configGuiKey;
import static net.awairo.minecraft.spawnchecker.config.SpawnCheckerConfig.defaultMinMax;

public final class KeyConfig {
  private static final String PATH = "key";

  private final Updater updater;
  private final IntValue repeatDelayValue;

  // region [key binding] RepeatDelay
  private final IntValue repeatRateValue;
  private RepeatDelay repeatDelayCache;
  private RepeatRate repeatRateCache;

  // endregion

  // region [key binding] RepeatRate

  KeyConfig(@NonNull Updater updater, @NonNull ForgeConfigSpec.Builder builder) {
    this.updater = updater;

    builder.comment(" Key binding configurations.");
    builder.push(PATH);

    repeatDelayValue = builder
      .comment(
        " Key repeat delay. (ms)",
        defaultMinMax(RepeatDelay.DEFAULT.milliSeconds(), RepeatDelay.MIN, RepeatDelay.MAX)
      )
      .translation(
        configGuiKey(PATH, "repeatDelay")
      )
      .defineInRange(
        "repeatDelay",
        RepeatDelay.DEFAULT::milliSeconds, RepeatDelay.MIN, RepeatDelay.MAX
      );

    repeatRateValue = builder
      .comment(
        " Key repeat rate. (ms)",
        defaultMinMax(RepeatRate.DEFAULT.milliSeconds(), RepeatRate.MIN, RepeatRate.MAX)
      )
      .translation(
        configGuiKey(PATH, "repeatRate")
      )
      .defineInRange(
        "repeatRate",
        RepeatRate.DEFAULT::milliSeconds, RepeatRate.MIN, RepeatRate.MAX
      );

    builder.pop();
  }

  public RepeatDelay repeatDelay() {
    var cache = repeatDelayCache;
    if (cache == null || cache.milliSeconds() != repeatDelayValue.get())
      repeatDelayCache = cache = RepeatDelay.ofMilliSeconds(repeatDelayValue.get());
    return cache;
  }

  public RepeatRate repeatRate() {
    var cache = repeatRateCache;
    if (cache == null || cache.milliSeconds() != repeatRateValue.get())
      repeatRateCache = cache = RepeatRate.ofMilliSeconds(repeatRateValue.get());
    return cache;
  }

  // endregion

}
