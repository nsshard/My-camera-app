package com.example.cw2program;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
// Import necessary stuff
public class buttonsmove extends Activity {
    Intent movetoact;
//Using intent of button
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Basic stuff
        Button Deeeee=(Button)findViewById(R.id.btn_locate);
    }

    public void move()
    {
        movetoact = new Intent(this,compass.class);
        startActivity(movetoact);
    }

}