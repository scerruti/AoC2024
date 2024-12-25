import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

public class Day24
{
    private static final HashMap<String, Operand> memory = new HashMap<>();
    private static final ArrayDeque<Operator> process = new ArrayDeque<>();
    private static final Graph<String, String> graph = new SparseMultigraph<>();

    public static void main(String[] args)
    {
        readSystem("example/day24.txt");
        visualizeGraph();

        part1();
        part2();
    }

    private static void visualizeGraph()
    {
        Day24GraphVisualizer.visualize(graph);
    }

    private static void part1()
    {
        System.out.printf("Part 1: %d%n%n", runMachine());
    }

    private static long runMachine() {
        while (!process.isEmpty())
        {
            Operator op = process.pop();
            op.execute();
        }

        List<String> outputRegisters = memory.keySet().stream().filter(s -> s.startsWith("z")).sorted().toList();
        long result = 0;
        for (String o : outputRegisters)
        {
            if (memory.get(o).isTrue())
            {
                long placeValue = (long) Math.pow(2, Integer.parseInt(o.substring(1)));
                result += placeValue;
            }
        }
        return result;
    }


    private static void part2()
    {
        for (String o : memory.keySet().stream().filter(s -> s.startsWith("z")).sorted().toList()) {
            Node node = memory.get(o);
            System.out.print(node.getName() + " = ");
            System.out.println(traverse(node));
        }
//        for (int i = 0; i < 5; i++)
//        {
//            clearMemory();
//            setNumber("x", (long) Math.pow(2, i));
//            setNumber("y", 31);
//
//            bootstrap();
//            long result = runMachine();
//            System.out.println((long) Math.pow(2, i) + " " + result);
//        }
    }

    public static String traverse(Node node) {
        if (node instanceof Operand && ((Operand) node).backOperators.isEmpty()) {
            return node.getName();
        } else if (node instanceof Operand) {
            return traverse(((Operand) node).backOperators.get(0));
        } else {
            return "( " + traverse(((Operator) node).from[0]) +
                    " " +  node.getName() + "("+((Operator) node).to.getName()+")"+
                    " " + traverse(((Operator) node).from[1]) +" )";
        }
    }

    private static void bootstrap()
    {
        process.clear();
        Set<Operator> operators = new HashSet<>();
        operators.clear();
        memory.keySet().stream()
                .filter(s -> s.startsWith("x") || s.startsWith("y"))
                .forEach(o -> operators.addAll(memory.get(o).operators));
        process.addAll(operators);
    }

    private static void clearMemory()
    {
        for (String o : memory.keySet())
        {
            memory.get(o).setState(Operand.State.UNKNOWN);
        }
    }

    private static void setNumber(String name, long value)
    {
        memory.keySet().stream().filter(s -> s.startsWith(name)).forEach(o -> memory.get(o).setState(Operand.State.FALSE));

        long place = 1;
        for (int i = 0; place <= value; i++)
        {
            if ((place & value) != 0)
            {
                String register = String.format("%s%02d", name, i);
                Operand operand = memory.get(register);
                if (operand != null) operand.setState(Operand.State.TRUE);
            }
            place <<= 1;
        }
    }

