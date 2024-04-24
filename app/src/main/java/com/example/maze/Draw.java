package com.example.maze;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Draw extends View {


    int shrinkAmount = 10;
    boolean DFSCompleted = false;
    boolean DFS = false;
    boolean firstSearch =  true;
    int delay = 0;
    Boolean DFSFirst = true;
    Paint pathPaint;

    Boolean invalidate = false;
    Paint paint1;
    Stack<Cell> stack;
    int rows,cols;
    int w = 50;
    Paint paint;
    Canvas canvas;
    ArrayList<Cell> grid;

    boolean isDrawing = false;
    Handler handler;
    private Runnable runnable;
    Cell current;

    boolean GridCreated = false;
    boolean first = true;

    boolean SetCreated = false;

    DisjointSet<Cell> set;
    int counter = 0;
    boolean Running = true;

    public Draw(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        paint = new Paint();
        paint.setColor(Color.WHITE);  // Set color to red
        paint.setStyle(Paint.Style.STROKE);  // Set fill style
        paint.setStrokeWidth(6);  // Set stroke width

        handler = new Handler();

        paint1 = new Paint();
        paint1.setColor(Color.GREEN);
        paint1.setStyle(Paint.Style.FILL);
        stack = new Stack<>();

        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(Color.RED);


    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas; //dependency injection


        if (!GridCreated) {
            cols = getWidth() / w;
            rows = getHeight() / w;
            System.out.println(cols + " " + rows);

            grid = new ArrayList<Cell>();

            for (int j = 0; j < rows; j++) {
                for (int i = 0; i < cols; i++) {
                    Cell temp = new Cell(i, j);
                    grid.add(temp);
                }
            }
            GridCreated = true;
        }

        if (firstSearch) {
//          GenerateMazeRecursiveBacktrack();
            GenerateMazeKruskal();
        }
        if (DFS) {
            this.DFS();
        }
//        if (this.invalidate) {
//            postInvalidateDelayed(delay);
//        }
        if (DFSCompleted) {
            for(Cell cell : stack){

                // Draw path indicator on the cell
                canvas.drawRect(
                        cell.i * w + shrinkAmount, // Left
                        cell.j * w + shrinkAmount, // Top
                        (cell.i * w) + w - shrinkAmount, // Right
                        (cell.j * w) + w - shrinkAmount, // Bottom
                        pathPaint
                );
            }
            canvas.drawRect(grid.get(grid.size()-1).i*w, grid.get(grid.size()-1).j*w, (grid.get(grid.size()-1).i*w)+w,
                    (grid.get(grid.size()-1).j*w)+w, paint1);//last cell

            canvas.drawRect(grid.get(0).i*w, grid.get(0).j*w,
                    (grid.get(0).i*w)+w, (grid.get(0).j*w)+w, paint1); // start cell
            
        }
        for (int i = 0;i<grid.size();i++){
            grid.get(i).show();
        }
    }
    public void GenerateMazeKruskal(){

        //step1- Creating N-1 sets with each contain one cell

        canvas.drawColor(Color.BLACK);
        if(!SetCreated){
            //Making sets of grid each set will contain only one cell
            set = new DisjointSet<>();
            for (int i = 0;i<grid.size();i++){
                set.makeSet(grid.get(i));
            }
            System.out.println(set.itemsSize());
            SetCreated = true;
        }
        //Step2-Getting random Cell and Random Neighbour
        if(Running){
            int index = RandomGridIndex();
            Cell current = grid.get(index);
            Cell next = current.checkNeighbours(); //return random Neighbour
            canvas.drawRect(current.i*w,current.j*w,(current.i*w)+w,(current.j*w)+w,paint1);

            //Step3-join (Union) the Sets if they are district
            if(set.find(current) != set.find(next)){
                //remove wall
                assert next != null;
                RemoveWalls(current,next);
                set.union(current,next);
                counter++;
            }

        }
        if(counter == grid.size()-1){
            Running = false;
        }
        postInvalidateDelayed(delay);

    }
    public void GenerateMazeRecursiveBacktrack(){

        canvas.drawColor(Color.BLACK);
        //marking source visited
        if(first){
            this.canvas.drawColor(Color.BLACK);
            current=grid.get(0);
            current.visited = true;
            first = false;
        }
        //getting the next node
        Cell next = current.checkNeighbours(); //returns a random Neighbour
        canvas.drawRect(current.i*w,current.j*w,(current.i*w)+w,(current.j*w)+w,paint1);
        if(next!=null){
            stack.push(current);
            RemoveWalls(current,next);
            next.visited = true;
            current = next;
        }
        else if(!stack.isEmpty()){
            current = stack.pop();
        }

        postInvalidateDelayed(delay);
    }

    public void TriggerDFS(){
        DFS = true;
    }
    public boolean DFS(){

        postInvalidateDelayed(delay);
        if(DFSFirst){

            //first marking every Node as non visited
            for (int i = 0;i<grid.size();i++){
                grid.get(i).visited = false;
            }

            //emptying the stack
            while(!stack.isEmpty()){
                stack.pop();
            }

            current=grid.get(0);
            current.visited = true;
            DFSFirst = false;
        }
        //getting next cell based upon if they don't have wall between current and next
        Cell next = current.NonWalledNeighbours();

        canvas.drawRect(current.i*w,current.j*w,(current.i*w)+w,(current.j*w)+w,paint1);
        if(next!=null){
            stack.push(current);
            next.visited = true;
            current = next;
        }
        else if(!stack.isEmpty()){
            current = stack.pop();
        }
        if (current == grid.get(grid.size()-1)) {

            DFS = false;
            DFSCompleted = true;
            return true;
        }

        return false;
    }

    private class Cell { //inner class

        boolean visited = false;

        //active walls of the Cell
        boolean[] wall = new boolean[4];

        //Position of the Cell
        int i, j;

        Cell(int i, int j) {

            this.i = i;
            this.j = j;

            //top
            wall[0] = true;

            //right
            wall[1] = true;

            //bottom
            wall[2] = true;

            //left
            wall[3] = true;
        }

        public void show() {

            int x = this.i * w;
            int y = this.j * w;

            //top wall
            if (wall[0]) {
                canvas.drawLine(x, y, x + w, y, paint);
            }
            //right wall
            if (wall[1]) {
                canvas.drawLine(x + w, y, x + w, y + w, paint);
            }
            //bottom wall
            if (wall[2]) {
                canvas.drawLine(x + w, y + w, x, y + w, paint);
            }

            //left wall
            if (wall[3]) {
                canvas.drawLine(x, y + w, x, y, paint);
            }
        }

        @Nullable
        private Cell NonWalledNeighbours(){


            //top Neighbour
            try{
                Cell top = grid.get(getIndex(i,j-1));
                if(!top.visited && wall[0] == false){
                    return top;
                }
            }catch (ArrayIndexOutOfBoundsException  ignored){

            }

            //right Neighbour
            try{
                Cell right = grid.get(getIndex(i+1,j));
                if(!right.visited && wall[1] == false){
                    return right;
                }
            }catch (ArrayIndexOutOfBoundsException ignored){

            }

            //bottom Neighbour
            try{
                Cell bottom = grid.get(getIndex(i,j+1));
                if(!bottom.visited && wall[2] == false){
                    return bottom;
                }
            }catch (ArrayIndexOutOfBoundsException ignored){

            }
            //left Neighbour
            try{

                Cell left = grid.get(getIndex(i-1,j));
                if(!left.visited && wall[3] == false){
                    return left;
                }
            }catch (ArrayIndexOutOfBoundsException ignored){
            }

            return null;
        }

        public Cell checkNeighbours(){
            //this method will checkNeighbours and randomly returns a Cell

            ArrayList<Cell> ActiveNeighbours = new ArrayList<>();

            //top Neighbour
            try{
                Cell top = grid.get(getIndex(i,j-1));
                if(!top.visited){
                    ActiveNeighbours.add(top);
                }
            }catch (ArrayIndexOutOfBoundsException  ignored){

            }

            //right Neighbour
            try{
                Cell right = grid.get(getIndex(i+1,j));
                if(!right.visited){
                    ActiveNeighbours.add(right);
                }
            }catch (ArrayIndexOutOfBoundsException ignored){

            }

            //bottom Neighbour
            try{
                Cell bottom = grid.get(getIndex(i,j+1));
                if(!bottom.visited){
                    ActiveNeighbours.add(bottom);
                }
            }catch (ArrayIndexOutOfBoundsException ignored){

            }
            //left Neighbour
            try{

                Cell left = grid.get(getIndex(i-1,j));
                if(!left.visited){
                    ActiveNeighbours.add(left);
                }

            }catch (ArrayIndexOutOfBoundsException ignored){

            }
            //selecting the Neighbours

            if(ActiveNeighbours.size() > 0){
                //picking a ActiveNeighbours

                long seed = System.currentTimeMillis();
                Random randomIndex = new Random(seed);

                Cell next = ActiveNeighbours.get(randomIndex.nextInt(ActiveNeighbours.size()));
                ActiveNeighbours.clear();
                return next;
            }
            else{
                return null;
            }
        }

        private int getIndex(int i, int j){

            //because we are using 1D array so have to convert the index

            if(i<0 || j<0 || i > cols-1 || j > rows-1){ //bounds
                return -1;
            }
            else{
                return i + j * cols;
            }
        }
    }
    public void RemoveWalls(Cell a, Cell b){ //a is current b is next

        int x = a.i - b.i;
        int y = a.j - b.j;

        if(x == 1){
            //remove left wall
            a.wall[3] = false; //remove left wall of current
            b.wall[1] = false; //remove right wall of next
        }
        else if(x == -1){
            //right wall remove
            a.wall[1] = false;
            b.wall[3] = false;
        }
        else if(y == 1){
            //current need top next need bottom

            a.wall[0] = false;
            b.wall[2] = false;
        }
        else if(y == -1){

            //bottom wall of current
            b.wall[0] = false;
            a.wall[2] = false;

        }

    }

    private int RandomGridIndex(){

        long seed = System.currentTimeMillis();
        Random randomIndex = new Random(seed);
        return randomIndex.nextInt(this.grid.size());
    }

    public void StartDrawing(){
        this.invalidate = true;
        postInvalidateDelayed(delay);

    }

    public void StopDrawing(){
        this.invalidate = false;
        postInvalidateDelayed(delay);
    }

    public void Reset(){


        //emptying the stack

        while(!this.stack.isEmpty()){
            this.stack.pop();
        }

        //creating a new grid and assigning it to the current grid
        ArrayList<Cell> newGrid = new ArrayList<Cell>();
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                Cell temp = new Cell(i, j);
                newGrid.add(temp);
            }
        }

        this.grid = newGrid; //copying the newGrid to actual grid
        first = true;
        DFS = false;
        firstSearch = true;

        postInvalidateDelayed(delay);
    }
}
