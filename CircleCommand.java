package ca.utoronto.utm.paint;

import java.io.IOException;

import javafx.scene.canvas.GraphicsContext;

public class CircleCommand extends PaintCommand {
	private Point centre;
	private int radius;
	
	public CircleCommand(Point centre, int radius){
		super();
		this.centre = centre;
		this.radius = radius;
	}
	
	public Point getCentre() { return centre; }
	
	public void setCentre(Point centre) { 
		this.centre = centre; 
		this.setChanged();
		this.notifyObservers();
	}
	public int getRadius() { return radius; }
	
	public void setRadius(int radius) { 
		this.radius = radius; 
		this.setChanged();
		this.notifyObservers();
	}
	
	public void execute(GraphicsContext g){
		int x = this.getCentre().x;
		int y = this.getCentre().y;
		int radius = this.getRadius();
		if(this.isFill()){
			g.setFill(this.getColor());
			g.fillOval(x-radius, y-radius, 2*radius, 2*radius);
		} else {
			g.setStroke(this.getColor());
			g.strokeOval(x-radius, y-radius, 2*radius, 2*radius);
		}
	}
	
	
	/**
	 * Saves the CircleCommand and all its attributes according to the  
	 * paintSaveFileFormat.txt and returns this format as a String.
	 * @return a String of the CircleCommand and all its attributes correctly formatted.
	 */
	@Override
	public String saveIntoFile() {
		String s = "";
		s += "\tcenter:" + "(" + this.centre.x + "," + this.centre.y + ")" + "\n";
		s += "\tradius:" + this.radius + "\n";
		return "Circle\n" + super.saveIntoFile() + s + "End Circle\n";
	}
	
	@Override
	public String toString() {
		String s = "";
		s += "center:" + "(" + this.centre.x + "," + this.centre.y + ")";
		s += "radius:" + this.radius;
		return super.toString() + s;
	}
	
	/**
	 * The Visitor calls the accept method of this CircleCommand so that the Visitor 
	 * @param visitor which is a Visitor that will take control of this CircleCommand. 
	 */
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	
	
}
