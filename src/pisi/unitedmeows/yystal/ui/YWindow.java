package pisi.unitedmeows.yystal.ui;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.prop;
import pisi.unitedmeows.yystal.ui.element.YContainer;
import pisi.unitedmeows.yystal.ui.font.Batch;
import pisi.unitedmeows.yystal.ui.font.Shader;
import pisi.unitedmeows.yystal.ui.font.data.CFont;
import pisi.unitedmeows.yystal.ui.font.data.CharInfo;
import pisi.unitedmeows.yystal.ui.utils.*;
import pisi.unitedmeows.yystal.utils.Vector2f;

public class YWindow extends YContainer {

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
	public prop<Color> backgroundColor = new prop<>(Color.RED);


	private String getPathFromAssets(final String file) {
		if (file == null) /* add check for linux */
			return "/" + Object.class.getResource("/pisi/unitedmeows/yystal/ui/assets/").toString().substring(6);
		return "/" + Object.class.getResource(String.format("/pisi/unitedmeows/yystal/ui/assets/%s", file)).toString().substring(6) /* to get rid of file:/ */;
	}

	public YWindow(final String _title, final int _width, final int _height) {
		super(new Vertex2f(0, 0), new Vector2f((float)_width, (float)_height), YOrigin.TOP_LEFT);
		title = _title;
	}

	public void open() {
		windowThread = new Thread(this::_open);
		windowThread.start();
	}

	public int width() { return Math.round(size.getX()); }

	public int height() { return Math.round(size.getY()); }

	public YWindow width(final int _width) {
		size(_width, size.getY());
		return this;
	}

	public YWindow height(final int _height) {
		size(size.getX(), _height);
		return this;
	}

	public YWindow setResizable(final boolean state) {
		resizable = state;
		if (windowThread != null) {
			glfwWindowHint(GLFW_RESIZABLE, state ? GLFW_TRUE : GLFW_FALSE);
		}
		return this;
	}

	public YWindow size(final float _width, final float _height) {
		size.setX(_width);
		size.setY(_height);
		glfwSetWindowSize(window, Math.round(size.getX()), Math.round(size.getY()));
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

		window = glfwCreateWindow(width(), height(), title, NULL, NULL);
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
				mouseDelta.setX((float) (xpos - mouseCoords.getX()));
				mouseDelta.setY((float) (ypos - mouseCoords.getY()));
				mouseCoords.setY((float) ypos);
				mouseCoords.setX((float) xpos);
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
		YYStal.registerWindow(this);
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
		glEnableClientState(GL11.GL_VERTEX_ARRAY);
		glMatrixMode(GL_PROJECTION);
		glOrtho(0, width(), height(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_TEXTURE_2D);
		while (!glfwWindowShouldClose(window)) {


			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glClearColor(backgroundColor.get().getRed(), backgroundColor.get().getGreen(), backgroundColor.get().getBlue(), 1.0F);
			// rendering here
			final int mouseX = mouseCoords.getX().intValue();
			final int mouseY = height() - mouseCoords.getY().intValue();
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
			batch.addText("font with 0.5 scale???", mouseX, mouseY - 35, 5, getRainbow(0L, 1F).getRGB());
			batch.addText("font with 0.25 scale???", mouseX, mouseY - 60, 0.25F, 8281781);
			batch.addText("font with 0.125 scale???", mouseX, mouseY - 70, 0.125F, -1);
			batch.flushBatch();

			if (!NullCheck.CHECK.isNull(rectangle)) {
				rectangle.draw(-8345634);
			}




			glfwPollEvents();
			glfwSwapBuffers(window);
			glErrCode = glGetError();
			if (glErrCode != GL_NO_ERROR) {
				//System.out.printf("OpenGL Error %s %s", glErrCode, System.lineSeparator());
			}
		}
	}

	@Override
	public void draw() {

	}

	@Override
	public boolean isMouseOver(float mouseX, float mouseY) {
		return glfwGetWindowAttrib(window, GLFW_FOCUSED) == 1;
	}

	public float mouseX() {
		return mouseCoords.getX();
	}

	public float mouseY() {
		return mouseCoords.getY();
	}

	public Color getRainbow(final long offset, final float fade) {
		final float hue = (System.nanoTime() + offset) / 5.0E9F % 1.0F;
		final long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0F, 1.0F)), 16);
		final Color c = new Color((int) color);
		return new Color(c.getRed() / 255.0F * fade, c.getGreen() / 255.0F * fade, c.getBlue() / 255.0F * fade, c.getAlpha() / 255.0F);
	}
}