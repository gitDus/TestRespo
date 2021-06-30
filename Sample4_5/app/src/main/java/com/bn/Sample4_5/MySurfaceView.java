package com.bn.Sample4_5;
import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���
    private SceneRenderer mRenderer;//������Ⱦ��    
    
    private float mPreviousY;//�ϴεĴ���λ��Y����
    private float mPreviousX;//�ϴεĴ���λ��X����
    
    int textureId;//ϵͳ���������id
    int textureIdNormal;//ϵͳ���������id
	
	public MySurfaceView(Context context) {    
        super(context);
        this.setEGLContextClientVersion(3); //����ʹ��OPENGL ES 3.0
        mRenderer = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRenderer);				//������Ⱦ��		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ   
    }   
	
	//�����¼��ص�����
    @SuppressLint("ClickableViewAccessibility")
	@Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//���㴥�ر�Yλ��
            float dx = x - mPreviousX;//���㴥�ر�Xλ��
            mRenderer.xAngle += dx * TOUCH_SCALE_FACTOR;//������x����ת�Ƕ�
            mRenderer.yAngle+= dy * TOUCH_SCALE_FACTOR;//������y����ת�Ƕ�
            requestRender();//�ػ滭��
        }
        mPreviousY = y;//��¼���ر�λ��
        mPreviousX = x;//��¼���ر�λ��
        return true; 
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  
		float yAngle;//��Y����ת�ĽǶ�
    	float xAngle; //��X����ת�ĽǶ�
    	//��ָ����obj�ļ��м��ض���
    	LoadedObjectVertexNormalTexture lovo[]=new LoadedObjectVertexNormalTexture[2];//0--���а�͹��ͼ������   1--��ͨ����
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//�����Ȼ�������ɫ����
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            //����ϵ��Զ
//            MatrixState.pushMatrix();
//            MatrixState.translate(-15, 0f, -150f);
//            //��Y�ᡢZ����ת
//            MatrixState.rotate(xAngle, 0, 1, 0);
////            MatrixState.rotate(xAngle, 1, 0, 0);
//
//            //�����ص����岿λ�����������
//            if(lovo[0]!=null)
//            {
//            	lovo[0].drawSelf(textureId,textureIdNormal);
//            }
//            MatrixState.popMatrix();

            //����ϵ��Զ
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0f, -150f);
            //��Y�ᡢZ����ת
            MatrixState.rotate(xAngle, 0, 1, 0);
//            MatrixState.rotate(xAngle, 1, 0, 0);

            //�����ص����岿λ�����������
            if(lovo[1]!=null)
            {
            	lovo[1].drawSelfN(textureId);
            }
            MatrixState.popMatrix();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //�����Ӵ���С��λ�� 
        	GLES30.glViewport(0, 0, width, height); 
        	//����GLSurfaceView�Ŀ��߱�
            float ratio = (float) width / height;
            //���ô˷����������͸��ͶӰ����
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 400);
            //���ô˷������������9����λ�þ���
            MatrixState.setCamera(0,0,0,0f,0f,-1f,0f,1.0f,0.0f);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {
            //������Ļ����ɫRGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);    
            //����ȼ��
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //�򿪱������   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //��ʼ���任����
            MatrixState.setInitStack();
            //��ʼ����Դλ��
            MatrixState.setLightLocation(0, 10, 100);
            //����Ҫ���Ƶ�����            
            lovo=LoadUtil.loadFromFile("box.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            //��������
            textureId=initTexture(R.drawable.zs);    
            textureIdNormal=initTexture(R.drawable.gridnt);    
        }   
    }
  	public int initTexture(int drawableId)//textureId
	{
		//��������ID  
		int[] textures = new int[1];
		GLES30.glGenTextures
		(
				1,          //����������id������
				textures,   //����id������
				0           //ƫ����
		);    
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);
        
        //ͨ������������ͼƬ===============begin===================
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }
        //ͨ������������ͼƬ===============end=====================  
        
        //ʵ�ʼ�������
        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,   //��������
        		0, 					  //�����Ĳ�Σ�0��ʾ����ͼ��㣬��������Ϊֱ����ͼ
        		bitmapTmp, 			  //����ͼ��
        		0					  //�����߿�ߴ�
        );
        bitmapTmp.recycle(); 		  //�������سɹ����ͷ�ͼƬ
        return textureId;
	}
}