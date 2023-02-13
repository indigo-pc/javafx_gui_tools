package com.ioi.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;

/**
 * Profile objects save a set of Field values from a JavaFX controller class to file with {@link #save(Controller, Object, String, String)}. 
 * These values can be loaded back to the JavaFX GUI Fields using {@link #load(Controller, String)}.
 * The GUI Fields can also be cleared using {@link #clear(Controller, Object)}.
 * GUI fields must be distinguished using an Annotation. That is, all instance variables associated with a Field which possess a given Annotation will be saved as part of one stand-alone profile.
 * 
 * HOW TO USE THIS API: 
 * Any Field from a JavaFX controller class can be saved and loaded. 
 * Each Field must have a descriptive name. Each field must also have a related instance variable, to where that field value is saved in that controller class instance. 
 * The instance variable and related Field MUST follow this naming convention: someGuiField and someGuiField_var, where the former is the Field and the latter is the instance variable to where the Field value is stored.
 * Each Field must also have a setter method which MUST have EXACTLY the same name as the Field itself. That setter method must accept as argument that correct data type for setting the Field.
 * For example, TextArea and TextField setters must accept String arguments and the body of the setter must invoke #setText(). 
 * 
 * @author Phillip Curtsmith
 *
 */
public class Profile {
	
	private final static String LOAD_SEPARATOR_CHAR = "\\?";
	private final static String SAVE_SEPARATOR_CHAR = "?";
	private final static String GENERAL_ERROR = "Invalid Profile data or format.";

	/**
	 * Method for saving Profiles to file. All Fields with a given annotation will be saved to that Profile.
	 * 
	 * @param c : An instance of a controller class
	 * @param a : An annotation associated with a single profile. All Fields with the given annotation will be printed to a single Profile.
	 * @param filePath : String representation of file path, to where the Profile is saved.
	 * @param fileName : String representation of file name for that Profile. 
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * 
	 * @author Phillip Curtsmith
	 */
	public static void save( Controller c, Object a, String filePath, String fileName ) throws IOException, IllegalArgumentException, IllegalAccessException {
		File dataFile = new File( filePath + fileName + ".txt" );
		FileWriter fw = new FileWriter( dataFile );
		BufferedWriter writer = new BufferedWriter( fw );
		Field[] f = c.getClass().getDeclaredFields();
		
		// Locate fields from Controller.java with correct Annotation, a
		for ( int i = 0; i < f.length; i++ ) {
			if ( f[i].isAnnotationPresent( (Class<? extends Annotation>) a ) ) {
				//System.out.println( f[i].getName() + SAVE_SEPARATOR_CHAR + f[i].get(c) );
				writer.write( f[i].getName() + SAVE_SEPARATOR_CHAR + f[i].get(c) + "\n" );
			}
		}
		writer.close();
	}
	
	/**
	 * Method for loading Profiles into JavaFX GUI Fields.
	 * 
	 * @param c : An instance of a controller class
	 * @param filePath : String representation of file path, to where the Profile is saved.
	 * 
	 * @throws IOException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * 
	 * @author Phillip Curtsmith
	 */
	public static void load( Controller c, String filePath ) throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		File dataFile = new File( filePath );
		Reader fr = new FileReader( dataFile );
		BufferedReader reader = new BufferedReader( fr );
		
		Field f;
		String line;
		String[] data;
		String field;
		String value;
		Method m;
		while( ( line = reader.readLine() ) != null ) { 
			
			try {
				data = line.split( LOAD_SEPARATOR_CHAR );
				field = data[0].split( "_" )[0];
				value = data[1];
				f = c.getClass().getField( field );	
			} catch ( Exception e ) {
				System.err.println( GENERAL_ERROR );
				break;
			}
			
			// TEXT-FIELD, TEXT-AREA, CHOICE-BOX
			if ( f.getType() == TextField.class || f.getType() == TextArea.class || f.getType() == ChoiceBox.class ) {
				try {
					m = c.getClass().getMethod( field, String.class );
					m.invoke( c, value );
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
					System.err.println( GENERAL_ERROR );
				}
			// RADIO-BUTTON, CHECK-BOX
			} else if ( f.getType() == RadioButton.class || f.getType() == CheckBox.class ) {
				try {
					m = c.getClass().getMethod( field, boolean.class );
					if ( value.equals( "true" ) ) {
						m.invoke( c, true );
					} else {
						m.invoke( c, false );
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
					System.err.println( GENERAL_ERROR );
				}
			// Other Field Types. Can be added later if needed.
			} else {
				throw new UnsupportedOperationException( "No current implementation for JavaFX entry Field type: " + f.getType().getSimpleName() + ". Consult your nearest engineer." );
			}
			
		}
		reader.close();
	}
	
	/**
	 * Method for clearing all GUI fields associated with a specific Profile.
	 * 
	 * @param c : An instance of a controller class
	 * @param a : An annotation associated with a single profile. The values at the Fields identified with this Annotation will be cleared from the GUI.
	 * 
	 * @author Phillip Curtsmith
	 * 
	 */
	public static void clear( Controller c, Object a ) {
		
		Field[] f = c.getClass().getDeclaredFields();
		String method;
		Method m;
		
		for ( int i = 0; i < f.length; i++ ) {
			if ( f[i].isAnnotationPresent( (Class<? extends Annotation>) a ) ) {
				method = f[i].getName().split( "_" )[0];
				// Strings
				if ( f[i].getType() == String.class ) {
					try {
						m = c.getClass().getMethod( method, String.class );
						m.invoke( c, "" );
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
				// Other primitives except boolean
				} else if ( f[i].getType().isPrimitive() & ! (f[i].getType() == boolean.class) ) {
					try {
						m = c.getClass().getMethod( method, String.class );
						m.invoke( c, "" );
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
				// Boolean
				} else if ( f[i].getType() == boolean.class ) {
					try {
						m = c.getClass().getMethod( method, boolean.class );
						m.invoke( c, false );
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
				// All other possible Field Types
				} else {
					Field unsupportedFieldType = null;
					try {
						unsupportedFieldType = c.getClass().getField( method );
					} catch ( NoSuchFieldException | SecurityException e) {
						e.printStackTrace();
					}
					throw new UnsupportedOperationException( "No current implementation for JavaFX entry Field type: " + unsupportedFieldType.getName() + ". Consult your nearest engineer." );
				}
			}
		}
	}
}
