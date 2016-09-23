package com.bytehamster.flowit;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.bytehamster.flowit.state.ExitState;
import com.bytehamster.flowit.state.GameState;
import com.bytehamster.flowit.state.LevelPackSelectState;
import com.bytehamster.flowit.state.LevelSelectState;
import com.bytehamster.flowit.state.MainMenuState;
import com.bytehamster.flowit.state.SettingsState;
import com.bytehamster.flowit.state.State;

public class Main extends Activity {
    private MyGLSurfaceView glSurfaceView;
    private SoundPool soundPool;
    private State currentState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void createViews() {
        setContentView(R.layout.main);
        glSurfaceView = (MyGLSurfaceView) findViewById(R.id.gl_surface_view);

        glSurfaceView.getRenderer().setOnReady(new Runnable() {
            @Override
            public void run() {
                soundPool = new SoundPool(Main.this);
                soundPool.loadSound(R.raw.click);
                soundPool.loadSound(R.raw.fill);
                soundPool.loadSound(R.raw.won);

                MainMenuState.getInstance().initialize(glSurfaceView.getRenderer(), soundPool, Main.this);
                ExitState.getInstance().initialize(glSurfaceView.getRenderer(), soundPool, Main.this);
                SettingsState.getInstance().initialize(glSurfaceView.getRenderer(), soundPool, Main.this);
                LevelPackSelectState.getInstance().initialize(glSurfaceView.getRenderer(), soundPool, Main.this);
                LevelSelectState.getInstance().initialize(glSurfaceView.getRenderer(), soundPool, Main.this);
                GameState.getInstance().initialize(glSurfaceView.getRenderer(), soundPool, Main.this);

                currentState = MainMenuState.getInstance();
                currentState.entry();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(currentState != null) {
            currentState.onKeyDown(keyCode, event);
            switchState();
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(currentState != null) {
            currentState.onTouchEvent(event);
            switchState();
        }
        return false;
    }

    private void switchState() {
        State newState = currentState.next();
        if (currentState != newState) {
            currentState.exit();
            currentState = newState;
            currentState.entry();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        createViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }
}
