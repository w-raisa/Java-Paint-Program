package ca.utoronto.utm.paint;

import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about
 * its effort to parse a file. After a successful parse, an instance
 * will have an ArrayList of PaintCommand suitable for rendering.
 * If there is an error in the parse, the instance stores information
 * about the error. For more on the format of Version 1.0 of the paint 
 * save file format, see the associated documentation.
 * 
 * @author Wafiqah Raisa
 *
 */
public class PaintFileParser {
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage =""; // error encountered during parse
	private PaintModel paintModel; 
	
	/**
	 * Below are Patterns used in parsing 
	 */
	private Pattern pFileStart=Pattern.compile("^PaintSaveFileVersion1.0$");
	private Pattern pFileEnd=Pattern.compile("^EndPaintSaveFile$");

	private Pattern pColor = Pattern.compile("^color:(\\d{1,3}),(\\d{1,3}),(\\d{1,3})$");
	private Pattern pFilled = Pattern.compile("^filled:(true|false)$");
	
	private Pattern pCircleStart=Pattern.compile("^Circle$");

	private Pattern pCircleEnd=Pattern.compile("^EndCircle$");
	private Pattern pCircleCenter = Pattern.compile("center:\\((\\d{1,3}),(\\d{1,3})\\)");
	private Pattern pCircleRadius = Pattern.compile("radius:([0-9]+)");
	
	private Pattern pRectangleStart = Pattern.compile("^Rectangle$");
	private Pattern pRectangleEnd = Pattern.compile("^EndRectangle$");
	private Pattern pRectangleP1 = Pattern.compile("^p1:\\((\\d{1,3}),(\\d{1,3})\\)");
	private Pattern pRectangleP2 = Pattern.compile("^p2:\\((-?\\d{1,3}),(-?\\d{1,3})\\)");
	
	private Pattern pSquiggleStart = Pattern.compile("^Squiggle$");
	private Pattern pSquiggleEnd = Pattern.compile("^EndSquiggle$");
	private Pattern pSquiggleStartPoints = Pattern.compile("^points$");
	private Pattern pSquigglePoint = Pattern.compile("^point:\\((-?\\d{1,3}),(-?\\d{1,3})\\)");
	private Pattern pSquiggleEndPoints = Pattern.compile("^endpoints$");	
	
	int[] cA;
	
	/**
	 * Store an appropriate error message in this, including 
	 * lineNumber where the error occurred.
	 * @param mesg
	 */
	private void error(String mesg){
		this.errorMessage = "Error in line "+lineNumber+" "+mesg;
	}
	
