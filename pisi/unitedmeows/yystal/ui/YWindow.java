package pisi.unitedmeows.yystal.ui;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_FOCUSED;
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
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
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
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
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
import pisi.unitedmeows.yystal.ui.font.StringCache;
import pisi.unitedmeows.yystal.ui.font.TTFRenderer;
import pisi.unitedmeows.yystal.ui.utils.NullCheck;
import pisi.unitedmeows.yystal.ui.utils.Rectangle;
import pisi.unitedmeows.yystal.ui.utils.Vertex2f;
import pisi.unitedmeows.yystal.ui.utils.YOrigin;
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
	public prop<Color> backgroundColor = new prop<>(Color.RED);

	private String getPathFromAssets(final String file) {
		if (file == null) /* add check for linux */
			return "/" + Object.class.getResource("/pisi/unitedmeows/yystal/ui/assets/").toString().substring(6);
		return "/" + Object.class.getResource(String.format("/pisi/unitedmeows/yystal/ui/assets/%s", file)).toString().substring(6) /* to get rid of file:/ */;
	}

	public YWindow(final String _title, final int _width, final int _height) {
		super(new Vertex2f(0, 0), new Vector2f(_width, _height), YOrigin.TOP_LEFT);
		title = _title;
	}

	public void open() {
		windowThread = new Thread(this::_open);
		windowThread.start();
	}

	public int width() {
		return Math.round(size.getX());
	}

	public int height() {
		return Math.round(size.getY());
	}

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

	public boolean isResizable() {
		return resizable;
	}

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
		loop();
	}

	public void close() {
		glfwSetWindowShouldClose(window, true);
	}

	private void loop() {
		int glErrCode;
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		double previousTime = glfwGetTime();
		int frameCount = 0;
		int lastCalculation = 0;
		glEnableClientState(GL11.GL_VERTEX_ARRAY);
		glMatrixMode(GL_PROJECTION);
		glOrtho(0, width(), height(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_TEXTURE_2D);
		final StringCache cache = new StringCache();
		cache.setDefaultFont(54F, true);
		final TTFRenderer ttfRenderer = new TTFRenderer(cache);
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glClearColor(backgroundColor.get().getRed(), backgroundColor.get().getGreen(), backgroundColor.get().getBlue(), 1.0F);
			final int mouseX = mouseCoords.getX().intValue();
			final int mouseY = mouseCoords.getY().intValue();
			final double currentTime = glfwGetTime();
			frameCount++;
			if (currentTime - previousTime >= 1.0) {
				lastCalculation = frameCount;
				frameCount = 0;
				previousTime = currentTime;
			}
			render: {
				final Rectangle rectangle = new Rectangle(0, 0, 800, 800);
				if (!NullCheck.CHECK.isNull(rectangle)) {
					rectangle.draw(-1, -8345634);
				}
				ttfRenderer.drawString(String.format("💀 fps -> %s %s", lastCalculation, Math.round((currentTime - previousTime) * 10) / 10D), 20, 60, Color.BLACK.getRGB(), false);
			}
			glfwPollEvents();
			glfwSwapBuffers(window);
			glErrCode = glGetError();
			if (glErrCode != GL_NO_ERROR) {
				System.out.printf("OpenGL Error %s %s", glErrCode, System.lineSeparator());
			}
		}
	}

	@Override
	public void draw() {}

	@Override
	public boolean isMouseOver(final float mouseX, final float mouseY) {
		return glfwGetWindowAttrib(window, GLFW_FOCUSED) == 1;
	}

	public float mouseX() {
		return mouseCoords.getX();
	}

	public float mouseY() {
		return mouseCoords.getY();
	}
}
