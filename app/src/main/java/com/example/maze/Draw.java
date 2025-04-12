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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class Draw extends View {


    int shrinkAmount = 10;

    public int iterations = 0;
    public int nodesVisited = 0;

    public MutableLiveData<Boolean> changeIterations = new MutableLiveData<>();

    public MutableLiveData<Boolean> changeNodesVisited = new MutableLiveData<>();

    MutableLiveData<Boolean> change = new MutableLiveData<>();

    MutableLiveData<Boolean> pathFound = new MutableLiveData<>();
    boolean tracebackpath = false;

    boolean BFSCompleted = false;
    boolean BFSFirst = true;

    Cell next;
    boolean BFS = false;

    List<Cell> neibourArray;
    boolean DFSCompleted = false;
    boolean DFS = false;
    boolean A_star = false;
    boolean firstSearch = true;
    long delay = -999999999999999999L;
    Boolean DFSFirst = true;
    Paint pathPaint;

    Boolean invalidate = false;
    Paint paint1;
    Stack<Cell> stack;
    Queue<Cell> queue;
    int rows, cols;
    int w = 80;
    Paint paint;
    Canvas canvas;
    ArrayList<Cell> grid;
    boolean kruskal = false;
    boolean isDrawing = false;
    PriorityQueue<Node> openSet;
    Handler handler;
    private Runnable runnable;
    Cell current;
    boolean GridCreated = false;
    boolean first = true;
    boolean SetCreated = false;
    boolean Backtracker = false;
    DisjointSet<Cell> set;
    int counter = 0;
    boolean Running = true;
    boolean A_starFirst = true;
    int g = 0;

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
        queue = new LinkedList<>();
        neibourArray = new ArrayList<>();
        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(Color.RED);
        openSet = new PriorityQueue<>();
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas; //dependency injection


        if (!GridCreated) {
            cols = getWidth() / w;
            rows = getHeight() / w;

            grid = new ArrayList<Cell>();

            for (int j = 0; j < rows; j++) {
                for (int i = 0; i < cols; i++) {
                    Cell temp = new Cell(i, j);
                    grid.add(temp);
                }
            }
            GridCreated = true;
        }
        if (this.invalidate) {

            if (kruskal) {
                GenerateMazeKruskal();
            }
            if (Backtracker) {
                GenerateMazeRecursiveBacktrack();
                iterations++;
                changeIterations.setValue(true);
            }
            if (DFS) {
                this.DFS();
                iterations++;
                changeIterations.setValue(true);
            }

            if (BFS) {
                this.BFS();
                iterations++;
                changeIterations.setValue(true);
            }
            if (A_star) {
                this.A_star();
                iterations++;
                changeIterations.setValue(true);
            }
        }

        if (DFSCompleted) {
            for (Cell cell : stack) {

                // Draw path indicator on the cell
                canvas.drawRect(
                        cell.i * w + shrinkAmount, // Left
                        cell.j * w + shrinkAmount, // Top
                        (cell.i * w) + w - shrinkAmount, // Right
                        (cell.j * w) + w - shrinkAmount, // Bottom
                        pathPaint
                );
            }
            canvas.drawRect(grid.get(grid.size() - 1).i * w, grid.get(grid.size() - 1).j * w, (grid.get(grid.size() - 1).i * w) + w,
                    (grid.get(grid.size() - 1).j * w) + w, paint1);//last cell

            canvas.drawRect(grid.get(0).i * w, grid.get(0).j * w,
                    (grid.get(0).i * w) + w, (grid.get(0).j * w) + w, paint1); // start cell

        }
        for (int i = 0; i < grid.size(); i++) {
            grid.get(i).show();
        }
    }

    public void TriggerKruskal() {
        this.GridCreated = false;
        this.invalidate = true;
        this.kruskal = true;
        this.SetCreated = false;
        postInvalidateDelayed(delay);
    }

    public void GenerateMazeKruskal() {

        //step1- Creating N-1 sets with each contain one cell

        canvas.drawColor(Color.BLACK);
        if (!SetCreated) {
            //Making sets of grid each set will contain only one cell
            set = new DisjointSet<>();
            for (int i = 0; i < grid.size(); i++) {
                set.makeSet(grid.get(i));
            }
            SetCreated = true;
        }
        //Step2-Getting random Cell and Random Neighbour
        if (Running) {
            int index = RandomGridIndex();
            Cell current = grid.get(index);
            Cell next = current.checkNeighbours(); //return random Neighbour
            canvas.drawRect(current.i * w, current.j * w, (current.i * w) + w, (current.j * w) + w, paint1);

            //Step3-join (Union) the Sets if they are district
            if (set.find(current) != set.find(next)) {
                //remove wall
                assert next != null;
                RemoveWalls(current, next);
                set.union(current, next);
                iterations++;
                counter++;
                changeIterations.setValue(true);
            }
            if (counter == grid.size() - 1) {
                Running = false;
                kruskal = false;
                this.change.setValue(true);
            }
        }

        postInvalidateDelayed(delay);
    }

    public void GenerateMazeRecursiveBacktrack() {

        canvas.drawColor(Color.BLACK);
        //marking source visited
        if (first) {
            this.canvas.drawColor(Color.BLACK);
            current = grid.get(0);
            current.visited = true;
            first = false;
        }
        //getting the next node
        Cell next = current.checkNeighbours(); //returns a random Neighbour
        canvas.drawRect(current.i * w, current.j * w, (current.i * w) + w, (current.j * w) + w, paint1);
        if (next != null) {
            stack.push(current);
            RemoveWalls(current, next);
            next.visited = true;
            current = next;
        } else if (!stack.isEmpty()) {
            current = stack.pop();
        }
        if (current == grid.get(0)) {
            //maze generated because back on initial position
            Backtracker = false;
            this.change.setValue(true);
            postInvalidateDelayed(delay);
        }

        postInvalidateDelayed(delay);
    }

    public void TriggerBacktracker() {
        this.GridCreated = false;
        this.invalidate = true;
        this.Backtracker = true;
        this.SetCreated = false;
        postInvalidateDelayed(delay);
    }

    public void TriggerDFS() {
        DFS = true;
        postInvalidateDelayed(delay);
    }

    public void TriggerBFS() {
        BFS = true;
        postInvalidateDelayed(delay);
    }

    public boolean DFS() {

        postInvalidateDelayed(delay);
        if (DFSFirst) {

            //first marking every Node as non visited
            for (int i = 0; i < grid.size(); i++) {
                grid.get(i).visited = false;
            }
            //emptying the stack
            while (!stack.isEmpty()) {
                stack.pop();
            }
            current = grid.get(0);
            current.visited = true;
            DFSFirst = false;
        }

        if (current == grid.get(grid.size() - 1)) {
            DFS = false;
            traceBackPath();
            DFSCompleted = true;
            pathFound.setValue(true);
        }
        //getting next cell based upon if they don't have wall between current and next
        Cell next = current.NonWalledNeighbours();

        canvas.drawRect(current.i * w, current.j * w, (current.i * w) + w, (current.j * w) + w, paint1);
        if (next != null) {
            stack.push(current);
            next.visited = true;
            next.parent = current;
            current = next;
        } else if (!stack.isEmpty()) {
            current = stack.pop();
        }
        return false;
    }

    private boolean BFS() {
        if (BFSFirst) {
            // Making all nodes not visited and clearing the stack
            for (int i = 0; i < grid.size(); i++) {
                grid.get(i).visited = false;
                grid.get(i).parent = null;
            }
            while (!stack.isEmpty()) {
                stack.pop();
            }
            current = grid.get(0);
            current.visited = true;
            queue.add(current);
            BFSFirst = false;
        }

        if (queue.isEmpty()) {
            BFS = false;
            pathFound.setValue(true);
            return true;
        }

        if (current == grid.get(grid.size() - 1)) {
            // Goal cell found, trace back the path
            traceBackPath();
            BFS = false;
            pathFound.setValue(true);
            return true;
        }
        current = queue.poll();
        stack.push(current);
        for (Cell neighbor : current.NonWalledNeighboursArray()) {
            if (!neighbor.visited) {
                neighbor.visited = true;
                neighbor.parent = current; // Set the parent cell
                queue.add(neighbor);
            }
            canvas.drawRect(
                    neighbor.i * w + shrinkAmount, // Left
                    neighbor.j * w + shrinkAmount, // Top
                    (neighbor.i * w) + w - shrinkAmount, // Right
                    (neighbor.j * w) + w - shrinkAmount, // Bottom
                    pathPaint
            );
        }

        postInvalidateDelayed(delay);
        return true;
    }

    private void drawCurrentPath(Canvas canvas, Cell current) {
        Paint currentPathPaint = new Paint();
        currentPathPaint.setColor(Color.YELLOW); // Color for the current path
        currentPathPaint.setStyle(Paint.Style.FILL);

        Cell temp = current;
        while (temp != null) {
            canvas.drawRect(
                    temp.i * w + shrinkAmount, // Left
                    temp.j * w + shrinkAmount, // Top
                    (temp.i * w) + w - shrinkAmount, // Right
                    (temp.j * w) + w - shrinkAmount, // Bottom
                    currentPathPaint // Paint for the current path
            );
            temp = temp.parent;
        }
    }

    void TriggerA_star() {
        A_star = true;
        postInvalidateDelayed(delay);
    }

    public boolean A_star() {
        if (A_starFirst) {
            g = 0;
            current = this.grid.get(0);
            current.f = 0;
            current.g = g;
            current.h = heuristic(current);
            openSet.add(new Node(this.grid.get(0)));
            A_starFirst = false;
        }

        if (current == this.grid.get(this.grid.size() - 1)) {
            traceBackPath();
            pathFound.setValue(true);
            return true;
        }

        current = openSet.poll().getCell();
        g = current.g;

        current.visited = true;
        canvas.drawRect(
                current.i * w + shrinkAmount, // Left
                current.j * w + shrinkAmount, // Top
                (current.i * w) + w - shrinkAmount, // Right
                (current.j * w) + w - shrinkAmount, // Bottom
                pathPaint // Paint for visited cells
        );

        // Draw the current path from the current node to the start node
        drawCurrentPath(canvas, current);

        for (Cell neighbour : current.NonWalledNeighboursArrayAStar()) {
            int temp_g_score = g + 1;
            int temp_h_score = heuristic(neighbour);
            int temp_f_score = temp_g_score + temp_h_score;

            if (temp_f_score < neighbour.f) {
                neighbour.g = temp_g_score;
                neighbour.h = temp_h_score;
                neighbour.f = temp_f_score;
                neighbour.parent = current; // Set the parent cell
                openSet.add(new Node(neighbour));
            }
            canvas.drawRect(
                    neighbour.i * w + shrinkAmount, // Left
                    neighbour.j * w + shrinkAmount, // Top
                    (neighbour.i * w) + w - shrinkAmount, // Right
                    (neighbour.j * w) + w - shrinkAmount, // Bottom
                    pathPaint
            );

        }
        postInvalidateDelayed(50);
        return true;
    }

    private int heuristic(Cell a) {
        return Math.abs(a.i - this.grid.get(grid.size() - 1).i) + Math.abs(a.j - this.grid.get(grid.size() - 1).j);
    }

    private void traceBackPath() {
        Cell currentCell = grid.get(grid.size() - 1); // Goal cell
        while (currentCell.parent != null) {
            // Mark path from goal to start
            canvas.drawRect(
                    currentCell.i * w + shrinkAmount,
                    currentCell.j * w + shrinkAmount,
                    (currentCell.i * w) + w - shrinkAmount,
                    (currentCell.j * w) + w - shrinkAmount,
                    pathPaint
            );
            currentCell = currentCell.parent; // Move to parent cell
            canvas.drawRect(grid.get(grid.size() - 1).i * w, grid.get(grid.size() - 1).j * w, (grid.get(grid.size() - 1).i * w) + w,
                    (grid.get(grid.size() - 1).j * w) + w, paint1);//last cell

            canvas.drawRect(grid.get(0).i * w, grid.get(0).j * w,
                    (grid.get(0).i * w) + w, (grid.get(0).j * w) + w, paint1); // start cell

        }
    }

    class Cell { //inner class

        int f = 999999;
        int g, h;
        Cell parent;
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


        private ArrayList<Cell> NonWalledNeighboursArrayAStar() {

            ArrayList<Cell> returnArray = new ArrayList<>();
            try {
                Cell top = grid.get(getIndex(i, j - 1));
                if (wall[0] == false) {
                    returnArray.add(top);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }

            //right Neighbour
            try {
                Cell right = grid.get(getIndex(i + 1, j));
                if (wall[1] == false) {
                    returnArray.add(right);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }

            //bottom Neighbour
            try {
                Cell bottom = grid.get(getIndex(i, j + 1));
                if (wall[2] == false) {
                    returnArray.add(bottom);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
            //left Neighbour
            try {

                Cell left = grid.get(getIndex(i - 1, j));
                if (wall[3] == false) {
                    returnArray.add(left);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
            return returnArray;
        }

        @Nullable
        private Cell NonWalledNeighbours() {


            //top Neighbour
            try {
                Cell top = grid.get(getIndex(i, j - 1));
                if (!top.visited && wall[0] == false) {
                    return top;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }

            //right Neighbour
            try {
                Cell right = grid.get(getIndex(i + 1, j));
                if (!right.visited && wall[1] == false) {
                    return right;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }

            //bottom Neighbour
            try {
                Cell bottom = grid.get(getIndex(i, j + 1));
                if (!bottom.visited && wall[2] == false) {
                    return bottom;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
            //left Neighbour
            try {

                Cell left = grid.get(getIndex(i - 1, j));
                if (!left.visited && wall[3] == false) {
                    return left;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }

            return null;
        }


        private List<Cell> NonWalledNeighboursArray() {

            //top Neighbour
            try {
                Cell top = grid.get(getIndex(i, j - 1));
                if (wall[0] == false) {
                    neibourArray.add(top);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }

            //right Neighbour
            try {
                Cell right = grid.get(getIndex(i + 1, j));
                if (wall[1] == false) {
                    neibourArray.add(right);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }

            //bottom Neighbour
            try {
                Cell bottom = grid.get(getIndex(i, j + 1));
                if (wall[2] == false) {
                    neibourArray.add(bottom);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
            //left Neighbour
            try {

                Cell left = grid.get(getIndex(i - 1, j));
                if (wall[3] == false) {
                    neibourArray.add(left);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }

            return neibourArray;
        }

        public Cell checkNeighbours() {
            //this method will checkNeighbours and randomly returns a Cell

            ArrayList<Cell> ActiveNeighbours = new ArrayList<>();

            //top Neighbour
            try {
                Cell top = grid.get(getIndex(i, j - 1));
                if (!top.visited) {
                    ActiveNeighbours.add(top);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }

            //right Neighbour
            try {
                Cell right = grid.get(getIndex(i + 1, j));
                if (!right.visited) {
                    ActiveNeighbours.add(right);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }

            //bottom Neighbour
            try {
                Cell bottom = grid.get(getIndex(i, j + 1));
                if (!bottom.visited) {
                    ActiveNeighbours.add(bottom);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
            //left Neighbour
            try {

                Cell left = grid.get(getIndex(i - 1, j));
                if (!left.visited) {
                    ActiveNeighbours.add(left);
                }

            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
            //selecting the Neighbours

            if (ActiveNeighbours.size() > 0) {
                //picking a ActiveNeighbours

                long seed = System.currentTimeMillis();
                Random randomIndex = new Random(seed);

                Cell next = ActiveNeighbours.get(randomIndex.nextInt(ActiveNeighbours.size()));
                ActiveNeighbours.clear();
                return next;
            } else {
                return null;
            }
        }

        private int getIndex(int i, int j) {

            //because we are using 1D array so have to convert the index

            if (i < 0 || j < 0 || i > cols - 1 || j > rows - 1) { //bounds
                return -1;
            } else {
                return i + j * cols;
            }
        }
    }

    public void RemoveWalls(Cell a, Cell b) { //a is current b is next

        int x = a.i - b.i;
        int y = a.j - b.j;

        if (x == 1) {
            //remove left wall
            a.wall[3] = false; //remove left wall of current
            b.wall[1] = false; //remove right wall of next
        } else if (x == -1) {
            //right wall remove
            a.wall[1] = false;
            b.wall[3] = false;
        } else if (y == 1) {
            //current need top next need bottom

            a.wall[0] = false;
            b.wall[2] = false;
        } else if (y == -1) {

            //bottom wall of current
            b.wall[0] = false;
            a.wall[2] = false;

        }

    }

    private int RandomGridIndex() {

        long seed = System.currentTimeMillis();
        Random randomIndex = new Random(seed);
        return randomIndex.nextInt(this.grid.size());
    }

    public void changeSize(int size) {
        this.Reset();
        this.w = size;
        this.GridCreated = false;
        invalidate();
    }

    public void StartDrawing() {
        this.invalidate = true;
        postInvalidateDelayed(delay);

    }

    public void StopDrawing() {
        this.invalidate = false;
        postInvalidateDelayed(delay);
    }

    public void Reset() {

        //emptying the stack
        if (this.stack != null) {
            while (!this.stack.isEmpty()) {
                this.stack.pop();
            }
        }
        if (this.grid != null) {
            ArrayList<Cell> newGrid = new ArrayList<Cell>();
            for (int j = 0; j < rows; j++) {
                for (int i = 0; i < cols; i++) {
                    Cell temp = new Cell(i, j);
                    newGrid.add(temp);
                }
            }
            this.grid = newGrid; //copying the newGrid to actual grid
        }
        if (this.set != null) {
            DisjointSet<Cell> temp = new DisjointSet<>();
            for (int i = 0; i < grid.size(); i++) {
                temp.makeSet(grid.get(i));
            }
            this.set = temp;
        }
        this.change.setValue(false);
        this.pathFound.setValue(false);
        first = true;
        DFS = false;
        firstSearch = true;
        this.SetCreated = true;
        this.counter = 0;
        this.kruskal = false;
        this.Backtracker = false;
        postInvalidateDelayed(delay);
    }
}