	/**
	 * Return the appropriate error message when an error is found in the
	 * file that is being parsed.
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage(){
		return this.errorMessage;
	}
	
	/**
	 * Parse the inputStream as a Paint Save File Format file.
	 * The result of the parse is stored as an ArrayList of Paint command.
	 * If the parse was not successful, this.errorMessage is appropriately
	 * set, with a useful error message.
	 * 
	 * @param inputStream the open file to parse
	 * @param paintModel the paint model to add the commands to
	 * @return whether the complete file was successfully parsed
	 * @throws FileNotFoundException 
	 */
	public boolean parse(BufferedReader inputStream, PaintModel paintModel) throws FileNotFoundException {
		this.paintModel = paintModel;
		this.errorMessage="";
		
		// During the parse, we will be building one of the 
		// following commands. As we parse the file, we modify 
		// the appropriate command.
		
		ArrayList<PaintCommand> paintCommands = new ArrayList<PaintCommand>();
		Point point = new Point(0,0);
		
		CircleCommand circleCommand = null; 
		RectangleCommand rectangleCommand = null;
		SquiggleCommand squiggleCommand = null;
		
		try {	
			int state=0; 
			Matcher m = null;
			String l;
			this.lineNumber=0;
			while ((l = inputStream.readLine()) != null) {
				this.lineNumber++;
				l = l.replaceAll("\\s*", "");
				if (l.equals("")) {
					continue;
				}
				switch(state){
					case 0:
						m=pFileStart.matcher(l);
						if(m.matches()){
							state=1;
							break;
						}
						else {
							error("Expected Start of Paint Save File");
							return false;
						}
					case 1: // Looking for the start of a new object or end of the save file
						m = pFileEnd.matcher(l);
						if (m.matches()){
							state = 17;
							break;
						}
						m=pCircleStart.matcher(l);
						if(m.matches()){
							circleCommand = new CircleCommand(point,0);
							state=2;
							break;
						}
						m = pRectangleStart.matcher(l);
						if (m.matches()) {
							state = 3;
							rectangleCommand = new RectangleCommand(point, point);
							break;
						}
						m = pSquiggleStart.matcher(l);
						if (m.matches()) {
							state = 4;
							squiggleCommand = new SquiggleCommand();
							break;
						}
						else {
							error("Expected Start of Shape Save File");
							return false;
						}
					case 2:
						m = pColor.matcher(l);
						if(m.matches()) {
							cA = this.colorSet(m.group(1), m.group(2), m.group(3));
							state = 5;
							if ((cA[0] < 0 || cA[0] > 255) || (cA[1] < 0 || cA[1] > 255) || (cA[2] < 0 || cA[2] > 255)) {
								error("Expected valid color value(s)");
								return false;
							}
							circleCommand.setColor(Color.rgb(cA[0], cA[1], cA[2]));
							break;
						}
						else {
							error("Expected Start of Circle Color");
							return false;
						}
					case 3:
						m = pColor.matcher(l);
						if (m.matches()) {
							cA = this.colorSet(m.group(1), m.group(2), m.group(3));
							if ((cA[0] < 0 || cA[0] > 255) || (cA[1] < 0 || cA[1] > 255) || (cA[2] < 0 || cA[2] > 255)) {
								error("Expected valid color value(s)");
								return false;
							}
							rectangleCommand.setColor(Color.rgb(cA[0], cA[1], cA[2]));
							state = 6;
							break;
						}
						else {
							error("Expected Start of Rectangle Color");
							return false;
						}
					case 4:
						m = pColor.matcher(l);
						if (m.matches()) {
							cA = this.colorSet(m.group(1), m.group(2), m.group(3));
							if ((cA[0] < 0 || cA[0] > 255) || (cA[1] < 0 || cA[1] > 255) || (cA[2] < 0 || cA[2] > 255)) {
								error("Expected valid color value(s)");
								return false;
							}
							squiggleCommand.setColor(Color.rgb(cA[0], cA[1], cA[2]));
							state = 7;
							break;
						}
						else {
							error("Expected Start of Squiggle Color");
							return false;
						}
					case 5:
						m = pFilled.matcher(l);
						if (m.matches()) {
							state = 8;
							circleCommand.setFill(Boolean.parseBoolean(m.group(1)));
							break;
						}
						else {
							error("Expected Start of Circle Filled");
							return false;
						}
					case 6:
						m = pFilled.matcher(l);
						if (m.matches()) {
							state = 9;
							rectangleCommand.setFill(Boolean.parseBoolean(m.group(1)));
							break;
						}
						else {
							error("Expected Start of Rectangle Filled");
							return false;
						}
					case 7:
						m = pFilled.matcher(l);
						if (m.matches()) {
							squiggleCommand.setFill(Boolean.parseBoolean(m.group(1)));
							state = 10;
							break;
						}
						else {
							error("Expected Start of Squiggle Filled");
							return false;
						}
					case 8:
						m = pCircleCenter.matcher(l);
						if (m.matches()) {
							circleCommand.setCentre(this.pointSet(m.group(1), m.group(2)));
							state = 11;
							break;
						}
						else {
							error("Expected Start of Circle Center");
							return false;
						}
					case 9:
						m = pRectangleP1.matcher(l);
						if (m.matches()) {
							rectangleCommand.setP1(this.pointSet(m.group(1), m.group(2)));
							state = 12;
							break;
						}
						else {
							error("Expected Start of Rectangle P1");
							return false;
						}
					case 10:
						m = pSquiggleStartPoints.matcher(l);
						if (m.matches()) {
							state = 13;
							break;
						}
						else {
							error("Expected Start of Squiggle Points");
							return false;
						}
					case 11:
						m = pCircleRadius.matcher(l);
						if (m.matches()) {
							circleCommand.setRadius(Integer.parseInt(m.group(1)));
							paintCommands.add(circleCommand);
							state = 14;
							break;
						}
						else {
							error("Expected Start of Circle Radius");
							return false;
						}
					case 12:
						m = pRectangleP2.matcher(l);
						if (m.matches()) {
							state = 15;
							rectangleCommand.setP2(this.pointSet(m.group(1), m.group(2)));
							paintCommands.add(rectangleCommand);
							break;
						}
						else {
							error("Expected Start of Rectangle P2");
							return false;
						}
					case 13:
						m = pSquigglePoint.matcher(l);
						if (m.matches()) {
							state = 13;
							squiggleCommand.add(this.pointSet(m.group(1), m.group(2)));
							break;
						}
						Matcher t = pSquiggleEndPoints.matcher(l);
						if (t.matches()) {
							state = 16;
							paintCommands.add(squiggleCommand);
							break;
						}
						else {
							error("Expected Start of Squiggle End Points");
							return false;
						}
					case 14:
						m = pCircleEnd.matcher(l);
						if (m.matches()) {
							state = 1;
							break;
						}
						else {
							error("Expected End of Circle End Save File");
							return false;
						}
					case 15:
						m = pRectangleEnd.matcher(l);
						if (m.matches()) {
							state = 1;
							break;
						}
						else {
							error("Expected End of Rectangle End Save File");
							return false;
						}
					case 16:
						m = pSquiggleEnd.matcher(l);
						if (m.matches()) {
							state = 1;
							break;
						}
						else {
							error("Expected End of Squiggle End Save File");
							return false;
						}
					case 17:
						if (l.equals("")) {
							break;
						}
						else {
							error("File did not end correctly");
							return false;
						}
				}
			}
		}  
		catch (Exception e){
			
		}
		this.paintModel.load(paintCommands);

		return true;
	}
	
	/**
	 * Returns an array of colours, holding the red, green, and blue values.
	 * @param colorR, which is an int denoting the red part of the colour.
	 * @param colorG, which is an int denoting the green part of the colour.
	 * @param colorB, which is an int denoting the blue part of the colour.
	 * @return colorArray, which is an array of integers denoting the red, green,
	 * and blue values respectively to the element they are in.
	 */
	public int[] colorSet(String colorR, String colorG, String colorB) {
		int r = Integer.parseInt(colorR);
		int g = Integer.parseInt(colorG);
		int b = Integer.parseInt(colorB);
		int[] colorArray = {r,g,b};
		return colorArray;
	}
	
	/**
	 * Changes the group's information to an int so that an actual
	 * Point object can be made out of it. Returns a point then where the
	 * x coordinate is the first group and the y coordinate is the second 
	 * group.
	 * @param x, which is a String denoting the x coordinate of the point
	 * @param y  which is a String denoting the y coordinate of the point
	 * @return point, which is Point object that has an x and y coordinate. 
	 */
	public Point pointSet(String x, String y) {
		Point point;
		int xPoint = Integer.parseInt(x);
		int yPoint = Integer.parseInt(y);
		point = new Point(xPoint, yPoint);
		return point;
	}
	
}
