package com.example.joysticktest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener{
    private float centerX;
    private float centerY;
    private float baseRadius;
    private float hatRadius;
    private JoystickListener joystickCallback;


    //Asetetaan koot tatille
    private void setupDimensions(){
        centerX = getWidth() / (float) 2;
        centerY = getHeight() / (float) 2;
        baseRadius = Math.min(getWidth(), getHeight()) / (float)4;
        hatRadius = Math.min(getWidth(), getHeight()) / (float)5;
    }

    public JoystickView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;
/*        if(context instanceof com.example.joysticktest.JoystickListener)
            joystickCallback = (JoystickListener) context;*/
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
        setOnTouchListener(this);
         if(context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;
/*       if(context instanceof com.example.joysticktest.JoystickListener)
            joystickCallback = (JoystickListener) context;*/
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;
/*       if(context instanceof com.example.joysticktest.JoystickListener)
            joystickCallback = (JoystickListener) context;*/
    }

    //Piirretään tatti
    private void drawJoystick(float newX, float newY){
        if(getHolder().getSurface().isValid()){
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint colors =new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            colors.setARGB(255, 50, 50, 50); //Pohjan värit
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors); //Piirrä pohja
            colors.setARGB(255, 0, 255, 255); //Tatin väri
            myCanvas.drawCircle(newX, newY, hatRadius, colors); //piirrä tatti
            getHolder().unlockCanvasAndPost(myCanvas); //Piirrä SurfaceView:iin

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        setupDimensions();
        drawJoystick(centerX, centerY);

    }

   @Override
   public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {

   }

   @Override
   public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

   }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.equals(this)) {
            //Liikutetaan tattia, jos käyttäjä koskettaa näyttöä
            if (motionEvent.getAction() != motionEvent.ACTION_UP) {
                float displacement = (float) Math.sqrt((Math.pow(motionEvent.getX() - centerX, 2)) + Math.pow(motionEvent.getY() - centerY, 2));
                if (displacement < baseRadius)
                    drawJoystick(motionEvent.getX(), motionEvent.getY());
                else {
                    float ratio = baseRadius / displacement;
                  //  float constrainedX = centerX + (motionEvent.getX() - centerX) * ratio;
                 //   float constrainedY = centerY + (motionEvent.getY() - centerX) * ratio;
//                    drawJoystick(constrainedX, constrainedY);

                    drawJoystick(motionEvent.getX(), motionEvent.getY()); //Piirretään tikku liikutettuun kohtaan
                    try {
//                        joystickCallback.onJoystickMoved((constrainedX - centerX) / baseRadius, (constrainedY - centerY) / baseRadius, getId());
                        //Kutsutaan funktiota, joka lähettää liikutetun suunnan MainActivityyn.
                       joystickCallback.onJoystickMoved((motionEvent.getX()-centerX) / baseRadius, (motionEvent.getY()-centerY) / baseRadius, getId());
                    } catch (NullPointerException ne) {
                        System.out.println("non");
                    }
//                    joystickCallback.onJoystickMoved((motionEvent.getX()-centerX) / baseRadius, (motionEvent.getY()-centerY) / baseRadius, getId());
                }
                //Jos käyttäjä taas nostaa sormensa pois näytöltä siirretään tatti takaisin keskelle
            } else {
                drawJoystick(centerX, centerY);
                try {
                    //Kerrotaan MainActivitylle, että tatti on keskellä
                    joystickCallback.onJoystickMoved(0, 0, getId());
                } catch (NullPointerException ne) {
                    System.out.println("non");
                }

            }
        }
        return true;
    }

    public interface JoystickListener{
        void onJoystickMoved(float xPercent, float yPercent, int id);
    }

}

