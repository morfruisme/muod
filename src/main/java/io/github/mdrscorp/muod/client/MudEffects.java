package io.github.mdrscorp.muod.client;

import java.util.ArrayList;
import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.mdrscorp.muod.Muod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MudEffects {
    private static final Identifier TEXTURE = new Identifier(Muod.MODID, "textures/gui/mud_effect.png");
    private static final int SIZE = 256;            // Size on screen in pixels (multiple of SPRITE_SIZE)
    private static final int SPRITE_SIZE = 32;
    private static final long FRAME_TIME = 600;     // Frame duration in milliseconds

    private static ArrayList<MudEffect> EFFECTS = new ArrayList<MudEffect>();

    public static void addEffect() {
        EFFECTS.add(new MudEffect());
    }

    public static void updateAndRender(MatrixStack matrices) {
        EFFECTS.removeIf(effect -> {
            long elapsed = System.currentTimeMillis() - effect.start;

            if (elapsed >= (effect.frame + 1)*FRAME_TIME)
                effect.frame += 1;

            if (effect.frame >= 9)
                return true;
            return false;
        });

        int x = MinecraftClient.getInstance().getWindow().getScaledWidth()/2;
        int y = MinecraftClient.getInstance().getWindow().getScaledHeight()/2;
        EFFECTS.forEach(effect -> effect.render(matrices, x, y));
    }

    public static class MudEffect {
        public int x;
        public int y;
        public long start;
        public int frame = 0;

        public MudEffect() {
            this.x = new Random().nextInt(300) - 150;
            this.y = new Random().nextInt(300) - 150;
            this.start = System.currentTimeMillis();
        }

        public void render(MatrixStack matrices, int x, int y) {
            x = x - SIZE/2 + this.x;
            y = y - SIZE/2 + this.y;
            int u = (this.frame % 3) * SPRITE_SIZE;
            int v = (this.frame / 3) * SPRITE_SIZE;

            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            DrawableHelper.drawTexture(matrices, x, y, SIZE, SIZE, u, v, SPRITE_SIZE, SPRITE_SIZE, 3*SPRITE_SIZE, 3*SPRITE_SIZE);
        }
    }
}
