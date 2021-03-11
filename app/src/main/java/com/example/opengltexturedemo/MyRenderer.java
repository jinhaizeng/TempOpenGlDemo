package com.example.opengltexturedemo;

import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;


/**
 *
 */

public class MyRenderer implements GLSurfaceView.Renderer {
    private int program;
    private int vPosition;
    private int uColor;
    private FloatBuffer mVerticesBuffer;
    private FloatBuffer mColorsBuffer;
    private FloatBuffer mTextureBuffer;
    private ShortBuffer mIndicesBuffer;

    /**
     * 加载制定shader的方法
     * @param shaderType shader的类型  GLES32.GL_VERTEX_SHADER   GLES32.GL_FRAGMENT_SHADER
     * @param sourceCode shader的脚本
     * @return shader索引
     */
    private int loadShader(int shaderType,String sourceCode) {
        // 创建一个新shader
        int shader = GLES32.glCreateShader(shaderType);
        // 若创建成功则加载shader
        if (shader != 0) {
            // 加载shader的源代码
            GLES32.glShaderSource(shader, sourceCode);
            // 编译shader
            GLES32.glCompileShader(shader);
            // 存放编译成功shader数量的数组
            int[] compiled = new int[1];
            // 获取Shader的编译情况
            GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {//若编译失败则显示错误日志并删除此shader
                Log.e("ES20_ERROR", "Could not compile shader " + shaderType + ":");
                Log.e("ES20_ERROR", GLES32.glGetShaderInfoLog(shader));
                GLES32.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    /**
     * 创建shader程序的方法
     */
    private int createProgram(String vertexSource, String fragmentSource) {
        //加载顶点着色器
        int vertexShader = loadShader(GLES32.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        // 加载片元着色器
        int pixelShader = loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        // 创建程序
        int program = GLES32.glCreateProgram();
        // 若程序创建成功则向程序中加入顶点着色器与片元着色器
        if (program != 0) {
            // 向程序中加入顶点着色器
            GLES32.glAttachShader(program, vertexShader);
            // 向程序中加入片元着色器
            GLES32.glAttachShader(program, pixelShader);
            // 链接程序
            GLES32.glLinkProgram(program);
            // 存放链接成功program数量的数组
            int[] linkStatus = new int[1];
            // 获取program的链接情况
            GLES32.glGetProgramiv(program, GLES32.GL_LINK_STATUS, linkStatus, 0);
            // 若链接失败则报错并删除程序
            if (linkStatus[0] != GLES32.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program: ");
                Log.e("ES20_ERROR", GLES32.glGetProgramInfoLog(program));
                GLES32.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    float vertices[] = {
        //     ---- 位置 ----       ---- 颜色 ----     - 纹理坐标 -
            0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 1.0f,   // 右上
            0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f,   // 右下
            -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 0.0f,   // 左下
            -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 1.0f    // 左上
    };

    short indices[] = {
            0, 1, 3, // first triangle
            1, 2, 3  // second triangle
    };

    private void initVerticesBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * vertices.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        mVerticesBuffer = byteBuffer.asFloatBuffer();
        mVerticesBuffer.put(vertices);
        mVerticesBuffer.position(0);
    }

    private void intIndicesBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * indices.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        mIndicesBuffer = byteBuffer.asShortBuffer();
        mIndicesBuffer.put(indices);
        mIndicesBuffer.position(0);
    }
    /**
     * 当GLSurfaceView中的Surface被创建的时候(界面显示)回调此方法，一般在这里做一些初始化
     * @param gl10 1.0版本的OpenGL对象，这里用于兼容老版本，用处不大
     * @param eglConfig egl的配置信息(GLSurfaceView会自动创建egl，这里可以先忽略)
     */
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 初始化着色器
        // 基于顶点着色器与片元着色器创建程序
        program = createProgram(verticesShader, fragmentShader);

        // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES32.glClear()用的颜色值而不是执行清屏)
        GLES32.glClearColor(1.0f, 0, 0, 1.0f);

        initVerticesBuffer();
        intIndicesBuffer();
//        mVerticesBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        mVerticesBuffer.put(vertices).position(0);
//
//        mVerticesBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        mVerticesBuffer.put(vertices).position(0);
//
//        mVerticesBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        mVerticesBuffer.put(vertices).position(0);
    }

    /**
     * 当GLSurfaceView中的Surface被改变的时候回调此方法(一般是大小变化)
     * @param gl10 同onSurfaceCreated()
     * @param width Surface的宽度
     * @param height Surface的高度
     */
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // 设置绘图的窗口(可以理解成在画布上划出一块区域来画图)
        GLES32.glViewport(0,0,width,height);
    }

    /**
     * 当Surface需要绘制的时候回调此方法
     * 根据GLSurfaceView.setRenderMode()设置的渲染模式不同回调的策略也不同：
     * GLSurfaceView.RENDERMODE_CONTINUOUSLY : 固定一秒回调60次(60fps)
     * GLSurfaceView.RENDERMODE_WHEN_DIRTY   : 当调用GLSurfaceView.requestRender()之后回调一次
     * @param gl10 同onSurfaceCreated()
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDrawFrame(GL10 gl10) {
        // 清屏
        GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT | GLES32.GL_COLOR_BUFFER_BIT);

        // 使用某套shader程序
        GLES32.glUseProgram(program);

        int VAO,VBO,EBO;
        int[] array = new int[1];
        GLES32.glGenVertexArrays(1, array, 0);
        VAO = array[0];

        array = new int[2];
        GLES32.glGenBuffers(2, array, 0);
        VBO = array[0];
        EBO = array[1];

        // 为画笔指定顶点位置数据(vPosition)
        GLES32.glBindVertexArray(VAO);
        GLES32.glBindBuffer(GL_ARRAY_BUFFER, VBO);
        GLES32.glBufferData(GL_ARRAY_BUFFER, 4 * vertices.length, mVerticesBuffer, GL_STATIC_DRAW);

        GLES32.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        GLES32.glBufferData(GL_ELEMENT_ARRAY_BUFFER, 2 * indices.length, mIndicesBuffer, GL_STATIC_DRAW);

        // position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
        glEnableVertexAttribArray(0);
        // color attribute
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
        glEnableVertexAttribArray(1);
        // texture coord attribute
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
        glEnableVertexAttribArray(2);

        // 绘制
        GLES32.glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        GLES32.glBindVertexArray(0);
    }

    // 顶点着色器的脚本
    private static final String verticesShader
            = "layout (location = 0) in vec3 vPos;            \n" // 顶点位置属性vPosition
            + "layout (location = 1) in vec3 vColor;          \n"
            + "layout (location = 2) in vec3 vTexCoord;       \n"
            + "out vec3 ourColor;                             \n"//顶点着色器的输出变量和颜色着色器的输入变量
                                                                 // 只要名称一样，那么变量就能对应传递上
            + "out vec3 texCoord;                             \n"
            + "void main(){                         \n"
            + "   gl_Position = vec4(vPosition, 1.0);         \n" // 确定顶点位置
            + "   ourColor = vColor;                          \n"
            + "   texCoord = vTexCoord;                       \n"
            + "}";

    // 片元着色器的脚本
    private static final String fragmentShader
            = "out vec4 fragColor;              \n" // uniform的属性uColor
            + "in vec3 ourColor;                \n"
            + "in vec2 texCoord;                \n"
            + "uniform sampler2D ourTexture;    \n" // 纹理采样器
            + "void main(){                     \n"
            + "   fragColor = ourTexture(texCoord, ourColor);        \n" // 给此片元的填充色
            + "}";
}