    public static void readSystem(String filePath)
    {
        InputStream stream = Day23.class.getResourceAsStream(filePath);
        if (stream == null)
        {
            System.err.println("File not found: " + filePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                Node.construct(line);
            }
        } catch (IOException e)
        {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    static abstract class Node
    {
        public static void construct(String line)
        {
            String[] tokens = line.trim().split(" ");
            if (tokens.length == 2)
            {
                new Operand(tokens[0].replace(":", ""),
                        tokens[1].equals("0") ? Operand.State.FALSE : Operand.State.TRUE);
            } else if (tokens.length == 5)
            {
                new Operator(tokens[1], tokens[0], tokens[2], tokens[4]);
            }
        }

        public abstract String getName();
    }

    private static class Operator extends Node
    {
        public Operation operation;
        public Operand[] from;
        public Operand to;

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Operator operator = (Operator) o;
            return operation == operator.operation && Objects.deepEquals(from, operator.from) && Objects.equals(to, operator.to);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(operation, Arrays.hashCode(from), to);
        }

        public Operator(String operation, String fromLeft, String fromRight, String toString)
        {
            this.operation = Operation.valueOf(operation);

            from = new Operand[2];
            addOrSetInput(0, fromLeft);
            addOrSetInput(1, fromRight);
            addOrSetOutput(toString);
            execute();

            String name = String.format("%s (%8x)", operation.toString(), hashCode());
            graph.addVertex(name);
            graph.addEdge(from[0].name+name, from[0].name, name);
            graph.addEdge(from[1].name+name, from[1].name, name);
            graph.addEdge(toString+toString, toString, name);
        }

        public void execute()
        {
            if (Arrays.stream(from).noneMatch(Operand::isUnknown))
            {
                to.setState(switch (this.operation)
                {
                    case AND ->
                            Arrays.stream(from).allMatch(Operand::isTrue) ? Operand.State.TRUE : Operand.State.FALSE;
                    case OR -> Arrays.stream(from).anyMatch(Operand::isTrue) ? Operand.State.TRUE : Operand.State.FALSE;
                    case XOR ->
                            Arrays.stream(from).anyMatch(Operand::isTrue) && !Arrays.stream(from).allMatch(Operand::isTrue) ? Operand.State.TRUE : Operand.State.FALSE;
                });
            }
        }

        private void addOrSetOutput(String output)
        {
            if (memory.containsKey(output))
            {
                this.to = memory.get(output);
            } else
            {
                this.to = new Operand(output);
            }
            this.to.addBackOperator(this);
        }

        private void addOrSetInput(int i, String fromLeft)
        {
            if (memory.containsKey(fromLeft))
            {
                this.from[i] = memory.get(fromLeft);
                this.from[i].setOperator(this);
                String name = String.format("%s (%8x)", operation.toString(), hashCode());
                graph.addEdge(this.from[i].name+name, this.from[i].name, name);
            } else
            {
                this.from[i] = new Operand(fromLeft, this);
            }
        }

        @Override
        public String toString()
        {
            return "Operator{" +
                    "operation=" + operation +
                    ", from=" + Arrays.stream(from).map(Operand::toString).collect(Collectors.joining()) +
                    ", to=" + to.toString() +
                    '}';
        }

        @Override
        public String getName()
        {
            return operation.name();
        }

        enum Operation
        {AND, OR, XOR}
    }

    private static class Operand extends Node
    {
        public String name;
        public State state;
        public List<Operator> operators = new ArrayList<>();
        public List<Operator> backOperators = new ArrayList<>();

        public Operand(String name, Operator operator, State state)
        {
            this.name = name;
            if (operator != null) this.operators.add(operator);
            this.state = state;
            memory.put(name, this);

        }


        public Operand(String name)
        {
            this(name, null, Operand.State.UNKNOWN);
        }

        public Operand(String name, Operator operator)
        {
            this(name, operator, Operand.State.UNKNOWN);
        }

        public Operand(String name, State state)
        {
            this(name, null, state);
        }

        public boolean isUnknown()
        {
            return (this.state == State.UNKNOWN);
        }

        public void setState(State state)
        {
            this.state = state;
            if (state != State.UNKNOWN && !this.operators.isEmpty())
            {
                process.addAll(this.operators);
            }
        }

        public boolean isTrue()
        {
            return this.state == State.TRUE;
        }

        public void setOperator(Operator operator)
        {
            this.operators.add(operator);
        }

        public void addBackOperator(Operator operator)
        {
            this.backOperators.add(operator);
        }

        @Override
        public String toString()
        {
            return name + "{" + state + '}';
        }

        @Override
        public String getName()
        {
            return this.name;
        }

        enum State
        {TRUE, FALSE, UNKNOWN}
    }

}
