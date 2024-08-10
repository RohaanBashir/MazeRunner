package com.example.maze;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button path;
    Boolean pressed = false;
    Button GenerateBtn;
    Draw DrawView;
    Button Stop;
    Button Reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Views Ids
        DrawView = findViewById(R.id.draw);
        GenerateBtn = findViewById(R.id.button);
        Stop = findViewById(R.id.Stop);
        Reset = findViewById(R.id.Reset);
        path = findViewById(R.id.path);


        GenerateBtn.setOnClickListener(v -> {
            DrawView.StartDrawing();

            //setting the visibility of the stop and reset buttons
            Stop.setVisibility(View.VISIBLE);
            Reset.setVisibility(View.VISIBLE);
            Stop.setEnabled(true);
            Reset.setEnabled(false);
            Stop.setText("Stop");

            GenerateBtn.setVisibility(View.INVISIBLE);
            GenerateBtn.setEnabled(false);


        });

        Stop.setOnClickListener(view -> {

            if(!pressed){
                DrawView.StopDrawing();
                this.pressed = true;
                Stop.setText("Start");
                Reset.setEnabled(true);


            }else{

                this.pressed = false;
                Reset.setEnabled(false);
                Stop.setText("Stop");
                DrawView.StartDrawing();


            }

        });

        Reset.setOnClickListener(view -> {

            pressed = false;

            DrawView.Reset();
            GenerateBtn.setEnabled(true);
            GenerateBtn.setVisibility(View.VISIBLE);

            Reset.setEnabled(true);
            Reset.setVisibility(View.INVISIBLE);

            Stop.setEnabled(true);
            Stop.setVisibility(View.INVISIBLE);


        });

        path.setOnClickListener(v->{

            DrawView.firstSearch = false;
            DrawView.TriggerA_star();

        });

    }
}