package com.example.npuzzleai;

import java.util.*;
import java.util.function.Consumer;

public class Solver {
    private Node startNode;
    private Node goalNode;
    private Vector<Node> FRINGE;
    private HashSet<Node> FRINGE_SET;
    private Vector<Node> CHILD;
    private HashSet<Node> CLOSED;
    private Vector<int[]> RESULT;
    private int approvedNodes;
    private int totalNodes;
    private long time;
    private String error;

    public Solver() {
        FRINGE = new Vector<>();
        FRINGE_SET = new HashSet<>();
        CHILD = new Vector<>();
        CLOSED = new HashSet<>();
        RESULT = new Vector<>();
    }

    public Result solveBFS(Node start, Node goal, Consumer<Integer> progressCallback) {
        if (start == null || goal == null) {
            return new Result("BFS", 0, 0, 0, 0, "Đầu vào không hợp lệ!");
        }
        startNode = start;
        goalNode = goal;
        RESULT.clear();
        FRINGE.clear();
        FRINGE_SET.clear();
        CHILD.clear();
        CLOSED.clear();
        approvedNodes = totalNodes = 0;
        long startTime = System.currentTimeMillis();

        if (!startNode.state.isSolvable()) {
            error = "Trạng thái không có lời giải!";
            return new Result("BFS", approvedNodes, totalNodes, 0, 0, error);
        }

        startNode.g = 0;
        FRINGE.add(startNode);
        FRINGE_SET.add(startNode);
        totalNodes++;
        int maxDepth = 0;

        try {
            while (!FRINGE.isEmpty()) {
                Node currentNode = FRINGE.remove(0);
                FRINGE_SET.remove(currentNode);
                approvedNodes++;

                if (currentNode.g > maxDepth) {
                    maxDepth = currentNode.g;
                    if (progressCallback != null) {
                        progressCallback.accept(maxDepth);
                    }
                }

                if (currentNode.state.isGoal(goalNode.state)) {
                    time = System.currentTimeMillis() - startTime;
                    totalNodes = approvedNodes + FRINGE.size();
                    addResult(currentNode);
                    if (progressCallback != null) {
                        progressCallback.accept(maxDepth);
                    }
                    return new Result("BFS", approvedNodes, totalNodes, RESULT.size() - 1, time, null);
                }

                CHILD = currentNode.successors();
                if (currentNode.parent != null) {
                    for (Iterator<Node> it = CHILD.iterator(); it.hasNext(); ) {
                        Node child = it.next();
                        if (child.equals(currentNode.parent)) {
                            it.remove();
                            break;
                        }
                    }
                }

                for (Node child : CHILD) {
                    child.parent = currentNode;
                    child.g = currentNode.g + 1;
                    if (!CLOSED.contains(child) && !FRINGE_SET.contains(child)) {
                        FRINGE.add(child);
                        FRINGE_SET.add(child);
                        totalNodes++;
                    }
                }
                CLOSED.add(currentNode);
                CHILD.clear();

                if (System.currentTimeMillis() - startTime > 120000) {
                    error = "Thuật toán quá tốn thời gian!";
                    if (progressCallback != null) {
                        progressCallback.accept(maxDepth);
                    }
                    return new Result("BFS", approvedNodes, totalNodes, 0, 0, error);
                }
            }
        } catch (OutOfMemoryError e) {
            error = "Tràn bộ nhớ!";
            if (progressCallback != null) {
                progressCallback.accept(maxDepth);
            }
            return new Result("BFS", approvedNodes, totalNodes, 0, 0, error);
        }

        error = "Không tìm thấy lời giải!";
        if (progressCallback != null) {
            progressCallback.accept(maxDepth);
        }
        return new Result("BFS", approvedNodes, totalNodes, 0, 0, error);
    }

    public Result solveAStar(Node start, Node goal, int heuristicType) {
        if (start == null || goal == null) {
            return new Result(getHeuristicName(heuristicType), 0, 0, 0, 0, "Đầu vào không hợp lệ!");
        }
        State.heuristic = heuristicType;
        startNode = start;
        goalNode = goal;
        RESULT.clear();
        FRINGE.clear();
        FRINGE_SET.clear();
        CHILD.clear();
        CLOSED.clear();
        approvedNodes = totalNodes = 0;
        long startTime = System.currentTimeMillis();

        if (!startNode.state.isSolvable()) {
            error = "Trạng thái không có lời giải!";
            return new Result(getHeuristicName(heuristicType), approvedNodes, totalNodes, 0, 0, error);
        }

        startNode.f = startNode.h = startNode.estimate(goalNode.state);
        startNode.g = 0;
        FRINGE.add(startNode);
        FRINGE_SET.add(startNode);
        totalNodes++;

        while (!FRINGE.isEmpty()) {
            if (System.currentTimeMillis() - startTime > 120000) {
                error = "Thuật toán quá tốn thời gian!";
                return new Result(getHeuristicName(heuristicType), approvedNodes, totalNodes, 0, 0, error);
            }

            Node currentNode = FRINGE.get(0);
            int fMin = currentNode.f;
            for (Node node : FRINGE) {
                if (node.f < fMin) {
                    fMin = node.f;
                    currentNode = node;
                }
            }
            FRINGE.removeElement(currentNode);
            FRINGE_SET.remove(currentNode);
            CLOSED.add(currentNode);
            approvedNodes++;

            if (currentNode.h == 0) {
                time = System.currentTimeMillis() - startTime;
                totalNodes = approvedNodes + FRINGE.size();
                addResult(currentNode);
                return new Result(getHeuristicName(heuristicType), approvedNodes, totalNodes, RESULT.size() - 1, time, null);
            }

            CHILD = currentNode.successors();
            if (currentNode.parent != null) {
                for (Iterator<Node> it = CHILD.iterator(); it.hasNext(); ) {
                    Node child = it.next();
                    if (child.equals(currentNode.parent)) {
                        it.remove();
                        break;
                    }
                }
            }

            for (Node child : CHILD) {
                child.parent = currentNode;
                child.g = currentNode.g + child.cost;
                child.h = child.estimate(goalNode.state);
                child.f = child.g + child.h;

                if (CLOSED.contains(child)) continue;

                if (!FRINGE_SET.contains(child)) {
                    FRINGE.add(child);
                    FRINGE_SET.add(child);
                    totalNodes++;
                } else {
                    for (Node node : FRINGE) {
                        if (node.equals(child) && child.f < node.f) {
                            FRINGE.removeElement(node);
                            FRINGE_SET.remove(node);
                            FRINGE.add(child);
                            FRINGE_SET.add(child);
                            break;
                        }
                    }
                }
            }
            CHILD.clear();
        }

        error = "Không tìm thấy lời giải!";
        return new Result(getHeuristicName(heuristicType), approvedNodes, totalNodes, 0, 0, error);
    }

    private String getHeuristicName(int heuristicType) {
        switch (heuristicType) {
            case 1: return "Số ô sai vị trí";
            case 2: return "Khoảng cách Manhattan";
            case 3: return "Số ô sai hàng/cột";
            case 4: return "Khoảng cách Euclid";
            default: return "Không xác định";
        }
    }

    private void addResult(Node n) {
        if (n == null) return;
        if (n.parent != null) {
            addResult(n.parent);
        }
        RESULT.add(n.state.value);
    }

    public Vector<int[]> getResult() {
        return RESULT != null ? RESULT : new Vector<>();
    }
}