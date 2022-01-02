package pisi.unitedmeows.yystal.ui;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import pisi.unitedmeows.yystal.ui.font.Batch;
import pisi.unitedmeows.yystal.ui.font.Shader;
import pisi.unitedmeows.yystal.ui.font.data.CFont;
import pisi.unitedmeows.yystal.ui.font.data.CharInfo;
import pisi.unitedmeows.yystal.ui.utils.NullCheck;
import pisi.unitedmeows.yystal.ui.utils.Rectangle;
import pisi.unitedmeows.yystal.utils.Vector2f;

public class YWindow {
	private int width;
	private int height;
	private final Vector2f /* yystal vector2f :D.d:D */ mouseCoords = new Vector2f(-1F, -1F);
	private final Vector2f mouseDelta = new Vector2f(-1F, -1F);
	private final String title;
	private boolean resizable;
	private long window;
	private Thread windowThread = null;
	public static FloatBuffer fbModel = BufferUtils.createFloatBuffer(16);
	public static FloatBuffer fbView = BufferUtils.createFloatBuffer(16);
	public static FloatBuffer fbProjection = BufferUtils.createFloatBuffer(16);
	private CFont font;

	private String getPathFromAssets(final String file) {
		if (file == null) return Object.class.getResource("/pisi/unitedmeows/yystal/ui/assets/").toString().substring(6);
		return Object.class.getResource(String.format("/pisi/unitedmeows/yystal/ui/assets/%s", file)).toString().substring(6) /* to get rid of file:/ */;
	}

	public YWindow(final String _title, final int _width, final int _height) {
		title = _title;
		width = _width;
		height = _height;
	}

	public void open() {
		windowThread = new Thread(this::_open);
		windowThread.start();
	}

	public int width() { return width; }

	public int height() { return height; }

	public YWindow width(final int _width) {
		width = _width;
		size(width, height);
		return this;
	}

	public YWindow height(final int _height) {
		height = _height;
		size(width, height);
		return this;
	}

	public YWindow setResizable(final boolean state) {
		resizable = state;
		if (windowThread != null) {
			glfwWindowHint(GLFW_RESIZABLE, state ? GLFW_TRUE : GLFW_FALSE);
		}
		return this;
	}

	public YWindow size(final int _width, final int _height) {
		width = _width;
		height = _height;
		glfwSetWindowSize(window, width, height);
		return this;
	}

	public void hide() {
		if (windowThread != null) {
			glfwHideWindow(window);
		}
	}

	public void show() {
		if (windowThread != null) {
			glfwShowWindow(window);
		}
	}

	public boolean isResizable() { return resizable; }

	@SuppressWarnings("resource") // stfu ide
	private void _open() {
		if (!glfwInit()) /* todo: throw an exception */
			return;
		glfwDefaultWindowHints();
		/*
		 * antialiasing
		 */
		glfwWindowHint(GLFW_SAMPLES, 4);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
		window = glfwCreateWindow(width, height, title, NULL, NULL);
		if (window == NULL) /* throw an exception */
			return;
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true);
			}
		});
		glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
			@Override
			public void invoke(final long window, final double xpos, final double ypos) {
				mouseDelta.first((float) (xpos - mouseCoords.first()));
				mouseDelta.second((float) (ypos - mouseCoords.second()));
				mouseCoords.second((float) ypos);
				mouseCoords.first((float) xpos);
			}
		});
		// center the window
		// add several options like (CENTER, RANDOM, LEFT, RIGHT)
		try (MemoryStack stack = stackPush()) {
			final IntBuffer pWidth = stack.mallocInt(1);
			final IntBuffer pHeight = stack.mallocInt(1);
			glfwGetWindowSize(window, pWidth, pHeight);
			final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		}
		// enabling vsync <- cringe
		glfwSwapInterval(1);
		glfwShowWindow(window);
		// capabilities should be created last afaik...
		font = new CFont(getPathFromAssets("font.ttf"), 64);
		loop();
	}

	public void close() { glfwSetWindowShouldClose(window, true); }

	private void loop() {
		int glErrCode;
		final Shader fontShader = new Shader(getPathFromAssets(null), getPathFromAssets("fontShader.vertex"), getPathFromAssets("fontShader.fragment"));
		final Shader sdfShader = new Shader(getPathFromAssets(null), getPathFromAssets("sdfShader.vertex"), getPathFromAssets("sdfShader.fragment"));
		final Batch batch = new Batch();
		batch.shader = fontShader;
		batch.sdfShader = sdfShader;
		batch.font = font;
		batch.initBatch(this);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		final CharInfo oneQuad = new CharInfo(0, 0, 1, 1);
		oneQuad.calculateTextureCoordinates(1, 1);
		double previousTime = glfwGetTime();
		int frameCount = 0;
		int lastCalculation = 0;
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
			// rendering here
			final int mouseX = mouseCoords.first().intValue();
			final int mouseY = height - mouseCoords.second().intValue();
			final double currentTime = glfwGetTime();
			frameCount++;
			if (currentTime - previousTime >= 1.0) {
				lastCalculation = frameCount;
				frameCount = 0;
				previousTime = currentTime;
			}
			final Rectangle rectangle = new Rectangle(0, 0, 1920, 1080);
			batch.addText(String.format("fps -> %s", lastCalculation), 0, 0, 1F, getRainbow(0L, 1F).getRGB());
			batch.addText("font 2x :D", mouseX, mouseY + 70, 2.0F, 7964363);
			batch.addText("font :D", mouseX, mouseY, 1.0F, 0xAA01BB);
			batch.addText("font with 0.5 scale???", mouseX, mouseY - 35, 0.5F, getRainbow(0L, 1F).getRGB());
			batch.addText("font with 0.25 scale???", mouseX, mouseY - 60, 0.25F, 8281781);
			batch.addText("font with 0.125 scale???", mouseX, mouseY - 70, 0.125F, -1);
			batch.flushBatch();
			if (!NullCheck.CHECK.isNull(rectangle)) {
				rectangle.draw(1, 7964363);
			}
			glfwSwapBuffers(window);
			glfwPollEvents();
			glErrCode = glGetError();
			if (glErrCode != GL_NO_ERROR) {
				System.out.printf("OpenGL Error %s %s", glErrCode, System.lineSeparator());
			}
		}
	}

	public Color getRainbow(final long offset, final float fade) {
		final float hue = (System.nanoTime() + offset) / 5.0E9F % 1.0F;
		final long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0F, 1.0F)), 16);
		final Color c = new Color((int) color);
		return new Color(c.getRed() / 255.0F * fade, c.getGreen() / 255.0F * fade, c.getBlue() / 255.0F * fade, c.getAlpha() / 255.0F);
	}
}
