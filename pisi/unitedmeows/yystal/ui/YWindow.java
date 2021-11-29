package pisi.unitedmeows.yystal.ui;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class YWindow {

	private int width;
	private int height;
	private String title;
	private boolean resizable;

	private long window;
	private Thread windowThread = null;

	public YWindow(String _title, int _width, int _height) {
		title = _title;
		width = _width;
		height = _height;
	}


	public void open() {
		windowThread = new Thread(this::_open);
		windowThread.start();
	}

	public YWindow width(int _width) {
		width = _width;
		size(width, height);
		return this;
	}

	public YWindow height(int _height) {
		height = _height;
		size(width, height);
		return this;
	}

	public void setResizable(boolean state) {
		resizable = state;
		if (windowThread != null) {
			glfwWindowHint(GLFW_RESIZABLE, state ? GLFW_TRUE : GLFW_FALSE);
		}
	}

	public YWindow size(int _width, int _height) {
		width = _width;
		height = _height;
		glfwSetWindowSize(window, width, height);
		return this;
	}

	public void hide() {
		if (windowThread != null)
			glfwHideWindow(window);
	}

	public void show() {
		if (windowThread != null)
			glfwShowWindow(window);
	}

	public boolean isResizable() {
		return resizable;
	}

	private void _open() {
		if (!glfwInit()) {
			/*todo: throw an exception */
			return;
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);

		window = glfwCreateWindow(width, height, title, NULL, NULL);
		if (window == NULL) {
			/* throw an exception */
			return;
		}

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// center the window
		// add several options like (CENTER, RANDOM, LEFT, RIGHT)
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically
		glfwMakeContextCurrent(window);

		// enabling vsync
		glfwSwapInterval(1);

		glfwShowWindow(window);

		loop();
	}

	public void close() {
		glfwSetWindowShouldClose(window, true);
	}

	private void loop() {
		GL.createCapabilities();

		// Set the clear color
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}
}
