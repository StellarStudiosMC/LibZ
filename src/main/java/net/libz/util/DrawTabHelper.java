package net.libz.util;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.cottonmc.cotton.gui.client.LibGui;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.libz.LibzClient;
import net.libz.api.InventoryTab;
import net.libz.api.Tab;
import net.libz.init.ConfigInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class DrawTabHelper {

    public static void drawTab(MinecraftClient client, MatrixStack matrices, Screen screenClass, int x, int y, int mouseX, int mouseY) {
        if (client != null && client.player != null && ConfigInit.CONFIG.inventoryButton && (Object) screenClass instanceof Tab) {

            int xPos = x;
            Text shownTooltip = null;
            for (int i = 0; i < LibzClient.inventoryTabs.size(); i++) {
                InventoryTab inventoryTab = LibzClient.inventoryTabs.get(i);
                if (inventoryTab.shouldShow(client)) {

                    boolean isFirstTab = i == 0;
                    boolean isSelectedTab = inventoryTab.isSelectedScreen(screenClass.getClass());

                    int textureX = isFirstTab ? 24 : 72;
                    if (isSelectedTab) {
                        textureX -= 24;
                    }
                    if (LibzClient.isLibGuiLoaded && LibGui.isDarkMode()) {
                        textureX += 96;
                    }

                    RenderSystem.setShaderTexture(0, LibzClient.inventoryTabTexture);
                    screenClass.drawTexture(matrices, xPos, isSelectedTab ? y - 23 : y - 21, textureX, 0, 24, isSelectedTab ? 27 : isFirstTab ? 25 : 21);
                    if (inventoryTab.getTexture() != null) {
                        RenderSystem.setShaderTexture(0, inventoryTab.getTexture());
                        DrawableHelper.drawTexture(matrices, xPos + 5, y - 16, 0, 0, 14, 14, 14, 14);
                    } else if (inventoryTab.getItemStack() != null) {
                        client.getItemRenderer().renderInGui(inventoryTab.getItemStack(), xPos + 4, y - 17);
                    }

                    if (!isSelectedTab && isPointWithinBounds(x, y, xPos - x + 1, -20, 22, 19, (double) mouseX, (double) mouseY)) {
                        shownTooltip = inventoryTab.getTitle();
                    }
                    xPos += 25;
                }
            }
            if (shownTooltip != null) {
                screenClass.renderTooltip(matrices, shownTooltip, mouseX, mouseY);
            }
        }
    }

    public static void onTabButtonClick(MinecraftClient client, Screen screenClass, int x, int y, double mouseX, double mouseY, boolean focused) {
        if (client != null && ConfigInit.CONFIG.inventoryButton && !focused && screenClass instanceof Tab) {
            int xPos = x;
            for (int i = 0; i < LibzClient.inventoryTabs.size(); i++) {
                InventoryTab inventoryTab = LibzClient.inventoryTabs.get(i);
                if (inventoryTab.shouldShow(client)) {
                    boolean isSelectedTab = inventoryTab.isSelectedScreen(screenClass.getClass());
                    if (inventoryTab.canClick(screenClass.getClass(), client)
                            && isPointWithinBounds(x, y, xPos - x + 1, isSelectedTab ? -24 : -20, 22, isSelectedTab ? 23 : 19, (double) mouseX, (double) mouseY)) {
                        inventoryTab.onClick(client);
                    }
                    xPos += 25;
                }
            }
        }
    }

    private static boolean isPointWithinBounds(int xPos, int yPos, int x, int y, int width, int height, double pointX, double pointY) {
        return (pointX -= (double) xPos) >= (double) (x - 1) && pointX < (double) (x + width + 1) && (pointY -= (double) yPos) >= (double) (y - 1) && pointY < (double) (y + height + 1);
    }
}
