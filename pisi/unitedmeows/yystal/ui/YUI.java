package pisi.unitedmeows.yystal.ui;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;
import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.ui.texture.YTexture;
import pisi.unitedmeows.yystal.utils.Vector2f;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

public class YUI {

    private static final HashMap<Thread, YWindow> windowMap;
    private static Map<Thread, Map<String, YTexture>> loadedTextures;

    static {
        loadedTextures = new HashMap<>();
        windowMap = new HashMap<Thread, YWindow>();
    }

    public static Map<String, YTexture> textureMap() {
        return loadedTextures.compute(Thread.currentThread(), (thread, stringYTextureMap) ->
            new HashMap<String, YTexture>());
    }
   
    public static YTexture texture(String name) {
        return textureMap().getOrDefault(name, null);
    }

    public static YTexture loadTexture(String name, BufferedImage image) {
        final YTexture texture = loadTexture(image);
        textureMap().put(name, texture);
        return texture;
    }

    public static YTexture loadTextureRGB(String name, BufferedImage image) {
        final YTexture texture = loadTextureRGB(image);
        textureMap().put(name, texture);
        return texture;
    }

    public static void freeTexture(int textureId) {
        glDeleteTextures(textureId);
    }


    public static YTexture loadTextureRGB(BufferedImage image) {
        final ByteBuffer buffer = createBufferFromImageRGB(image);
        int textureID = glGenTextures(); //Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID

        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);

        //Return the texture ID so we can bind it later again
        return new YTexture(textureID);
    }


    public static YTexture loadTexture(BufferedImage image){
        final ByteBuffer buffer = createBufferFromImageARGB(image);

        int textureID = glGenTextures(); //Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID

        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        //Return the texture ID so we can bind it later again
        return new YTexture(textureID);
    }

    public static ByteBuffer createBufferFromImageARGB(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();
        return buffer;
    }

    public static ByteBuffer createBufferFromImageRGB(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 3); //4 for RGBA, 3 for RGB

        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
            }
        }

        buffer.flip();
        return buffer;
    }

    public Vector2f mousePosition() { return new Vector2f(mouseX(), mouseY()); }

    public static float mouseX() { return YYStal.currentWindow().mouseX(); }

    public static float mouseY() { return YYStal.currentWindow().mouseY();}

    public static YWindow currentWindow() { return windowMap.getOrDefault(Thread.currentThread(), null); }

    public static void registerWindow(YWindow window) {
        windowMap.put(Thread.currentThread(), window);
    }

    public static boolean unregisterWindow() { return windowMap.remove(Thread.currentThread()) != null; }

    public static boolean unregisterWindow(YWindow window) {
        Iterator<Map.Entry<Thread, YWindow>> iterator = windowMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Thread, YWindow> entry = iterator.next();
            if (entry.getValue() == window) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
    
}