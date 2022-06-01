package pisi.unitedmeows.yystal.ui.utils;

import org.lwjgl.opengl.GL11;
import pisi.unitedmeows.yystal.ui.texture.YTexture;

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
        glVertex2d(startX, endY);
        glColor4f((endColor >> 16 & 255) / 255.0F, (endColor >> 8 & 255) / 255.0F,
                (endColor & 255) / 255.0F, (endColor >> 24 & 255) / 255.0F);
        glVertex2d(endX, endY);
        glVertex2d(endX, startY);
        glEnd();
        glPopMatrix();
        glDisable(GL_LINE_SMOOTH);
        glShadeModel(GL_FLAT);
    }

    public static void drawRect(float startX, float startY, float endX, float endY, int color) {
        drawRect(startX, startY, endX, endY, color, color);
    }

    public static void drawImage(int textureId, float x, float y, float width, float height) {
        glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
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
    }


}
