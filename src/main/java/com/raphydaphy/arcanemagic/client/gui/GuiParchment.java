package com.raphydaphy.arcanemagic.client.gui;

import com.raphydaphy.arcanemagic.parchment.IParchment;
import com.raphydaphy.arcanemagic.parchment.ParchmentDrownedDiscovery;
import com.raphydaphy.arcanemagic.parchment.ParchmentRegistry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

// TODO: sideonly client
public class GuiParchment extends GuiScreen
{
    // x and y size of the parchment texture
    public static final int DIMENSIONS = 64;
    public static final int TEX_HEIGHT = 69;

    public static final int PROGRESS_BAR_LENGTH = 48;
    public static final int FULL_PROGRESS = PROGRESS_BAR_LENGTH - 2;

    public static final float SCALE = 3;
    public static final int SCALED_DIMENSIONS = (int) (DIMENSIONS * SCALE);

    private ItemStack stack;
    private IParchment parchment;

    public GuiParchment(ItemStack stack, IParchment parchment)
    {
        this.stack = stack;
        this.parchment = parchment;
    }

    @Override
    protected void initGui()
    {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        // The start x and y coords of the notebook on the screen
        int screenX = (mc.mainWindow.getScaledWidth() / 2) - (SCALED_DIMENSIONS / 2);
        int screenY = (mc.mainWindow.getScaledHeight() / 2) - (SCALED_DIMENSIONS / 2);

        parchment.drawParchment(stack,this, mc, screenX, screenY, mouseX, mouseY);

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (!super.mouseClicked(mouseX, mouseY, mouseButton))
        {
            if (mouseButton == 0)
            {
                System.out.println("left click");
            }

            return true;
        }
        return false;
    }
}