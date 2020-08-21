package ca.utoronto.utm.paint;
import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;
import java.util.ArrayList;

public class SquiggleCommand extends PaintCommand {
	private ArrayList<Point> points=new ArrayList<Point>();
	
	public void add(Point p){ 
		this.points.add(p); 
		this.setChanged();
		this.notifyObservers();
	}
	public ArrayList<Point> getPoints(){ return this.points; }
	
	
	@Override
	public void execute(GraphicsContext g) {
		ArrayList<Point> points = this.getPoints();
		g.setStroke(this.getColor());
		for(int i=0;i<points.size()-1;i++){
			Point p1 = points.get(i);
			Point p2 = points.get(i+1);
			g.strokeLine(p1.x, p1.y, p2.x, p2.y);
		}
		
	}
	
	@Override 
	public String saveIntoFile() {
		String s = "";
		s += "\tpoints\n";
		for (Point point: this.points) {
			s += "\t\tpoint:(" + point.x + "," + point.y + ")\n";
		}
		s += "\tend points\n";
		return "Squiggle\n" + super.saveIntoFile() + s + "End Squiggle\n";
	}
	
	@Override 
	public String toString() {
		String s = "";
		for (Point point: this.points) {
			s += "point:(" + point.x + "," + point.y;
		}
		return super.toString() + s;
	}
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
}
