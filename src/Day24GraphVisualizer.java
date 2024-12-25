import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.renderers.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class Day24GraphVisualizer
{
    public static void visualize(Graph graph) {

//        LeftRightLayout<String, String> layout = new LeftRightLayout<>(graph);

        FRLayout<String, String> layout = new FRLayout<>(graph);
        layout.setSize(new Dimension(600, 600));

        int width = 600;
        int height = 600;

        int leftX = width / 8;
        int rightX = 7 * width / 8;
        int y = height * 4 / 10;

        // Manually set positions for specific nodes
        for (Object v : graph.getVertices())
        {
            String name = v.toString();
            if (name.length() != 3) continue;
            try
            {
                int idx = Integer.parseInt(name.substring(1));
                layout.setLocation(name, new Point2D.Double(leftX, y + idx));
            } catch (NumberFormatException e) {
                continue;
            }

        }

        BasicVisualizationServer<String, String> vv =
                new BasicVisualizationServer<>(layout);
        vv.setPreferredSize(new Dimension(600, 1000));
        vv.getRenderContext().setVertexLabelTransformer(vx -> {return vx.toString().substring(0,3);});
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        // Set vertex color and size
        vv.getRenderContext().setVertexFillPaintTransformer(vertex -> { return Color.WHITE;});
        vv.getRenderContext().setVertexShapeTransformer(vertex -> { return new Rectangle(-15, -15, 30, 30);});

        JFrame frame = new JFrame("Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }

    private static Graph<String, String> createGraph() {
        Graph<String, String> graph = new SparseMultigraph<>();
        // Add vertices and edges here
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addEdge("Edge-A-B", "A", "B");
        return graph;
    }


    public static class LeftRightLayout<V, E> extends AbstractLayout<V, E>
    {

        public LeftRightLayout(Graph<V, E> graph)
        {
            super(graph);
        }

        @Override
        public void initialize()
        {
            int width = 600;
            int height = 600;

            int leftX = width / 8;
            int rightX = 7 * width / 8;
            int y = height * 4 / (getGraph().getVertexCount() + 1);

            for (V v : getGraph().getVertices())
            {
                String name = v.toString();

                if (name.length() == 3 && (name.startsWith("x") || name.startsWith("y")))
                {
                    int idx = Integer.parseInt(name.substring(1));
                    idx += name.startsWith("y") ? height / 2 : 0;
                    System.err.printf("%s %d %d%n", name, leftX, idx + y);
                    setLocation(v, new Point2D.Double(leftX, idx * y));
                } else if (name.length() == 3 && name.startsWith("z"))
                {
                    int idx = Integer.parseInt(name.substring(1));
                    setLocation(v, new Point2D.Double(rightX, idx * 2 * y));
                } else {
                    setLocation(v, new Point2D.Double(rightX, 2 * y));
                }
            }
        }

        @Override
        public void reset()
        {
            initialize();
        }
    }
}

