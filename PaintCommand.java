package ca.utoronto.utm.paint;
import java.io.Serializable;
import java.util.Observable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class PaintCommand extends Observable implements Visitable {
	private Color color;
	private boolean fill;
	
	PaintCommand(){
		// Pick a random color for this
		int r = (int)(Math.random()*256);
		int g = (int)(Math.random()*256);
		int b= (int)(Math.random()*256);
		this.color = Color.rgb(r, g, b);
		
		this.fill = (1==(int)(Math.random()*2));
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public boolean isFill() {
		return fill;
	}
	public void setFill(boolean fill) {
		this.fill = fill;
	}
	
	/**
	 * Put into format all the information of a PaintCommand that is required
	 * so that it can be saved to a text file with correct formatting.
	 * @return s which is String containing all the needed attributes of a PaintCommand 
	 */
	public String saveIntoFile() {
		int r = (int)(this.color.getRed() * 255);
		int g = (int)(this.color.getGreen() * 255);
		int b = (int)(this.color.getBlue() * 255);
		String s = "";
		s+="\tcolor:"+r+","+g+","+b+"\n";
		s+="\tfilled:"+this.fill+"\n";
		return s;
	}
	
	public String toString(){
		double r = this.color.getRed();
		double g = this.color.getGreen();
		double b = this.color.getBlue();

		String s = "";
		s+="\tcolor:"+r+","+g+","+b + "\n";
		s+="\tfilled:"+this.fill + "\n";
		return s;
	}
	
	public abstract void execute(GraphicsContext g);
}
