package pisi.unitedmeows.yystal.ui;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.clazz.prop;
import pisi.unitedmeows.yystal.exception.YExManager;
import pisi.unitedmeows.yystal.exception.impl.YexIO;
import pisi.unitedmeows.yystal.ui.element.IBackground;
import pisi.unitedmeows.yystal.ui.element.YContainer;
import pisi.unitedmeows.yystal.ui.element.YElement;
import pisi.unitedmeows.yystal.ui.element.simple.YBackgroundColor;
import pisi.unitedmeows.yystal.ui.events.KeyDownEvent;
import pisi.unitedmeows.yystal.ui.events.KeyUpEvent;
import pisi.unitedmeows.yystal.ui.events.MouseEvent;
import pisi.unitedmeows.yystal.ui.font.TTFRenderer;
import pisi.unitedmeows.yystal.ui.texture.YTexture;
import pisi.unitedmeows.yystal.ui.utils.*;
import pisi.unitedmeows.yystal.ui.utils.keyboard.YKeyManager;
import pisi.unitedmeows.yystal.utils.Vector2f;
import pisi.unitedmeows.yystal.utils.Vector4;
import pisi.unitedmeows.yystal.utils.kThread;

import javax.imageio.ImageIO;

public class YWindow extends YContainer {

	private final Vector2f mouseCoords = new Vector2f(-1F, -1F);
	private final Vector2f mouseDelta = new Vector2f(-1F, -1F);

	private long window;
	private Thread windowThread = null;


	public static FloatBuffer fbModel = BufferUtils.createFloatBuffer(16);
	public static FloatBuffer fbView = BufferUtils.createFloatBuffer(16);
	public static FloatBuffer fbProjection = BufferUtils.createFloatBuffer(16);


    public event<KeyDownEvent> keyDownEvent = new event<>();
    public event<KeyUpEvent> keyUpEvent = new event<>();
    public prop<YElement> focused = new prop<YElement>(this);
    private float partialTicks;

    private Queue<Runnable> executeQueue = new LinkedBlockingQueue<>();
    private final YKeyManager keyManager = new YKeyManager();

    public yuiprop<Boolean> resizable = new yuiprop<Boolean>(this,
            () -> false) {
        @Override
        public void set(Supplier<Boolean> _value) {
            final boolean newValue = _value.get();
            value = newValue;
            executeOnThread(() -> glfwWindowHint(GLFW_RESIZABLE, newValue ? GLFW_TRUE : GLFW_FALSE));
        }
    };

    public yuiprop<String> title = new yuiprop<String>(this, () -> "Unnamed YWindow") {
        @Override
        public void set(Supplier<String> _value) {
            final String newTitle = _value.get();
            value = newTitle;
            executeOnThread(() -> {
                glfwSetWindowTitle(window, newTitle);
            });
        }
    };



    public YWindow(final int _width, final int _height) {
        super(new Vertex2f(0, 0), new Vector2f(_width, _height), YOrigin.TOP_LEFT);
    }

	public YWindow(final String _title, final int _width, final int _height) {
		this(_width, _height);
		title.set(() -> _title);
	}

	public void open() {
		windowThread = new Thread(this::_open);
		windowThread.start();
	}

    public void waitForWindow() {
        while (!show) {
            kThread.sleep(3);
        }
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


	public YWindow size(final float _width, final float _height) {
		size.setX(_width);
		size.setY(_height);
		glfwSetWindowSize(window, Math.round(size.getX()), Math.round(size.getY()));
		return this;
	}

    @Override
	public void hide() {
		if (windowThread != null) {
			glfwHideWindow(window);
            show = false;
		}
	}

	public void show() {
		if (windowThread != null) {
			glfwShowWindow(window);
            show = true;
		}
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
		glfwWindowHint(GLFW_RESIZABLE, resizable.get() ? GLFW_TRUE : GLFW_FALSE);
		window = glfwCreateWindow(width(), height(), title.get(), NULL, NULL);
		if (window == NULL) /* throw an exception */ {
            YExManager.pop(new YexIO("Couldn't create the window"));
            return;
        }
        YUI.registerWindow(this);
		glfwMakeContextCurrent(window);
		GL.createCapabilities();

        glfwSetWindowSizeCallback(window, (window, width, height)-> {
            size.setX((float) width);
            size.setY((float) height);
            glViewport(0, 0, width,height);
        });

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE) {
                glfwSetWindowShouldClose(window, true);
			} else {
                switch (action) {
                    case 1: {
                        keyManager.press(key);
                        keyDownEvent.fire(key);
                        break;
                    }
                    case 0: {
                        keyManager.release(key);
                        keyUpEvent.fire(key);
                        break;
                    }
                }
            }
		});

        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {

            if (action == GLFW_PRESS) {
                focused.set(this);
                for (YElement element : elements()) {
                    if (element.isMouseOver(mouseX(), mouseY())) {
                        System.out.println("mouse over");
                        element.focus();
                        focused.set(element);
                        break;
                    }
                }


            }

            focused.get().mouseEvent.fire(MouseEvent.Button.parse(button), MouseEvent.Action.parse(action), mods);
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

        // fontrenderer will generate all sizes from 1 to 100 with increments of .5F
        // 30F is the default size

        setup();
        while (!executeQueue.isEmpty()) {
            executeQueue.poll().run();
        }

        show = true;
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glClearColor(0, 0, 0, 1.0F);
            final double currentTime = glfwGetTime();
            frameCount++;
            if (currentTime - previousTime >= 1.0) {
                lastCalculation = frameCount;
                frameCount = 0;
                previousTime = currentTime;
            }
            partialTicks = (float) (currentTime - previousTime);
            render:
            {
                glPushMatrix();
                draw();
                glPopMatrix();


                //    ttfRenderer.drawString(String.format("YWindow fps -> %s %s", lastCalculation, Math.round((currentTime - previousTime) * 10) / 10D), 20, 60, Color.BLACK.getRGB(), false);
                //		ttfRenderer.drawString(String.format("fps -> %s %s", lastCalculation, Math.round((currentTime - previousTime) * 10) / 10D), 20, 100, Color.BLACK.getRGB(), false);
            }

            glfwSwapBuffers(window);
            glErrCode = glGetError();
            if (glErrCode != GL_NO_ERROR) {
                System.out.printf("OpenGL Error %s %s", glErrCode, System.lineSeparator());
            }
            if (!executeQueue.isEmpty()) {
                executeQueue.poll().run();
            }
            glfwPollEvents();
        }
        YUI.unregisterWindow();

        glfwDestroyWindow(window);
    }

	@Override
	public void draw() {
        super.draw();
    }

    public void executeOnThread(Runnable runnable) {
        (executeQueue == null ? executeQueue = new LinkedBlockingQueue<>() : executeQueue).add(runnable);
    }


    @Override
    public void setup() {
        super.setup();
    }

    @Override
	public boolean isMouseOver(final float mouseX, final float mouseY) {
		return glfwGetWindowAttrib(window, GLFW_FOCUSED) == 1;
	}

    @Override
    public float renderX() {
        return 0;
    }

    @Override
    public float renderY() {
        return 0;
    }

    public float mouseX() {
		return mouseCoords.getX();
	}

	public float mouseY() {
		return mouseCoords.getY();
	}

    public float partialTicks() {
        return partialTicks;
    }

    public YKeyManager keyManager() {
        return keyManager;
    }
}
