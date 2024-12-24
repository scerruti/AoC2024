import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.reflect.Array.set;

public class Day23
{
    private static HashMap<String, Set<String>> nodes = new HashMap<String, Set<String>>();
    private static Set<String> trios = new HashSet<String>();

    public static void main(String[] args)
    {
        readNodesFromFile("example/day23.txt");
        trios.addAll(getPartners(nodes.keySet()));
        System.out.println(trios);
        trios = trios.stream().filter(s -> {List<String> c = Arrays.asList(s.split(",")); return c.stream().anyMatch(q -> q.startsWith("t"));}).collect(Collectors.toSet());
        System.out.println(trios.size());

//        String password = getLargestClique();
        Set<Set<String>> maxSet = findMaximalCliques(nodes);
        String password = "";
        int maxLength = maxSet.stream().mapToInt(Set::size).max().getAsInt();
        Optional<Set<String>> lan = maxSet.stream().filter(s -> s.size() == maxLength).findFirst();
        if (lan.isPresent()) {
            password = lan.get().stream().sorted(String::compareTo).collect(Collectors.joining(","));
        }

        System.out.println(password);
    }

    /*
    algorithm BronKerbosch2(R, P, X) is
    if P and X are both empty then
        report R as a maximal clique
    choose a pivot vertex u in P ⋃ X
    for each vertex v in P \ N(u) do
        BronKerbosch2(R ⋃ {v}, P ⋂ N(v), X ⋂ N(v))
        P := P \ {v}
        X := X ⋃ {v}
     */


    private static Set<Set<String>> maximalCliques = new HashSet<>();

    private static Set<Set<String>> findMaximalCliques(Map<String, Set<String>> graph) {
        bronKerbosch(graph, new HashSet<>(), new HashSet<>(graph.keySet()), new HashSet<>());
        return maximalCliques;
    }

    private static void bronKerbosch(Map<String, Set<String>> graph, Set<String> R, Set<String> P, Set<String> X) {
        if (P.isEmpty() && X.isEmpty()) {
            maximalCliques.add(new HashSet<>(R));
            return;
        }

        String pivot = choosePivot(P, X, graph);

        Set<String> PMinusPivotNeighbors = new HashSet<>(P);
        if (!pivot.isEmpty()) {
            PMinusPivotNeighbors.removeAll(graph.get(pivot));
        }

        for (String v : PMinusPivotNeighbors) {
            Set<String> newR = new HashSet<>(R);
            newR.add(v);
            Set<String> newP = new HashSet<>(P);
            newP.retainAll(graph.get(v));
            Set<String> newX = new HashSet<>(X);
            newX.retainAll(graph.get(v));
            bronKerbosch(graph, newR, newP, newX);

            P.remove(v);
            X.add(v);
        }
    }

    private static String choosePivot(Set<String> P, Set<String> X, Map<String, Set<String>> graph) {
        int maxIntersection = -1;
        String pivot = "";
        for (String v : P.isEmpty() ? X : P) {
            HashSet<String> N = new HashSet<>(P);
            N.retainAll(graph.get(v));
            int intersectionSize = N.size();
            if (intersectionSize > maxIntersection) {
                maxIntersection = intersectionSize;
                pivot = v;
            }
        }
        return pivot;
    }

    private static String getLargestClique() {
        String largestClique = "";

        for (String n1 : nodes.keySet()) {
            System.out.println("\n\n" + n1);
            ArrayDeque<String> visited = new ArrayDeque<>();
            visited.push(n1);
            for (String n2 : nodes.get(n1) ) {
                visited.push(n2);

                Set<String> list = new HashSet(nodes.get(n2));
                list.retainAll(nodes.get(n1));
                list.removeAll(visited);

                String clique = getLargestClique(visited, list, largestClique);

                if (clique.length() > largestClique.length()) {
                    largestClique = clique;
                }

                visited.pop();
            }
            System.out.println(largestClique);
        }
        return largestClique;
    }

    private static String getLargestClique(ArrayDeque<String> visited, Set<String> strings, String largestClique)
    {
        if (strings.isEmpty()) {
            String clique = String.join(",", strings.stream().sorted().collect(Collectors.toList()));
            return clique.length() > largestClique.length() ? clique : largestClique;
        }
        if (visited.size() * 3 + strings.size()*3 - 1 < largestClique.length()) {
            return largestClique;
        }

        for (String n : strings) {
            if (visited.contains(n)) continue;
            visited.push(n);

            HashSet<String> list = new HashSet<>(strings);
            list.retainAll(nodes.get(n));
            list.removeAll(visited);

            String clique = getLargestClique(visited, list, largestClique);
            if (clique.length() > largestClique.length()) {
                largestClique = clique;
            }
            visited.pop();
        }
        return largestClique;
    }

    private static Set<String> getPartners(Set<String> list) {
        return getPartners(list, new HashSet<>(), 3);
    }

    private static Set<String> getPartners(Set<String> list, HashSet<String> visited, int size)
    {
        if (visited.size() == size) {
            return Collections.singleton(visited.stream().sorted(String::compareTo).collect(Collectors.joining(",")));
        }

        Set<String> partners = new HashSet<>();
        for (String node : list) {
            if (visited.contains(node)) continue;
            boolean found = true;
            for (String v : visited) {
                if (!nodes.get(v).contains(node)) {
                    found = false;
                    continue;
                }
            }
            if (!found) continue;

            HashSet myVisited = new HashSet(visited);
            myVisited.add(node);
            partners.addAll(getPartners(nodes.get(node), myVisited, size));
        }
        return partners;
    }

    public static int[] readNodesFromFile(String filePath)
    {
        List<Integer> integers = new ArrayList<>();
        InputStream stream = Day23.class.getResourceAsStream(filePath);
        if (stream == null)
        {
            System.err.println("File not found: " + filePath);
            return new int[0];
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] parts = line.split("-"); // Split by whitespace
                nodes.putIfAbsent(parts[0], new HashSet<>());
                nodes.get(parts[0]).add(parts[1]);

                nodes.putIfAbsent(parts[1], new HashSet<>());
                nodes.get(parts[1]).add(parts[0]);
            }
        } catch (IOException e)
        {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }

        // Convert the list to an array and return
        return integers.stream().mapToInt(Integer::intValue).toArray();
    }
}
