package pisi.unitedmeows.yystal.ui.font;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform1iv;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

public class Shader {
	private int shaderProgramID;
	private boolean beingUsed = false;
	private String vertexSource;
	private String fragmentSource;
	private final String filepath;

	public Shader(final String realPath, final String vertex, final String fragment) {
		this.filepath = realPath;
		try {
			final String vertexa = new String(Files.readAllBytes(Paths.get(vertex)));
			vertexSource = vertexa;
			final String fragmento = new String(Files.readAllBytes(Paths.get(fragment)));
			fragmentSource = fragmento;
		} catch (final IOException e) {
			e.printStackTrace();
			assert false : "Error: Could not open file for shader: '" + filepath + "'";
		}
		compile();
	}

	public void compile() {
		// ============================================================
		// Compile and link shaders
		// ============================================================
		int vertexID , fragmentID;
		// First load and compile the vertex shader
		vertexID = glCreateShader(GL_VERTEX_SHADER);
		// Pass the shader source to the GPU
		glShaderSource(vertexID, vertexSource);
		glCompileShader(vertexID);
		// Check for errors in compilation
		int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			final int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed.");
			System.out.println(glGetShaderInfoLog(vertexID, len));
			assert false : "";
		}
		// First load and compile the vertex shader
		fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
		// Pass the shader source to the GPU
		glShaderSource(fragmentID, fragmentSource);
		glCompileShader(fragmentID);
		// Check for errors in compilation
		success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			final int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed.");
			System.out.println(glGetShaderInfoLog(fragmentID, len));
			assert false : "";
		}
		// Link shaders and check for errors
		shaderProgramID = glCreateProgram();
		glAttachShader(shaderProgramID, vertexID);
		glAttachShader(shaderProgramID, fragmentID);
		glLinkProgram(shaderProgramID);
		// Check for linking errors
		success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
		if (success == GL_FALSE) {
			final int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filepath + "'\n\tLinking of shaders failed.");
			System.out.println(glGetProgramInfoLog(shaderProgramID, len));
			assert false : "";
		}
	}

	public void use() {
		if (!beingUsed) {
			// Bind shader program
			glUseProgram(shaderProgramID);
			beingUsed = true;
		}
	}

	public void detach() {
		glUseProgram(0);
		beingUsed = false;
	}

	public void uploadMat4f(final String varName, final Matrix4f mat4) {
		final int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		final FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
		mat4.get(matBuffer);
		glUniformMatrix4fv(varLocation, false, matBuffer);
	}

	public void uploadMat3f(final String varName, final Matrix3f mat3) {
		final int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		final FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
		mat3.get(matBuffer);
		glUniformMatrix3fv(varLocation, false, matBuffer);
	}

	public void uploadVec4f(final String varName, final Vector4f vec) {
		final int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
	}

	public void uploadVec3f(final String varName, final Vector3f vec) {
		final int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform3f(varLocation, vec.x, vec.y, vec.z);
	}

	public void uploadVec2f(final String varName, final Vector2f vec) {
		final int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform2f(varLocation, vec.x, vec.y);
	}

	public void uploadFloat(final String varName, final float val) {
		final int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1f(varLocation, val);
	}

	public void uploadInt(final String varName, final int val) {
		final int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1i(varLocation, val);
	}

	public void uploadTexture(final String varName, final int slot) {
		final int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1i(varLocation, slot);
	}

	public void uploadIntArray(final String varName, final int[] array) {
		final int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1iv(varLocation, array);
	}
}
