package com.ioi.gui;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

public class SaveDialogSample {
	
	/**
	 * General method to configure and launch a dialog for saving profiles (GUI field values) to file.
	 * @param profileAnnotation : annotation class to identify which fields to save
	 */
	private void openSaveProfileDialog( Object profileAnnotation ) {
		Dialog< Object > dialog = new Dialog<>();
		dialog.setTitle( "Save New Profile" );
		dialog.setHeaderText( "Enter a unique profile name and destination directory." );
		Label label1 = new Label( "Profile Name: " ); Label label2 = new Label( "Save Directory: " ); Label label3 = new Label( ".txt" ); TextField text1 = new TextField(); TextField text2 = new TextField(); Button buttonDirectory = new Button( "..." ); TextField error = new TextField(); error.setEditable( false ); error.setStyle( "-fx-text-fill: red; -fx-background-color: transparent;" ); text2.setEditable( false );
		GridPane grid = new GridPane(); grid.add(label1, 1, 1); grid.add(text1, 2, 1); grid.add(label3, 3, 1); grid.add(label2, 1, 2); grid.add(text2, 2, 2); grid.add( buttonDirectory, 3, 2); grid.add( error, 0, 4); grid.setMargin( text1, new Insets(5,5,5,5) ); grid.setMargin( text2, new Insets(5,5,0,5) ); GridPane.setColumnSpan( error, GridPane.REMAINING);
		dialog.getDialogPane().setContent(grid);
		ButtonType buttonTypeOk = new ButtonType( "Save Profile", ButtonData.OK_DONE );
		ButtonType buttonTypeCancel = new ButtonType( "Cancel", ButtonData.CANCEL_CLOSE );
		dialog.getDialogPane().getButtonTypes().add( buttonTypeOk );
		dialog.getDialogPane().getButtonTypes().add( buttonTypeCancel );
    	buttonDirectory.setOnAction(new EventHandler<ActionEvent>() {
    	    public void handle( ActionEvent e ) {
    	    	DirectoryChooser directoryChooser = new DirectoryChooser();
    	    	File selectedDirectory = directoryChooser.showDialog( dialog.getOwner() );
    	    	if ( selectedDirectory != null ) { 
    	    		try {
						text2.setText( selectedDirectory.getCanonicalPath() + File.separator );
					} catch (IOException e1) { e1.printStackTrace(); }
    	    	}
    	    }
    	});
	    	final Button okButton = (Button) dialog.getDialogPane().lookupButton( buttonTypeOk );
			okButton.addEventFilter(ActionEvent.ACTION, ae -> {
				if ( fileNameContainsIllegalCharacter( text1.getText() ) ) {
					error.setText( "Profile name contains an illegal character." );
					ae.consume();
				}
				if ( text2.getText().isBlank() ) {
					error.setText( "Populate destination directory for your profile." );
					ae.consume();
				}
				try {
					Profile.save( yourControllerClass, profileAnnotation, text2.getText(), text1.getText() );
				} catch (IllegalArgumentException | IllegalAccessException | IOException e1) { e1.printStackTrace(); }
			});
		dialog.showAndWait();
	}
	
}
