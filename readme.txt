Readme
Profile.java API
Phillip Curtsmith
Autum 2021

Provides a facility to save, load, or clear fields from JavaFX GUI through use of three (like-named) static methods. Note that the Profile API supports TextArea, TextField, ChoiceBox, RadioButton, and CheckBox JavaFX Controls as of November 2021. Additional Controls can be added as necessary. Complex JavaFX Controls such as TableView or TreeView are currently not supported and it may be difficult or impossible to add support for these Controls.

How to use the API for future development

Profile API uses Annotations to distinguish fields of different modules from one another.

Each field associated with a single Profile is declared in a controller class. To enable Profile support, an instance variable of the appropriate type must also be defined for each field. For example, if the field is a TextField, the instance variable type may be String, int, double, or similar. In general, if the value that a user enters into the GUI should be parsed to an int, that instance variable should be an int. This instance variable name MUST have the same name as the JavaFX field name, with the addition of “_var”. In addition, each instance variable must have a setter method which accepts the correct type as argument. This setter method MUST have the same name as the JavaFX control. 

For a given Control, the following satisfies the above description:

1	@FXML public TextField percentOverfill;
2	@UniformityTestProfile public double percentOverfill_var;
3	public void percentOverfill( String s ) { percentOverfill.setText(s); }

The @FXML Annotation from line 1 is standard development practice for JavaFX GUIs with FXML code programmed with SceneBuilder. Line 2 features a custom Annotation associated with a Profile. Line 3 is a setter method for this value. Note also that the body of the method calls the field directly to set the value s. Each control for a given Profile must have these three elements associated with a custom Annotation of your choosing.

After fields are configured, one must add a call to Profile#save. This is done through a canned method SaveDialogSample#openSaveProfileDialog in this repository. This is a general-purpose method which opens a dialog to facilitate the saving process. It is strongly encouraged that you incorporate at least one method per Profile to validate inputs before attempting to save those field values to file.

The following method is a suitable call to Controller#openSaveProfileDialog. This method must be linked to a Button or other Control as needed:

1	@FXML
2	private void saveUniformityProfile( ) {
3	     if ( !validUniformityTestFields() ) {
4		     printUniformityTestStatus( "Invalid fields!" );
5		     return;
6	     }
7	     openSaveProfileDialog( UniformityTestProfile.class );
8	     printUniformityTestStatus( "" ); // clear any error messages
9	}


Note that Controller#openSaveProfileDialog accepts the custom Annotation as argument. This allows Profile to distinguish between fields of various Profiles. 

Loading values into the GUI fields of a given Profile can be accomplished with a call to Profile#load. The following is a method used for this purpose:

1	@FXML
2	private void loadUniformityProfile() {
3		FileChooser fileChooser = new FileChooser();
4		fileChooser.setTitle( "Select a Uniformity Test Profile." );
5		fileChooser.getExtensionFilters().add( new ExtensionFilter("TextFiles","*.txt") );
6		File file = fileChooser.showOpenDialog( Prometheus.getStage() );
7		if ( file != null ) {
8			try {
9				Profile.load( getController(), file.toString() );
10			} catch (...) {
11				e.printStackTrace();
12			}
13		}
14	}

Note that the call to Profile#load does not require any Annotation argument. When a user selects a Profile from file, values will populate into fields specified in that Profile. As before, the method above must be linked to a Button or other Control.

Clearing values from fields is very simple with a call to Profile#clear, which does require that the custom Profile Annotation be specified:

1	@FXML
2	private void clearUniformityProfile() {
3		Profile.clear( this, UniformityTestProfile.class );
4	}

In general, the Profile API allows for simple adding and removing of fields from any GUI module without requiring that the saving and loading code be entirely re-written. These functions can be quickly added to fresh GUI fields using the three static methods in Profile. 
