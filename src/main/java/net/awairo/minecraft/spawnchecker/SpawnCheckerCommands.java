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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import net.awairo.minecraft.spawnchecker.config.SpawnCheckerConfig;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

@Log4j2
final class SpawnCheckerCommands {
    private static final int ALL = 0;
    private static final int INTEGRATED_SERVER_OP = 2;
    private static final int DEDICATED_SERVER_OP = 4;

    private final SpawnCheckerConfig config;

    private static final Component TO_ENABLE =
        new TranslatableComponent("spawnchecker.command.message.toEnabled");
    private static final Component TO_DISABLE =
        new TranslatableComponent("spawnchecker.command.message.toDisabled");

    private static final Component GUIDELINE_ON =
        new TranslatableComponent("spawnchecker.command.message.guidelineOn");
    private static final Component GUIDELINE_OFF =
        new TranslatableComponent("spawnchecker.command.message.guidelineOff");

    private final CommandDispatcher<Source> dispatcher = new CommandDispatcher<>();

    SpawnCheckerCommands(@NonNull SpawnCheckerConfig config) {
        this.config = config;
        dispatcher.register(builder());
    }

    private Source commandSource;

    @SuppressWarnings("unchecked")
    void registerTo(@NonNull LocalPlayer player) {
        this.commandSource = new Source(player.connection.getSuggestionsProvider());
        player.connection.getCommands()
            .register((LiteralArgumentBuilder<SharedSuggestionProvider>) (LiteralArgumentBuilder<?>) builder());
    }

    boolean parse(String message) {
        val res = dispatcher.parse(message.substring(1), commandSource);
        log.debug("res: {}", res.getContext());
        try {
            dispatcher.execute(res);
            return true;
        } catch (CommandSyntaxException e) {
            log.debug("Failed execute command.", e);
            return false;
        }
    }

    private LiteralArgumentBuilder<Source> builder() {
        return literal("spawnchecker")
            .then(literal("on").executes(ctx -> success(ctx, config::enable, TO_ENABLE)))
            .then(literal("off").executes(ctx -> success(ctx, config::disable, TO_DISABLE)))
            .then(literal("guideline")
                .then(literal("on").executes(ctx -> success(ctx, config.presetModeConfig()::guidelineOn, GUIDELINE_ON)))
                .then(literal("off").executes(ctx -> success(ctx, config.presetModeConfig()::guidelineOff, GUIDELINE_OFF)))
            )
            ;
    }

    private int success(CommandContext<Source> ctx, Runnable runnable, Component message) {
        log.debug("do executes: {}, {}", ctx, message);
        runnable.run();
        ctx.getSource().sendFeedback(message);
        return Command.SINGLE_SUCCESS;
    }

    private LiteralArgumentBuilder<Source> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    @RequiredArgsConstructor
    private static final class Source implements SharedSuggestionProvider {
        private final ClientSuggestionProvider underlying;

        void sendFeedback(Component message) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendMessage(message, Util.NIL_UUID);
            }
        }

        @Override
        @Nonnull
        public Collection<String> getOnlinePlayerNames() {
            return underlying.getOnlinePlayerNames();
        }

        @Override
        public Collection<String> getSelectedEntities() {
            return underlying.getSelectedEntities();
        }

        @Override
        public Collection<TextCoordinates> getRelevantCoordinates() {
            return underlying.getRelevantCoordinates();
        }

        @Override
        public Collection<TextCoordinates> getAbsoluteCoordinates() {
            return underlying.getAbsoluteCoordinates();
        }

        @Override
        public RegistryAccess registryAccess() {
            return underlying.registryAccess();
        }

        @Override
        @Nonnull
        public Collection<String> getAllTeams() {
            return underlying.getAllTeams();
        }

        @Override
        @Nonnull
        public Collection<ResourceLocation> getAvailableSoundEvents() {
            return underlying.getAvailableSoundEvents();
        }

        @Override
        @Nonnull
        public Stream<ResourceLocation> getRecipeNames() {
            return underlying.getRecipeNames();
        }

        @Override
        @Nonnull
        public CompletableFuture<Suggestions> customSuggestion(
            @Nonnull CommandContext<SharedSuggestionProvider> context, @Nonnull SuggestionsBuilder suggestionsBuilder) {
            return underlying.customSuggestion(context, suggestionsBuilder);
        }

        @Override
        public Set<ResourceKey<Level>> levels() {
            return underlying.levels();
        }

        @Override
        public boolean hasPermission(int permissionLevel) {
            return underlying.hasPermission(permissionLevel);
        }
    }
}
