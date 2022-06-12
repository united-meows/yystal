package pisi.unitedmeows.yystal.ui.utils;

import org.lwjgl.opengl.GL11;
import pisi.unitedmeows.yystal.ui.texture.YTexture;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FLAT;

public class RenderMethods {
    
    public static void drawRect(float startX, float startY, float endX, float endY, int startColor, int endColor) {
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);
        glPushMatrix();
        glBegin(GL_QUADS);
        glColor4f((startColor >> 16 & 255) / 255.0F, (startColor >> 8 & 255) / 255.0F, (startColor & 255) / 255.0F,
                (startColor >> 24 & 255) / 255.0F);
        glVertex2d(startX, startY);

        glColor4f((endColor >> 16 & 255) / 255.0F, (endColor >> 8 & 255) / 255.0F,
                (endColor & 255) / 255.0F, (endColor >> 24 & 255) / 255.0F);
        glVertex2d(startX, endY);

        glVertex2d(endX, endY);

        glColor4f((startColor >> 16 & 255) / 255.0F, (startColor >> 8 & 255) / 255.0F, (startColor & 255) / 255.0F,
                (startColor >> 24 & 255) / 255.0F);
        glVertex2d(endX, startY);
        glEnd();
        glPopMatrix();
        glDisable(GL_LINE_SMOOTH);
        glShadeModel(GL_FLAT);
    }

    public static void drawRect(float startX, float startY, float endX, float endY, int color) {
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);
        glPushMatrix();
        glBegin(GL_QUADS);
        glColor4f((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F,
                (color >> 24 & 255) / 255.0F);
        glVertex2d(startX, startY);
        glVertex2d(startX, endY);

        glVertex2d(endX, endY);

        glVertex2d(endX, startY);
        glEnd();
        glPopMatrix();
        glDisable(GL_LINE_SMOOTH);
        glShadeModel(GL_FLAT);
    }

    public static void drawBorderedRect(float startX, float startY, float endX, float endY, float borderSize, int insideColor, int borderColor) {
        drawRect(startX + borderSize, startY + borderSize, endX - borderSize, endY - borderSize, insideColor);
        drawRect(startX + borderSize, startY, endX - borderSize, startY + borderSize, borderColor);
        drawRect(startX, startY, startX + borderSize, endY, borderColor);
        drawRect(endX - borderSize, startY, endX, endY, borderColor);
        drawRect(startX + borderSize, endY - borderSize, endX - borderSize, endY, borderColor);
    }

    public static void drawRoundedRect(float x2, float y2, float x1, float y1, int color) {
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        drawVLine(x2 *= 2.0f, (y2 *= 2.0f) + 1.0f, (y1 *= 2.0f) - 2.0f, color);
        drawVLine((x1 *= 2.0f) - 1.0f, y2 + 1.0f, y1 - 2.0f, color);
        drawHLine(x2 + 2.0f, x1 - 3.0f, y2, color);
        drawHLine(x2 + 2.0f, x1 - 3.0f, y1 - 1.0f, color);
        drawHLine(x2 + 1.0f, x2 + 1.0f, y2 + 1.0f, color);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y2 + 1.0f, color);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, color);
        drawHLine(x2 + 1.0f, x2 + 1.0f, y1 - 2.0f, color);
        GL11.glPushMatrix();
        drawRect(x2 + 1.0f, y2 + 1.0f, x1 - 1.0f, y1 - 1.0f, color);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    public static void drawRoundedRect(float x2, float y2, float x1, float y1, int color, int color2) {
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        drawVLine(x2 *= 2.0f, (y2 *= 2.0f) + 1.0f, (y1 *= 2.0f) - 2.0f, color, color2);
        drawVLine((x1 *= 2.0f) - 1.0f, y2 + 1.0f, y1 - 2.0f, color, color2);
        drawHLine(x2 + 2.0f, x1 - 3.0f, y2, color, color2);
        drawHLine(x2 + 2.0f, x1 - 3.0f, y1 - 1.0f, color, color2);
        drawHLine(x2 + 1.0f, x2 + 1.0f, y2 + 1.0f, color, color2);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y2 + 1.0f, color, color2);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, color, color2);
        drawHLine(x2 + 1.0f, x2 + 1.0f, y1 - 2.0f, color, color2);
        GL11.glPushMatrix();
        drawRect(x2 + 1.0f, y2 + 1.0f, x1 - 1.0f, y1 - 1.0f, color, color2);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }


    public static void drawRoundedBorderedRect(float startX, float startY,
                                               float endX, float endY,
                                               float borderSize,
                                               int insideC, int borderC) {
        drawRoundedRect(startX - borderSize, startY - borderSize, endX + borderSize, endY + borderSize
        , borderC);
        drawRoundedRect(startX, startY, endX, endY, insideC);
    }

    public static void drawRoundedBorderedRect(float startX, float startY,
                                               float endX, float endY,
                                               float borderSize,
                                               int insideC, int insideC2, int borderC) {
        drawRoundedRect(startX - borderSize, startY - borderSize, endX + borderSize, endY + borderSize
                , borderC);
        drawRoundedRect(startX, startY, endX, endY, insideC, insideC2);
    }


    public static void drawRoundedBorderedRect(float x2, float y2, float x1, float y1, int borderC, int insideC) {
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        drawVLine(x2 *= 2.0f, (y2 *= 2.0f) + 1.0f, (y1 *= 2.0f) - 2.0f, borderC);
        drawVLine((x1 *= 2.0f) - 1.0f, y2 + 1.0f, y1 - 2.0f, borderC);
        drawHLine(x2 + 2.0f, x1 - 3.0f, y2, borderC);
        drawHLine(x2 + 2.0f, x1 - 3.0f, y1 - 1.0f, borderC);
        drawHLine(x2 + 1.0f, x2 + 1.0f, y2 + 1.0f, borderC);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y2 + 1.0f, borderC);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, borderC);
        drawHLine(x2 + 1.0f, x2 + 1.0f, y1 - 2.0f, borderC);
        drawRect(x2 + 1.0f, y2 + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }

    public static void drawImage(int textureId, float x, float y, float width, float height) {
        glPushMatrix();
        glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTranslatef(x, y, 0);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(0, 0);
        glTexCoord2f(1, 0);
        glVertex2f(width, 0);
        glTexCoord2f(1, 1);
        glVertex2f(width, height);
        glTexCoord2f(0, 1);
        glVertex2f(0, height);
        glEnd();
        glLoadIdentity();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void drawCircle(float cx, float cy, float r, int num_segments, int c) {
        r *= 2.0F;
        cx *= 2.0F;
        cy *= 2.0F;
        float f = (c >> 24 & 0xFF) / 255.0F;
        float f1 = (c >> 16 & 0xFF) / 255.0F;
        float f2 = (c >> 8 & 0xFF) / 255.0F;
        float f3 = (c & 0xFF) / 255.0F;
        float theta = (float)(6.2831852D / num_segments);
        float p = (float)Math.cos(theta);
        float s = (float)Math.sin(theta);
        float x = r;
        float y = 0.0F;
        enableGL2D();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(2);
        for (int ii = 0; ii < num_segments; ii++) {
            GL11.glVertex2f(x + cx, y + cy);
            float t = x;
            x = p * x - s * y;
            y = s * t + p * y;
        }
        GL11.glEnd();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        disableGL2D();
    }



    public static void drawHLine(float x, float y, float x1, int color) {
        if (y < x) {
            float var5 = x;
            x = y;
            y = var5;
        }
        drawRect(x, x1, y + 1.0F, x1 + 1.0F, color);
    }


    public static void drawVLine(float x, float y, float x1, int color) {
        if (x1 < y) {
            float var5 = y;
            y = x1;
            x1 = var5;
        }
        drawRect(x, y + 1.0F, x + 1.0F, x1, color);
    }

    public static void drawHLine(float x, float y, float x1, int color, int color2) {
        if (y < x) {
            float var5 = x;
            x = y;
            y = var5;
        }
        drawRect(x, x1, y + 1.0F, x1 + 1.0F, color, color2);
    }


    public static void drawVLine(float x, float y, float x1, int color, int color2) {
        if (x1 < y) {
            float var5 = y;
            y = x1;
            x1 = var5;
        }
        drawRect(x, y + 1.0F, x + 1.0F, x1, color, color2);
    }





    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }


    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }



}
