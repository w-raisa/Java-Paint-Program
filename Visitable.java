package ca.utoronto.utm.paint;

import java.io.IOException;

/**
 * 
 * @author Wafiqah Raisa
 * The interface for an object that is the Visitable in the 
 * Visitor design pattern.
 */
public interface Visitable {

	public void accept(Visitor visitor) throws IOException;
	
}
