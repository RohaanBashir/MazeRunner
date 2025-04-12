package com.example.maze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Boolean isGenerated = false;
    Button path;
    Boolean pressed = false;
    Button GenerateBtn;
    Draw DrawView;
    Button Stop;
    Button Reset;
    Spinner mySpinner;
    int PathAlgoSelection = -99;
    SeekBar mySeekBar;
    TextView valueTextView;
    TextView status;
    Spinner pathFindingSpinner;

    Button resetMaze;
    int pathAlgo = 0;

    TextView iterations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //button Ids
        DrawView = findViewById(R.id.draw);
        GenerateBtn = findViewById(R.id.button);
        Stop = findViewById(R.id.Stop);
        Reset = findViewById(R.id.Reset);
        status = findViewById(R.id.status);
        path = findViewById(R.id.path);
        pathFindingSpinner = findViewById(R.id.pathfindingspinner);
        resetMaze = findViewById(R.id.resetMaze);

        TextView iterations = findViewById(R.id.iterations);


        //Path finding spinner code
        mySpinner = findViewById(R.id.my_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mySpinner.setAdapter(adapter);

        // Set an item selected listener
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    PathAlgoSelection = 0;
                } else if (position == 1) {
                    PathAlgoSelection = 1;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        //Path finding spinner code


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> newAdapter = ArrayAdapter.createFromResource(this,
                R.array.pathfind_items, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        pathFindingSpinner.setAdapter(newAdapter);

        // Set an item selected listener
        pathFindingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    pathAlgo = 0;
                } else if (position == 1) {
                    pathAlgo = 1;
                }
                else if (position == 2) {
                    pathAlgo = 2;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        mySeekBar = findViewById(R.id.my_seekbar);
        valueTextView = findViewById(R.id.value_textview);

        // Set up a listener for the SeekBar
        mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the TextView to show the current stage
                String stage = getStage(progress);
                valueTextView.setText("GridSize: " + stage);
                if(progress == 0){
                    DrawView.changeSize(120);
                }
                if(progress == 1){
                    DrawView.changeSize(80);
                }
                if(progress == 2){
                    DrawView.changeSize(50);
                }
                if(progress == 3){
                    DrawView.changeSize(35);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: You can handle any event when the user starts touching the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: You can handle any event when the user stops touching the SeekBar
            }
        });


        GenerateBtn.setOnClickListener(v -> {
            if (this.PathAlgoSelection == 0) {
                DrawView.TriggerKruskal();
            }
            if (this.PathAlgoSelection == 1) {
                DrawView.TriggerBacktracker();
            }

            status.setText("Generating");
            status.setTextColor(Color.GREEN);

            //setting the visibility of the stop and reset buttons
            Stop.setVisibility(View.VISIBLE);
            Reset.setVisibility(View.VISIBLE);
            Stop.setEnabled(true);
            Reset.setEnabled(false);
            Stop.setText("Stop");
            GenerateBtn.setVisibility(View.INVISIBLE);
            GenerateBtn.setEnabled(false);

            //hiding seek bar
            mySeekBar.setVisibility(View.INVISIBLE);
            mySpinner.setVisibility(View.INVISIBLE);
            //hiding siez text

            valueTextView.setVisibility(View.INVISIBLE);


        });

        Stop.setOnClickListener(view -> {


            if (!pressed) {
                DrawView.StopDrawing();
                this.pressed = true;
                Stop.setText("Start");
                Reset.setEnabled(true);
                status.setText("Stopped");
                status.setTextColor(Color.RED);
            } else {
                this.pressed = false;
                Reset.setEnabled(false);
                Stop.setText("Stop");
                DrawView.StartDrawing();
                status.setText("Generating");
                status.setTextColor(Color.GREEN);
            }
        });


        Reset.setOnClickListener(view -> {

            status.setText("Maze generation");
            status.setTextColor(Color.WHITE);
            pressed = false;

            DrawView.Reset();
            GenerateBtn.setEnabled(true);
            GenerateBtn.setVisibility(View.VISIBLE);

            Reset.setEnabled(true);
            Reset.setVisibility(View.INVISIBLE);

            Stop.setEnabled(true);
            Stop.setVisibility(View.INVISIBLE);

            mySeekBar.setVisibility(View.VISIBLE);
            mySpinner.setVisibility(View.VISIBLE);

            //hiding size text
            valueTextView.setVisibility(View.VISIBLE);



        });


        DrawView.changeIterations.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                iterations.setText("Iterations: " + DrawView.iterations);
            }
        });


        DrawView.changeNodesVisited.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                iterations.setText("Nodes Visited: " + DrawView.nodesVisited);
            }
        });

        DrawView.change.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(DrawView.change.getValue() == true){

                    status.setText("Maze Generated!");
                    status.setTextColor(Color.GREEN);
                    isGenerated = true;

                    path.setEnabled(true);
                    path.setVisibility(View.VISIBLE);
                    pathFindingSpinner.setVisibility(View.VISIBLE);

                    Reset.setEnabled(false);
                    Reset.setVisibility(View.INVISIBLE);

                    Stop.setEnabled(false);
                    Stop.setVisibility(View.INVISIBLE);

                    mySeekBar.setVisibility(View.INVISIBLE);
                    mySeekBar.setEnabled(false);

                    mySpinner.setVisibility(View.INVISIBLE);
                    mySpinner.setEnabled(false);
                }
            }
        });


        DrawView.pathFound.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (DrawView.pathFound.getValue() == true) {
                    status.setText("Path Found!");
                    status.setTextColor(Color.GREEN);

                    // Show only reset
                    Reset.setEnabled(false);
                    Reset.setVisibility(View.INVISIBLE);

                    // Hide everything else
                    path.setVisibility(View.INVISIBLE);
                    pathFindingSpinner.setVisibility(View.INVISIBLE);
                    Stop.setVisibility(View.INVISIBLE);
                    GenerateBtn.setVisibility(View.INVISIBLE);
                    mySeekBar.setVisibility(View.INVISIBLE);
                    valueTextView.setVisibility(View.INVISIBLE);
                    mySpinner.setVisibility(View.INVISIBLE);
                }
            }
        });

        path.setOnClickListener(v->{
            DrawView.iterations = -1;
            DrawView.firstSearch = false;
            if(pathAlgo == 0){
                DrawView.TriggerA_star();
            }
            if(pathAlgo == 1){
                DrawView.TriggerBFS();
            }
            if(pathAlgo == 2){
                DrawView.TriggerDFS();
            }



        });

    }
    private String getStage(int progress) {
        switch (progress) {
            case 0:
                return "Small";
            case 1:
                return "Medium";
            case 2:
                return "Big";
            case 3:
                return "Extra Big";
            default:
                return "Unknown";
        }
    }

}