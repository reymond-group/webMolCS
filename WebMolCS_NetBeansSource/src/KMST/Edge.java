/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package KMST;

/**
 *
 * @author daenu
 */
public class Edge {

    private int from;
    private int to;
    private double weight;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Edge(int from, int to, double weight) {
        this.setFrom(from);
        this.setTo(to);
        this.setWeight(weight);
    }

    @Override
    public String toString() {
        return "{ from: " + this.getFrom() + ", to: " + this.getTo() + ", weight: " + this.getWeight() + " }";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Edge.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Edge other = (Edge) obj;

        if (this.getFrom() == other.getFrom() && this.getTo() == other.getTo()) {
            return true;
        }
        if (this.getFrom() == other.getTo() && this.getTo() == other.getFrom()) {
            return true;
        }

        return false;
    }
}
