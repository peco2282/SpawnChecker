package net.awairo.minecraft.spawnchecker.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TranslationTextComponent extends TranslatableContents implements Component {

    public TranslationTextComponent(String p_237504_) {
        super(p_237504_);
    }

    public TranslationTextComponent(String p_237506_, Object... p_237507_) {
        super(p_237506_, p_237507_);
    }

    @Override
    public @NotNull Style getStyle() {
        return null;
    }

    @Override
    public @NotNull ComponentContents getContents() {
        return this;
    }

    @Override
    public @NotNull List<Component> getSiblings() {
        return null;
    }

    @Override
    public @NotNull FormattedCharSequence getVisualOrderText() {
        return obj -> true;
    }
}
