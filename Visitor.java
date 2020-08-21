package ca.utoronto.utm.paint;

import java.io.IOException;
/**
 * 
 * @author Wafiqah Raisa
 * The interface for a Visitor in the Visitor design pattern.
 */
public interface Visitor {
	
	public void visit(SquiggleCommand sC) throws IOException;
	public void visit(CircleCommand cC) throws IOException;
	public void visit(RectangleCommand rC) throws IOException;

}
