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

import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ScanRange<T extends ScanRange<T>> implements Comparable<T> {
  private static final int MIN_VALUE = 3;
  private static final int MAX_VALUE = 32;
  private static final int DEFAULT_VALUE = 12;
  private static final Comparator<ScanRange<?>> COMPARATOR =
    Comparator.<ScanRange<?>, String>comparing(ScanRange::classSimpleName)
      .thenComparingInt(ScanRange::value);
  @Getter
  private final int value;

  private ScanRange(int value) {
    if (value < MIN_VALUE || value > MAX_VALUE) throw new IllegalArgumentException(Integer.toString(value));
    this.value = value;
  }

  private static <T extends ScanRange<T>> Map<Integer, T> createValues(IntFunction<T> constructor) {
    return IntStream
      .iterate(MIN_VALUE, i -> ++i)
      .limit(MAX_VALUE - MIN_VALUE + 1)
      .mapToObj(constructor)
      .collect(Collectors.toMap(ScanRange::value, UnaryOperator.identity()));
  }

  public abstract T next();

  public abstract T prev();

  @Override
  public int compareTo(@Nonnull T o) {
    return COMPARATOR.compare(this, o);
  }

  @Override
  public int hashCode() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this || getClass().isInstance(obj) && ((ScanRange) obj).value == value;
  }

  @Override
  public String toString() {
    return "ScanRange." + classSimpleName() + "(" + value + ")";
  }

  int nextValue() {
    return Math.min(value + 1, MAX_VALUE);
  }

  int prevValue() {
    return Math.max(value - 1, MIN_VALUE);
  }

  private String classSimpleName() {
    return getClass().getSimpleName();
  }

  public static final class Horizontal extends ScanRange<Horizontal> {
    public static final int MIN_VALUE = ScanRange.MIN_VALUE;
    public static final int MAX_VALUE = ScanRange.MAX_VALUE;

    private static final Map<Integer, Horizontal> VALUES = createValues(Horizontal::new);
    public static final Horizontal DEFAULT = of(DEFAULT_VALUE);

    private Horizontal(int value) {
      super(value);
    }

    public static Horizontal of(int value) {
      return VALUES.get(value);
    }

    @Override
    public Horizontal next() {
      return of(nextValue());
    }

    @Override
    public Horizontal prev() {
      return of(prevValue());
    }
  }

  public static final class Vertical extends ScanRange<Vertical> {
    public static final int MIN_VALUE = ScanRange.MIN_VALUE;
    public static final int MAX_VALUE = ScanRange.MAX_VALUE;

    private static final Map<Integer, Vertical> VALUES = createValues(Vertical::new);
    public static final Vertical DEFAULT = of(DEFAULT_VALUE);

    private Vertical(int value) {
      super(value);
    }

    public static Vertical of(int value) {
      return VALUES.get(value);
    }

    @Override
    public Vertical next() {
      return of(nextValue());
    }

    @Override
    public Vertical prev() {
      return of(prevValue());
    }
  }
}
