package model;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class DTNView extends JFrame {

	private static final long serialVersionUID = 4040479539604325307L;
	
	private Color GRN = new Color(151, 221, 0);
	private Color GRA = new Color(240, 240, 240);
	
	public DirectedOrderedSparseMultigraph<String, String> g;
	public VisualizationViewer<String, String> vv;

	public DTNView(DirectedOrderedSparseMultigraph<String, String> graph) {
		// System.out.println(g.toString());
		g = graph;

		// layout
		Layout<String, String> layout = new CircleLayout<String, String>(g);
		layout.setSize(new Dimension(400, 400));

		// viewer
		vv = new VisualizationViewer<String, String>(layout);
		vv.setPreferredSize(new Dimension(400, 400));
		vv.setBackground(Color.white);

		// mouse
		DefaultModalGraphMouse<Integer, Paint> gm = new DefaultModalGraphMouse<Integer, Paint>();
		gm.setMode(Mode.PICKING);
		vv.setGraphMouse(gm);

		// render
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		RenderContext<String, String> rc = vv.getRenderContext();
//		rc.setLabelOffset(10);
		rc.setVertexLabelTransformer(new ToStringLabeller<String>());
		rc.setEdgeLabelTransformer(new ToStringLabeller<String>());
		rc.setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.black));

		rc.setVertexFillPaintTransformer(new Transformer<String, Paint>() {
			public Paint transform(String e) {
				return GRN;
			}
		});
		
		rc.setVertexDrawPaintTransformer(new Transformer<String, Paint>() {
			public Paint transform(String e) {
				return GRN;
			}
		});

		rc.setEdgeDrawPaintTransformer(new Transformer<String, Paint>() {
			public Paint transform(String e) {
				if (vv.getPickedEdgeState().isPicked(e))
					return Color.lightGray;
				return GRA;
			}
		});

		rc.setArrowDrawPaintTransformer(new Transformer<String, Paint>() {
			public Paint transform(String e) {
				if (vv.getPickedEdgeState().isPicked(e))
					return Color.black;
				return Color.lightGray;
			}
		});

		rc.setArrowFillPaintTransformer(new Transformer<String, Paint>() {
			public Paint transform(String e) {
				if (vv.getPickedEdgeState().isPicked(e))
					return Color.black;
				return Color.lightGray;
			}
		});

		rc.setEdgeStrokeTransformer(new Transformer<String, Stroke>() {
			public Stroke transform(String e) {
				if (vv.getPickedEdgeState().isPicked(e)) {
					return new BasicStroke(5.0f);
				}
				return new BasicStroke(5.0f);
			}
		});

		rc.setVertexFontTransformer(new Transformer<String, Font>() {
			public Font transform(String arg0) {
				return new Font("Arial", Font.BOLD, 20);
			}
		});

		rc.setEdgeFontTransformer(new Transformer<String, Font>() {
			public Font transform(String e) {
				return new Font("Arial", Font.BOLD, 20);
			}
		});

		rc.setVertexShapeTransformer(new Transformer<String, Shape>() {
			public Shape transform(String v) {
				Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);
				return AffineTransform.getScaleInstance(1, 1)
						.createTransformedShape(circle);
			}
		});

		AbstractEdgeShapeTransformer<String, String> aesf = (AbstractEdgeShapeTransformer<String, String>) rc
				.getEdgeShapeTransformer();
		aesf.setControlOffsetIncrement(40);

		// add view
		getContentPane().add(vv, BorderLayout.CENTER);
		setTitle("Disjunctive Temporal Network");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		DirectedOrderedSparseMultigraph<String, String> g = new DirectedOrderedSparseMultigraph<String, String>();

		String[] v = new String[5];
		for (int i = 0; i < 5; i++) {
			v[i] = "X" + (i + 1);
			g.addVertex(v[i]);
		}
		g.addEdge("<1,5>", v[0], v[1]);
		g.addEdge("<1,30>", v[1], v[0]);
		g.addEdge("<2,20>", v[1], v[4]);
		g.addEdge("<3,15>", v[2], v[3]);
		g.addEdge("<4,12>", v[0], v[4]);
		g.addEdge("<5,40>", v[1], v[3]);

		DTNView view = new DTNView(g);
		view.g.addEdge("ff", v[0], v[4]);
	}
}
