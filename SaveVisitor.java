package ca.utoronto.utm.paint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @author Wafiqah Raisa
 * The SaveVisitor class is-a object, and it is-a Visitor in the 
 * Visitor design pattern. Saving of the objects takes place in this class, and 
 * all the information about each of the commands is written into a text file
 * when saving.
 * SaveVisitor has-a PrintWriter, a BufferedWriter, a PaintModel and an ArrayList
 * holding PaintCommand objects. It responds to all the concrete command classes.
 */
public class SaveVisitor implements Visitor {
	
	private PrintWriter printWriter;
	private BufferedWriter bufferedWriter;
	private PaintModel paintModel;
	private ArrayList<PaintCommand> commands;
	
	/**
	 * the constructor for this class initializes its PrintWriter object to be 
	 * the PrintWriter object passed in, and initializes its PaintModel object to
	 * be the PaintModel object passed in.
	 * @param printWriter, which is a PrintWriter object
	 * @param paintModel, which is a PaintModel object
	 * @throws FileNotFoundException 
	 */
	public SaveVisitor(PrintWriter printWriter, PaintModel paintModel) throws FileNotFoundException {
		this.printWriter = printWriter;
		this.bufferedWriter = new BufferedWriter(this.printWriter);
		this.paintModel = paintModel;
	}
	
	/**
	 * Loops through the ArrayList holding the PaintCommand objects and saves
	 * them by sending this Visitor to the according Concrete Command class and 
	 * having that Concrete Command class accept this visitor.
	 * @throws IOException
	 */
	public void save() throws IOException {
		this.commands = this.paintModel.getCommands();
		this.bufferedWriter.write("Paint Save File Version 1.0\n");
		for (int i = 0; i < this.commands.size(); i++) { // 1st change
			this.commands.get(i).accept(this);
		}
		this.bufferedWriter.write("End Paint Save File");
		this.bufferedWriter.close();
	}
	
	/**
	 * Writes the attributes of a CircleCommand into a text file, and saves it. 
	 * @param circleCommand, which is a CircleCommand.
	 */
	@Override
	public void visit(CircleCommand circleCommand) throws IOException {
	    this.bufferedWriter.write(circleCommand.saveIntoFile());
	}
	
	/**
	 * Writes the attributes of a SquiggleCommand into a text file, and saves it. 
	 * @param SquiggleCommand, which is a SquiggleCommand.
	 */
	@Override
	public void visit(SquiggleCommand squiggleCommand) throws IOException {
		this.bufferedWriter.write(squiggleCommand.saveIntoFile());
	}

	/**
	 * Writes the attributes of a RectangleCommand into a text file, and saves it. 
	 * @param rectangleCommand, which is a RectangleCommand.
	 */
	@Override
	public void visit(RectangleCommand rectangleCommand) throws IOException {
		this.bufferedWriter.write(rectangleCommand.saveIntoFile());
	}
	
}
