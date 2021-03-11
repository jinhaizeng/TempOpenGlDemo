package com.example.opengltexturedemo;

import android.content.Context;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.Log;

import androidx.annotation.LongDef;
import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BUFFER_SIZE;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES30.GL_MAP_READ_BIT;

public class GLTextureRender implements GLSurfaceView.Renderer {
    private FloatBuffer mVerticesBuffer;
    private FloatBuffer mColorsBuffer;
    private FloatBuffer mTextureBuffer;
    private ShortBuffer mIndicesBuffer;
    private Context mContext;
    private int mProgram;
    private int mVAO, mVBO, mEBO, mVCO;
    private int mWidth, mHeight;
    private static final String TAG = "GLTextureRender";
    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_SHORT = 2;
    //数组定义
    private final float[] mVerticesData =
            {
                    -0.5f, 0.5f, 0.0f,
                    -0.5f, -0.5f, 0.0f,
                    0.5f, -0.5f, 0.0f,
                    0.5f, 0.5f, 0.0f,
            };

    private final float[] mColorsData =
            {
                    1.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f,
            };

    private final float[] mTextureData =
            {
                    0.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
            };

    private final short[] mIndicesData =
            {
                    0, 1, 2,
                    0, 2, 3,
            };

    public GLTextureRender(Context context) {
        mContext = context;
        mVerticesBuffer = initBuffer(mVerticesData);
        mVerticesBuffer.put(mVerticesData);
        mVerticesBuffer.position(0);

        mColorsBuffer = initBuffer(mColorsData);
        mColorsBuffer.put(mColorsData);
        mColorsBuffer.position(0);

        mTextureBuffer = initBuffer(mTextureData);
        mTextureBuffer.put(mTextureData);
        mTextureBuffer.position(0);

        mIndicesBuffer = initBuffer(mIndicesData);
        mIndicesBuffer.put(mIndicesData);
        mIndicesBuffer.position(0);
    }

    public FloatBuffer initBuffer(float[] data) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BYTES_PER_FLOAT * data.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        return byteBuffer.asFloatBuffer();
    }

    public ShortBuffer initBuffer(short[] data) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BYTES_PER_SHORT * data.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        return byteBuffer.asShortBuffer();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mProgram = createProgram(OpenGLUtils.readTextFileFromResource(mContext, R.raw.gltexture_vertex),
                OpenGLUtils.readTextFileFromResource(mContext, R.raw.gltexture_fragment));
        int[] array = new int[1];
        GLES32.glGenVertexArrays(1, array, 0);
        mVAO = array[0];
        array = new int[2];
        GLES32.glGenBuffers(2, array, 0);
        mVBO = array[0];
        mEBO = array[1];
        loadBufferDate();
        Log.d(TAG, "onSurfaceCreated, mProgram: " + mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
        Log.d(TAG, "onSurfaceChanged, mWidth: " + mWidth + ", mHeight: " + mHeight);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d(TAG, "onSurfaceCreated, onDrawFrame");
        GLES32.glViewport(0, 0 , mWidth, mHeight);
        GLES32.glClear(GL_COLOR_BUFFER_BIT);
        GLES32.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES32.glUseProgram(mProgram);

        GLES32.glEnableVertexAttribArray(0);
//        GLES32.glEnableVertexAttribArray(1);
//        GLES32.glEnableVertexAttribArray(2);

        GLES32.glBindVertexArray(mVAO);

        Log.d(TAG, "mIndicesData.length: " + mIndicesData.length + "mIndicesBuffer: " + mIndicesBuffer);
        GLES32.glDrawElements(GL_TRIANGLES, mIndicesData.length, GL_UNSIGNED_SHORT, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void loadBufferDate() {
        GLES32.glBindVertexArray(mVAO);

        mVerticesBuffer.position(0);
        GLES32.glBindBuffer(GL_ARRAY_BUFFER, mVBO);
        GLES32.glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mVerticesData.length,
                mVerticesBuffer, GL_STATIC_DRAW);
        GLES32.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        GLES32.glEnableVertexAttribArray(0);

//        mColorsBuffer.position(0);
//        GLES32.glBindBuffer(GL_ARRAY_BUFFER, mVCO);
//        GLES32.glBufferData(GL_ARRAY_BUFFER, BYTES_PER_FLOAT * mColorsData.length,
//                mColorsBuffer, GL_STATIC_DRAW);
//        GLES32.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
//        GLES32.glEnableVertexAttribArray(1);

        mIndicesBuffer.position(0);
        GLES32.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mEBO);
        GLES32.glBufferData(GL_ELEMENT_ARRAY_BUFFER, BYTES_PER_SHORT  * mIndicesData.length,
                mIndicesBuffer, GL_STATIC_DRAW);
    }
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
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES32.glGetShaderInfoLog(shader));
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
        Log.d(TAG, "program: " + program + ", vertexShader: " + vertexShader
                + ", pixelShader: " + pixelShader);
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
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES32.glGetProgramInfoLog(program));
                GLES32.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
}
