package se.kth.jabeja;

import se.kth.jabeja.config.Config;
import se.kth.jabeja.rand.RandNoGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Reinit {
  static void redistributeColors(HashMap<Integer, Node> graph, Config config) {
    calcEdgeCut(graph);

    // Reset color of all nodes
    for (Node node : graph.values()) {
      node.setColor(-1);
    }

    // Set up priority queues and insert first nodes
    ArrayList<PriorityQueue<NodeExt>> queues = new ArrayList<>();
    for (int p = 0; p < config.getNumPartitions(); p++) {
      queues.add(new PriorityQueue<>(1, new NodeExtComparator()));

      // Pick a random node as first representative
      Node firstNode;
      do {
        int index = RandNoGenerator.nextInt(graph.size()) + 1;
        firstNode = graph.get(index);
      } while (firstNode.getColor() != -1);
      firstNode.setColor(p);

      // Insert the neighbors of the first node as candidates for next colorization
      for (int index : firstNode.getNeighbours()) {
        Node neighbor = graph.get(index);
        if (neighbor.getColor() == -1) {
          NodeExt neighborExt = new NodeExt(neighbor);
          queues.get(p).add(neighborExt);
        }
      }
    }

    boolean allColored = false;
    while (!allColored) {
      for (int p = 0; p < config.getNumPartitions(); p++) {
        NodeExt bestCandidate = null;

        // Find next best candidate to recolor from queue
        PriorityQueue queue = queues.get(p);
        if (!queue.isEmpty()) {
          do {
            bestCandidate = (NodeExt) queue.poll();
          } while (bestCandidate != null && bestCandidate.node.getColor() != -1);
        }

        // If no candidates left, pick random
        if (bestCandidate == null) {
          int startIndex = RandNoGenerator.nextInt(graph.size()) + 1;
          Node randomCandidate = null;
          for (int index = startIndex; index != (startIndex - 1) % graph.size(); index = (index + 1) % graph.size()) {
            randomCandidate = graph.get(index + 1);
            if (randomCandidate.getColor() == -1)
              break;
          }
          if (randomCandidate != null && randomCandidate.getColor() == -1) {
            bestCandidate = new NodeExt(randomCandidate);
          } else {
            allColored = true;
          }
        }

        if (bestCandidate != null) {
          // Recolor candidate
          bestCandidate.node.setColor(p);

          // Update keys of other candidates in queue
          for (NodeExt candidate : queues.get(p)) {
            if (candidate.node.getNeighbours().contains(bestCandidate.node.getId()))
              candidate.incNumSameColorNeighbors();
          }

          // Add neighbors of the candidate as new candidates to queue
          for (int n : bestCandidate.node.getNeighbours()) {
            Node neighbor = graph.get(n);
            if (neighbor.getColor() == -1) {
              // Check that potential candidate is not already in the queue
              boolean candidateInQueue = false;
              for (Object queueObj : queue) {
                NodeExt queueNode = (NodeExt) queueObj;
                if (queueNode.node.getId() == n) {
                  candidateInQueue = true;
                  break;
                }
              }

              if (!candidateInQueue) {
                // Add new candidate
                NodeExt nodeExt = new NodeExt(neighbor);
                queues.get(p).add(nodeExt);
              }
            }
          }
        }
      }
    }

    calcEdgeCut(graph);
  }

  private static void calcEdgeCut(HashMap<Integer, Node> graph) {
    int grayLinks = 0;

    for (Integer i : graph.keySet()) {
      Node node = graph.get(i);
      int nodeColor = node.getColor();
      ArrayList<Integer> nodeNeighbors = node.getNeighbours();

      if (nodeNeighbors != null) {
        for (int n : nodeNeighbors) {
          Node p = graph.get(n);
          int pColor = p.getColor();

          if (nodeColor != pColor)
            grayLinks++;
        }
      }
    }

    int edgeCut = grayLinks / 2;
    System.out.println("cut: " + edgeCut);
  }

  static class NodeExt {
    Node node;
    int numSameColorNeighbors;
    int numDifferentColorNeighbors;
    double key;

    NodeExt(Node node) {
      this.node = node;
      this.numSameColorNeighbors = 1;
      this.numDifferentColorNeighbors = node.getNeighbours().size() - 1;
      updateKey();
    }

    void incNumSameColorNeighbors() {
      numSameColorNeighbors++;
      numDifferentColorNeighbors--;
      updateKey();
    }

    void updateKey() {
      if (numDifferentColorNeighbors > 0)
        key = (double) numSameColorNeighbors / numDifferentColorNeighbors;
      else
        key = numSameColorNeighbors * 2; // to prevent divide by zero exception
    }
  }

  static class NodeExtComparator implements Comparator<NodeExt> {
    public int compare(NodeExt n1, NodeExt n2) {
      return Double.compare(-n1.key, -n2.key);
    }
  }
}

