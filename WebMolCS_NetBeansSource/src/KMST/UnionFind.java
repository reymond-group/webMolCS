package KMST;

import java.util.*;

public class UnionFind<T> {

    private Map<T, T> parentMap;
    private Map<T, Integer> rankMap;

    public UnionFind(Set<T> elements) {
        parentMap = new HashMap();
        rankMap = new HashMap();
        for (T element : elements) {
            parentMap.put(element, element);
            rankMap.put(element, 0);
        }
    }

    public void addElement(T element) {
        parentMap.put(element, element);
        rankMap.put(element, 0);
    }

    protected Map<T, T> getParentMap() {
        return parentMap;
    }

    protected Map<T, Integer> getRankMap() {
        return rankMap;
    }

    public T find(T element) {
        if (!parentMap.containsKey(element)) {
            throw new IllegalArgumentException("elements must be contained in given set");
        }

        T parent = parentMap.get(element);
        if (parent.equals(element)) {
            return element;
        }

        T newParent = find(parent);
        parentMap.put(element, newParent);
        return newParent;
    }

    public void union(T element1, T element2) {
        if (!parentMap.containsKey(element1) || !parentMap.containsKey(element2)) {
            throw new IllegalArgumentException("elements must be contained in given set");
        }

        T parent1 = find(element1);
        T parent2 = find(element2);

        if (parent1.equals(parent2)) {
            return;
        }

        int rank1 = rankMap.get(parent1);
        int rank2 = rankMap.get(parent2);
        if (rank1 > rank2) {
            parentMap.put(parent2, parent1);
        } else if (rank1 < rank2) {
            parentMap.put(parent1, parent2);
        } else {
            parentMap.put(parent2, parent1);
            rankMap.put(parent1, rank1 + 1);
        }
    }
}