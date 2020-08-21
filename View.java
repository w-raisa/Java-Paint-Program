package ca.utoronto.utm.paint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class View implements EventHandler<ActionEvent> {

	private PaintModel paintModel;
	private PaintPanel paintPanel;
	private ShapeChooserPanel shapeChooserPanel;
	private Stage stage;
	
	private static SaveVisitor saveVisitor;
	
	public View(PaintModel model, Stage stage) {
		this.stage = stage;
		this.paintModel = model;
		initUI(stage);
	}

	public PaintModel getPaintModel() {
		return this.paintModel;
	}

	public void setPaintModel(PaintModel paintModel) {
		this.paintModel=paintModel;
		this.paintPanel.setPaintModel(paintModel);
	}
	private void initUI(Stage stage) {

		this.paintPanel = new PaintPanel(this.paintModel);
		this.shapeChooserPanel = new ShapeChooserPanel(this);

		BorderPane root = new BorderPane();
		root.setTop(createMenuBar());
		root.setCenter(this.paintPanel);
		root.setLeft(this.shapeChooserPanel);

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Paint");
		stage.show();
	}

	public PaintPanel getPaintPanel() {
		return paintPanel;
	}

	public ShapeChooserPanel getShapeChooserPanel() {
		return shapeChooserPanel;
	}

	private MenuBar createMenuBar() {

		MenuBar menuBar = new MenuBar();
		Menu menu;
		MenuItem menuItem;

		// A menu for File

		menu = new Menu("File");

		menuItem = new MenuItem("New");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Open");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Save");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menu.getItems().add(new SeparatorMenuItem());

		menuItem = new MenuItem("Exit");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuBar.getMenus().add(menu);

		// Another menu for Edit

		menu = new Menu("Edit");

		menuItem = new MenuItem("Cut");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Copy");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Paste");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menu.getItems().add(new SeparatorMenuItem());

		menuItem = new MenuItem("Undo");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Redo");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuBar.getMenus().add(menu);

		return menuBar;
	}

	public void setPaintPanelShapeManipulatorStrategy(ShapeManipulatorStrategy strategy) {
		this.paintPanel.setShapeManipulatorStrategy(strategy);
	}

	@Override
	public void handle(ActionEvent event) {
		System.out.println(((MenuItem) event.getSource()).getText());
		String command = ((MenuItem) event.getSource()).getText();
		if (command.equals("Open")) {
			FileChooser fc = new FileChooser();
			File file = fc.showOpenDialog(this.stage);

			if (file != null) {
				System.out.println("Opening: " + file.getName() + "." + "\n");
				BufferedReader bufferedReader = null;
				try {
					bufferedReader = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				PaintModel paintModel = new PaintModel();
				PaintFileParser parser = new PaintFileParser();
				try {
					boolean b = parser.parse(bufferedReader,  paintModel);
					if (b == false) {
						Alert alert = new Alert(AlertType.INFORMATION);
				        alert.setTitle("Error Message");
				        alert.setHeaderText("File not in correct format!");
				        alert.setContentText(parser.getErrorMessage());
				        alert.showAndWait();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				this.setPaintModel(paintModel);
			} else {
				System.out.println("Open command cancelled by user." + "\n");
			}
		} else if (command.equals("Save")) {
			FileChooser fc = new FileChooser();
			File file = fc.showSaveDialog(this.stage);

			if (file != null) {
				System.out.println("Saving: " + file.getName() + "." + "\n");
				BufferedWriter wr = null;
				PrintWriter writer = null;
				try {
					writer = new PrintWriter(file);
					View.save(writer, paintModel);
//					saveVisitor = new SaveVisitor(writer, this.paintModel);
//					saveVisitor.save();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					writer.close();
				}
			} else {
				System.out.println("Save command cancelled by user." + "\n");
			}
		} 
		else if (command.equals("New")) {
			this.paintModel.reset();
			//this.setPaintModel(new PaintModel());
		}	
	}
	
	/**
	 * Save the given paintModel to the open file
	 * @param writer
	 * @param paintModel
	 */
	public static void save(PrintWriter writer, PaintModel paintModel) {
		try { saveVisitor = new SaveVisitor(writer, paintModel); } 
		catch (FileNotFoundException e) { e.printStackTrace(); }
		try { saveVisitor.save(); }
		catch (IOException e) { e.printStackTrace(); }
	}
}
