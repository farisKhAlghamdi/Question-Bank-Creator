import java.io.*;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;



public class Project extends Application {
	
	// list view to view the text of questions
	protected ListView<String> lv = new ListView<>();
	
	// object of type QuestionArray to hold the questions
	protected QuestionArray questionArray = new QuestionArray();
	
	// file object to read/write ./to the binary file
	protected File file = new File("QuestionBank.dat");
	
	// base root
	protected BorderPane root = new BorderPane();
	
	// scene to hold roots
	protected Scene scene = new Scene(root);
	
	public void start(Stage stage) {
		
		// set some properties of the root
		root.setPadding(new Insets(20));
		
		// set some properties of the list view
		lv.setFixedCellSize(60);
		lv.setPrefWidth(800);
		lv.setPrefHeight(500);
		
		// pane to hold the list view
		StackPane lVPane = new StackPane(lv);
		lVPane.setPadding(new Insets(20));
		
		// creating the buttons
		Button createBt = getButton("create");
		Button viewBt = getButton("view");
		Button editBt = getButton("edit");
		Button deleteBt = getButton("delete");
		Button exitBt = getButton("exit");
		
		// pane to hold the buttons
		BorderPane buttonsBox = new BorderPane();
		buttonsBox.setPadding(new Insets(20, 20, 20, 40));
		
		HBox hb = new HBox(40);
		hb.setAlignment(Pos.CENTER);
		
		hb.getChildren().addAll(createBt, viewBt, editBt, deleteBt);
		
		buttonsBox.setCenter(hb);
		buttonsBox.setRight(exitBt);
		
		// label for the title
		Label TitleLabel = new Label("Question Bank Creator");
		TitleLabel.setFont(new Font("Times New Roman", 38));
		
		// add the nodes to the root
		root.setCenter(lVPane);
		root.setBottom(buttonsBox);
		root.setTop(TitleLabel);
		BorderPane.setAlignment(TitleLabel, Pos.CENTER);
		
		// import the questions from the file, if any
		try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
			questionArray.setArrayOfQ((ArrayList<Question>)input.readObject());
			lv.setItems(questionArray.getListOfText());
		} 
		catch (IOException e1) {}
		catch (ClassNotFoundException e1) {}
		
		// set the handlers for the buttons
		createBt.setOnAction(new CreateHandler());
		viewBt.setOnAction(new ViewHandler());
		editBt.setOnAction(new EditHandler());
		
		deleteBt.setOnAction(e -> {
			// get what question the user select
			Question selectedQ = questionArray.getSelectedQuestion();
			// delete the question
			questionArray.deleteQuestion(selectedQ);
			// update the items of the List view
			lv.setItems(questionArray.getListOfText());
		});
		
