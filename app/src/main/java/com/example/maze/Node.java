package com.example.maze;

public class Node implements Comparable<Node> {
    private Draw.Cell cell; // Cell information
    public Node(Draw.Cell cell) {

        this.cell = cell;
    }
    public Draw.Cell getCell() {
        return cell;
    }
    @Override
    public int compareTo(Node other) {
        // First compare based on f value (lower f is prioritized)
        if (this.cell.f != other.cell.f) {
            return Integer.compare(this.cell.f, other.cell.f);
        }
        // If f values are the same, compare based on h value (lower h is prioritized)
        return Integer.compare(this.cell.h, other.cell.h);
    }
    @Override
    public String toString() {
        return "Node[cell=" + cell + "]";
    }
}