		exitBt.setOnAction(e -> {
			
			try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))) {
				// write the arrayList that contains the questions to the binary file
				output.writeObject(questionArray.getArrayOfQ());
				
			} catch (IOException e11) {
				System.out.println("couldn't save properly");
			}
			System.exit(0);
		});
		
		// show the stage
		Image img = new Image("picture.png");
		stage.setTitle("Question Bank Creator");
		stage.getIcons().add(img);
		stage.setScene(scene);
		stage.show();
	}
	
	// method to get a button
	private Button getButton(String text) {
		Button bt = new Button(text);
		bt.setFont(new Font("Arial", 18));
		return bt;
	}
	
	// method to get label (for create and edit classes)
	private Label getLabel(String tx, int size) {
		Label lb = new Label(tx);
		lb.setFont(new Font("Calibri Light", size));
		return lb;
	}
	
	// method to get text field for user input (for create and edit classes)
	private TextField getTextField(int count, int height) {
		TextField tf = new TextField();
		tf.setPrefColumnCount(count);
		tf.setPrefHeight(height);
		return tf;
	}
	
	class CreateHandler implements EventHandler<ActionEvent> {
		
		public void handle(ActionEvent event) {
			
			// VBox to hold the labels and text fields
			VBox vb = new VBox(50);
			vb.setAlignment(Pos.CENTER);
			
			// HBox to hold label and text field for the text of the question
			HBox questionBox = new HBox(20);
			questionBox.setAlignment(Pos.CENTER);
			
			Label questionlabel = getLabel("Enter question: ", 22);
			TextField questionTF = getTextField(50, 50);
			
			questionBox.getChildren().addAll(questionlabel, questionTF);
			
			// Grid Pane to hold labels and text fields for the choices
			GridPane gp = new GridPane();
			gp.setAlignment(Pos.CENTER);
			gp.setHgap(20);
			gp.setVgap(20);
			
			Label aLabel = getLabel("Enter choice A: ", 18);
			Label bLabel = getLabel("Enter choice B: ", 18);
			Label cLabel = getLabel("Enter choice C: ", 18);
			Label dLabel = getLabel("Enter choice D: ", 18);
			
			TextField aTF = getTextField(40, 30);
			TextField bTF = getTextField(40, 30);
			TextField cTF = getTextField(40, 30);
			TextField dTF = getTextField(40, 30);
			
			gp.addRow(0, aLabel, aTF);
			gp.addRow(1, bLabel, bTF);
			gp.addRow(2, cLabel, cTF);
			gp.addRow(3, dLabel, dTF);
			
			// HBox to hold label and buttons for the correct answer
			HBox correctAnsBox = new HBox(20);
			correctAnsBox.setAlignment(Pos.CENTER);
			
			Label correctAnsLabel = getLabel("Correct answer: ", 15);
			
			RadioButton aRB = new RadioButton("A");
			RadioButton bRB = new RadioButton("B");
			RadioButton cRB = new RadioButton("C");
			RadioButton dRB = new RadioButton("D");
			ToggleGroup tg = new ToggleGroup();
			aRB.setToggleGroup(tg);
			bRB.setToggleGroup(tg);
			cRB.setToggleGroup(tg);
			dRB.setToggleGroup(tg);
			
			HBox choicesBox = new HBox(30);
			choicesBox.getChildren().addAll(aRB, bRB, cRB, dRB);
			
			correctAnsBox.getChildren().addAll(correctAnsLabel, choicesBox);
			
			// add the nodes to the VBox
			vb.getChildren().addAll(questionBox, gp, correctAnsBox);
			
			// HBox to hold the buttons save and cancel
			HBox buttonsBox = new HBox(80);
			buttonsBox.setPadding(new Insets(20));
			buttonsBox.setAlignment(Pos.CENTER);
			
			Button saveBt = getButton("Save");
			Button cancelBt = getButton("Cancel");
			
			buttonsBox.getChildren().addAll(cancelBt, saveBt);
			
			// label for the title
			Label TitleLabel = new Label("Create a question");
			TitleLabel.setFont(new Font("Times New Roman", 32));
			
			// root pane to hold everything
			BorderPane createPane = new BorderPane(vb);
			createPane.setPadding(new Insets(30));
			createPane.setBottom(buttonsBox);
			createPane.setTop(TitleLabel);
			BorderPane.setAlignment(TitleLabel, Pos.CENTER);
			
			// set the root of the scene to createPane root
			scene.setRoot(createPane);
			
			// set the handlers for the buttons
			
			saveBt.setOnAction(e -> {
				// get the correct answer
				String selectedChoice = ((RadioButton)tg.getSelectedToggle()).getText();
				
				// create a Question object and set its data fileds
				Question q = new Question(questionTF.getText(), selectedChoice);
				q.setChoices(aTF.getText(), bTF.getText(), cTF.getText(), dTF.getText());
				
				// add the question to the arrayList using QuestionArray object
				questionArray.addQuestion(q);
				
				// set and view the text of questions in the List view
				lv.setItems(questionArray.getListOfText());
				
				// set the root of the scene to the original root
				scene.setRoot(root);
			});
			
			cancelBt.setOnAction(e -> {
				scene.setRoot(root);
			});
			
		}
	}
	
	class ViewHandler implements EventHandler<ActionEvent> {
		
		public void handle(ActionEvent event) {
			
			// get what question the user select
			Question selectedQ = questionArray.getSelectedQuestion();
			// get the index of that question
			int questionIndex = lv.getSelectionModel().getSelectedIndex();
			
			// VBox to hold the labels and texts
			VBox vb = new VBox(50);
			
			// set some properties for the VBox
			vb.setAlignment(Pos.CENTER);
			vb.setStyle("-fx-background-color: white");
			vb.setPadding(new Insets(20));
			BorderPane.setMargin(vb, new Insets(20));
			
			// get the text of the question
			Text questionText = new Text(lv.getSelectionModel().getSelectedItem());
			questionText.setFont(new Font("Calibri Light", 26));
			
			Label questionlabel = getLabel("Q: ", new Font("Arial", 24), Color.RED, questionText);
			
			// Grid Pane to hold the choices
			GridPane gp = new GridPane();
			gp.setAlignment(Pos.CENTER);
			gp.setHgap(80);
			gp.setVgap(30);
			
			Label aLabel = getLabel("A- ", new Font("Arial", 24), Color.GREEN, selectedQ.getChoice(1), new Font("Calibri Light", 24));
			Label bLabel = getLabel("B- ", new Font("Arial", 24), Color.GREEN, selectedQ.getChoice(2), new Font("Calibri Light", 24));
			Label cLabel = getLabel("C- ", new Font("Arial", 24), Color.GREEN, selectedQ.getChoice(3), new Font("Calibri Light", 24));
			Label dLabel = getLabel("D- ", new Font("Arial", 24), Color.GREEN, selectedQ.getChoice(4), new Font("Calibri Light", 24));
			
			gp.add(aLabel, 0, 0);
			gp.add(bLabel, 1, 0);
			gp.add(cLabel, 0, 1);
			gp.add(dLabel, 1, 1);
			
			// get the correct answer
			Text correctAns = new Text(selectedQ.getCorrectAnswer());
			correctAns.setFont(new Font("Arial", 24));
			correctAns.setFill(Color.GREEN);
			
			Label correctAnsLabel = getLabel("Answer: ", new Font("Calibri", 20), Color.RED, correctAns);
			
			// add the nodes to the VBox
			vb.getChildren().addAll(questionlabel, gp, correctAnsLabel);
			
			// get the close button
			Button closeBt = getButton("close");
			
			// label for the title
			Label TitleLabel = new Label("Question " + (questionIndex + 1));
			TitleLabel.setFont(new Font("Times New Roman", 32));
			
			// root pane to hold everything
			BorderPane viewPane = new BorderPane(vb);
			viewPane.setPadding(new Insets(30));
			viewPane.setBottom(closeBt);
			viewPane.setTop(TitleLabel);
			BorderPane.setAlignment(TitleLabel, Pos.CENTER);
			BorderPane.setAlignment(closeBt, Pos.CENTER);
			
			// set the root of the scene to viewPane root
			scene.setRoot(viewPane);
			
			// set a handler for the close button
			closeBt.setOnAction(e -> {
				scene.setRoot(root);
			});	
		}
		
		// method to get a label with specific format
		private Label getLabel(String labelText, Font labelFont, Color labelColor, String text, Font textFont) {
			Text tx = new Text(text);
			tx.setFont(textFont);
			Label lb = getLabel(labelText, labelFont, labelColor, tx);
			return lb;
		}
		
		// overload the getLabel method
		private Label getLabel(String labelText, Font labelFont, Color labelColor, Text text) {
			Label lb = new Label(labelText, text);
			lb.setFont(labelFont);
			lb.setTextFill(labelColor);
			lb.setContentDisplay(ContentDisplay.RIGHT);
			lb.setGraphicTextGap(10);
			return lb;
		}
	}
	
	class EditHandler implements EventHandler<ActionEvent> {
		
		public void handle(ActionEvent event) {
			
			// get what question the user select
			Question selectedQ = questionArray.getSelectedQuestion();
			// get the index of that question
			int questionIndex = lv.getSelectionModel().getSelectedIndex();
			
			// get a pane similar to the createPane //
			
			VBox vb = new VBox(50);
			vb.setAlignment(Pos.CENTER);
			
			HBox questionBox = new HBox(20);
			questionBox.setAlignment(Pos.CENTER);
			
			Label questionlabel = getLabel("Enter question: ", 22);
			
			TextField questionTF = getTextField(50, 50);
			// set the text of the text field to the old text of the question
			questionTF.setText(selectedQ.getText());
			
			questionBox.getChildren().addAll(questionlabel, questionTF);
			
			GridPane gp = new GridPane();
			gp.setAlignment(Pos.CENTER);
			gp.setHgap(20);
			gp.setVgap(20);
			
			Label aLabel = getLabel("Enter choice A: ", 18);
			Label bLabel = getLabel("Enter choice B: ", 18);
			Label cLabel = getLabel("Enter choice C: ", 18);
			Label dLabel = getLabel("Enter choice D: ", 18);
			
			TextField aTF = getTextField(40, 30);
			TextField bTF = getTextField(40, 30);
			TextField cTF = getTextField(40, 30);
			TextField dTF = getTextField(40, 30);
			
			// set the text of the text fields to the old choices
			aTF.setText(selectedQ.getChoice(1));
			bTF.setText(selectedQ.getChoice(2));
			cTF.setText(selectedQ.getChoice(3));
			dTF.setText(selectedQ.getChoice(4));
			
			gp.addRow(0, aLabel, aTF);
			gp.addRow(1, bLabel, bTF);
			gp.addRow(2, cLabel, cTF);
			gp.addRow(3, dLabel, dTF);
			
			HBox correctAnsBox = new HBox(20);
			correctAnsBox.setAlignment(Pos.CENTER);
			
			Label correctAnsLabel = getLabel("Correct answer: ", 15);
			
			RadioButton aRB = new RadioButton("A");
			RadioButton bRB = new RadioButton("B");
			RadioButton cRB = new RadioButton("C");
			RadioButton dRB = new RadioButton("D");
			ToggleGroup tg = new ToggleGroup();
			aRB.setToggleGroup(tg);
			bRB.setToggleGroup(tg);
			cRB.setToggleGroup(tg);
			dRB.setToggleGroup(tg);
			
			// make the old answer selected
			switch (selectedQ.getCorrectAnswer()) {
			case "A": aRB.setSelected(true); break;
			case "B": bRB.setSelected(true); break;
			case "C": cRB.setSelected(true); break;
			case "D": dRB.setSelected(true); break;
			}
			
			HBox choicesBox = new HBox(30);
			choicesBox.getChildren().addAll(aRB, bRB, cRB, dRB);
			
			correctAnsBox.getChildren().addAll(correctAnsLabel, choicesBox);
			
			vb.getChildren().addAll(questionBox, gp, correctAnsBox);
			
			HBox buttonsBox = new HBox(80);
			buttonsBox.setPadding(new Insets(20));
			buttonsBox.setAlignment(Pos.CENTER);
			
			Button saveBt = getButton("Save");
			Button cancelBt = getButton("Cancel");
			
			buttonsBox.getChildren().addAll(cancelBt, saveBt);
			
			Label TitleLabel = new Label("Edit question " + (questionIndex + 1));
			TitleLabel.setFont(new Font("Times New Roman", 32));
			
			// root pane to hold everything
			BorderPane editPane = new BorderPane(vb);
			editPane.setPadding(new Insets(30));
			editPane.setBottom(buttonsBox);
			editPane.setTop(TitleLabel);
			BorderPane.setAlignment(TitleLabel, Pos.CENTER);
			
			scene.setRoot(editPane);
			
			saveBt.setOnAction(e -> {
				// get the correct answer
				String selectedChoice = ((RadioButton)tg.getSelectedToggle()).getText();
				
				// modify the data fields of the question
				selectedQ.setText(questionTF.getText());
				selectedQ.setCorrectAnswer(selectedChoice);
				selectedQ.setChoices(aTF.getText(), bTF.getText(), cTF.getText(), dTF.getText());
				
				// save the new question to the array
				questionArray.editQuestion(selectedQ, questionIndex);
				
				// set and view the text of questions in the List view
				lv.setItems(questionArray.getListOfText());
				
				// set the root of the scene to the original root
				scene.setRoot(root);
				
			});
			
			cancelBt.setOnAction(e -> {
				// just go back
				scene.setRoot(root);
			});
			
		}
	}
	
	// class (inner class) to hold the array of questions and provide the desired methods
	class QuestionArray {
		
		// the array of Question objects
		private ArrayList<Question> arrayOfQ = new ArrayList<>();
		
		// the array of questions text
		private ArrayList<String> arrayOfText = new ArrayList<>();
		
		// getter method for arrayOfQ data field
		public ArrayList<Question> getArrayOfQ() {
			return arrayOfQ;
		}
		
		// setter method for arrayOfQ data field
		public void setArrayOfQ(ArrayList<Question> arrayOfQ) {
			this.arrayOfQ = arrayOfQ;
			// and also set the text array
			generateArrayOfText();
		}
		
		// getter method for arrayOfText data field
		public ArrayList<String> getArrayOfText() {
			return arrayOfText;
		}
		
		// method to return the texts of questions in an ObservableList object
		public ObservableList<String> getListOfText() {
			 return FXCollections.observableArrayList(arrayOfText);
		}
		
		// generate the text array from the current array of questions
		public void generateArrayOfText() {
			for (int i = 0; i < arrayOfQ.size(); i++) {
				arrayOfText.add(arrayOfQ.get(i).getText());
			}
		}
		
		// method to add a new question
		public void addQuestion(Question q) {
			arrayOfQ.add(q);
			arrayOfText.add(q.getText());	
		}
		
		// method to delete a question
		public void deleteQuestion(Question q) {
			arrayOfQ.remove(q);
			arrayOfText.remove(q.getText());
		}
		
		// method to change a question
		public void editQuestion(Question q, int index) {
			arrayOfQ.set(index, q);
			arrayOfText.set(index, q.getText());
		}
		
		// method to return the selected question from the list view
		public Question getSelectedQuestion() {
			int questionIndex = lv.getSelectionModel().getSelectedIndex();
			return arrayOfQ.get(questionIndex);
		}
		
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}

}

// class to represent a question
class Question implements Serializable {
	
	// the text of the question
	private String text;
	
	// the correct answer as letter (A, B, C, D)
	private String correctAnswer;
	
	// array to hold the text of the choices
	private String[] choices;
	
	// constructor
	public Question(String text, String correctAnswer) {
		this.text = text;
		this.correctAnswer = correctAnswer;
	}
	
	// no-arg constructor
	public Question() {
		
	}
	
	// getter and setter methods for text data field:
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	// getter and setter methods for correctAnswer data field:
	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
	
	// getter and setter methods for choices data field:
	public String[] getChoices() {
		return choices;
	}

	public void setChoices(String... choices) {
		this.choices = choices;
	}
	
	// method to return a specific choice
	public String getChoice(int i) {
		
		String choice = "";
		
		switch (i) {
		case 1: choice = choices[0]; break;
		case 2: choice = choices[1]; break;
		case 3: choice = choices[2]; break;
		case 4: choice = choices[3]; break;
		}
		
		return choice;
	}
}
