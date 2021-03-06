package skinConditionsTracker.View;

import skinConditionsTracker.Controller.DatabaseAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * Skin Condition Log
 * Copyright (C) 2012 Andrea Budac, Kurtis Morin, Christian Jukna
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/** 
 * DisplayPhotoView<br>
 * This view is for displaying the photo that is selected.
 * This activity is started from PhotoLayoutView.
 * This activity returns no data.
 * This activity is given the bitmap, folder name and
 * time stamp by the activity that calls it.
 * 
 * @author Andrea Budac: abudac
 * @author Christian Jukna: jukna
 * @author Kurtis Morin: kmorin1<br><br>
 * 
 * April 06, 2012
 * 
 */

public class DisplayPhotoView extends Activity {
	private ImageView imagePreview;
	private TextView photoGroupName;
	private TextView photoTimeStamp;
	private TextView photoTag;
	private TextView photoAnnotate;
	private Button addAnnotate;
	private Button addTag;

	private DatabaseAdapter dbHelper;
	private Cursor cursor;

	private long rowId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photopreview); 

		imagePreview = (ImageView) findViewById(R.id.image2);
		photoGroupName = (TextView) findViewById(R.id.photogroupname);
		photoTimeStamp = (TextView) findViewById(R.id.phototimestamp);
		photoTag = (TextView) findViewById(R.id.phototag);
		photoAnnotate = (TextView) findViewById(R.id.photoannotate);
		addAnnotate = (Button) findViewById(R.id.new_annotation);
		addTag = (Button) findViewById(R.id.new_tag);

		dbHelper = new DatabaseAdapter(this);

		rowId = getIntent().getLongExtra("rowId", 0);

		final Dialog addAnnotationDialog = addAnnotateDialog(); 
		final Dialog addTagDialog = addTagDialog();

		/** 
		 * Allow user to add annotation to currently displayed photo
		 */
		addAnnotate.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v) 
			{
				addAnnotationDialog.show();
			}
		});

		/**
		 *  Allow user to add tag to currently displayed photo
		 */
		addTag.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v) 
			{
				addTagDialog.show();
			}
		});
	}

	/**
	 * Creates a database where it gets a bitmap from the rowID
	 * passed into the activity and displays it in the imageview.
	 * The tag, date, folder, and annotation are also retrieved and are
	 * displayed.
	 */
	@Override
	protected void onStart() {
		super.onStart();

		dbHelper.open();
		cursor = dbHelper.fetchPhoto(rowId);

		//Sets the image view to the enlarged bitmap of the photo that
		//was clicked.        
		byte[] photo = cursor.getBlob(cursor.getColumnIndex(DatabaseAdapter.PHOTO));
		Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
		imagePreview.setImageBitmap(bitmap);

		//Set the folder name at top of screen to correct folder.
		String folder = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.FOLDER));
		photoGroupName.setText(folder);

		//Set the time stamp at bottom of screen to correct time stamp.
		String time = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.DATE));
		photoTimeStamp.setText(time);

		//Set the tag at bottom of screen to correct tag.
		String tag = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.TAG));
		photoTag.setText(tag);

		//Set the annotation at bottom of screen to correct annotation.
		String annotate = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.ANNOTATE));
		photoAnnotate.setText(annotate);
	}

	/**
	 * Close the database and cursor on pause to prevent errors. 
	 */
	@Override
	protected void onPause() {
		super.onPause();
		cursor.close();
		dbHelper.close();
	}

	/**
	 * Creates the add annotation dialog when called.
	 * When it is shown a dialog appears asking the
	 * user to input an annotation. They can then confirm or cancel.
	 * The function returns the created dialog.
	 * 
	 * @return addAnnotateDialog.create()
	 */
	private Dialog addAnnotateDialog() {
		final EditText input = new EditText(DisplayPhotoView.this);
		Builder addAnnotateDialog = new AlertDialog.Builder(DisplayPhotoView.this);
		// do the work to define the addDialog
		addAnnotateDialog.setView(input);
		addAnnotateDialog.setTitle("Adding a new annotation...");
		addAnnotateDialog.setIcon(R.drawable.dialog_add);
		addAnnotateDialog.setMessage("Please specify the annotation to add.");
		// Setting Positive "Add folder" Button
		addAnnotateDialog.setPositiveButton("Add Annotation", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				//actions to complete when clicking Add folder
				String annotation = input.getText().toString();
				
				ToastCreator toast = new ToastCreator(DisplayPhotoView.this);
				
				if(annotation.equals("")){
					toast.toaster("Please insert text for an annotation.");
				}
				else if (annotation.contains("'")){
					toast.toaster("Please do not include single quotes in annotation.");
				}
				else if (annotation.length() > 256){
					toast.toaster("Please make the annotation shorter.");
				}
				else{
					dbHelper.addAnnotationToPhoto(annotation, rowId);

					photoAnnotate.setText(annotation);
				}
				
				input.setText("");
			}
		});

		// Setting Negative "NO" Button
		addAnnotateDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//actions to complete when clicking cancel
			}
		});
		return addAnnotateDialog.create();
	}

	/**
	 * Creates the add tag dialog when called.
	 * When it is shown a dialog appears asking the
	 * user to input a tag. They can then confirm or cancel.
	 * The function returns the created dialog.
	 * 
	 * @return addTagDialog.create()
	 */
	private Dialog addTagDialog() {
		final EditText input = new EditText(DisplayPhotoView.this);
		Builder addTagDialog = new AlertDialog.Builder(this);
		// do the work to define the addDialog
		addTagDialog.setView(input);
		addTagDialog.setTitle("Adding a new tag...");
		addTagDialog.setIcon(R.drawable.dialog_add);
		addTagDialog.setMessage("Please specify the tag to add.");
		// Setting Positive "Add tag" Button
		addTagDialog.setPositiveButton("Add tag", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				//actions to complete when clicking Add folder
				String tag = input.getText().toString();
				
				ToastCreator toast = new ToastCreator(DisplayPhotoView.this);
				
				if(tag.equals("")){
					toast.toaster("Please insert text for a tag.");
				}
				else if (tag.contains("'")){
					toast.toaster("Please do not include single quotes in tag.");
				}
				else if (tag.length() > 30){
					toast.toaster("Please make the tag shorter.");
				}
				else if (tag.contains("\n")){
					toast.toaster("Please make the tag one line.");
				}
				else if (tag.equals("Default Folder Photos")){
					toast.toaster("Don't get smart with me.");
				}
				else{
					dbHelper.addTagToPhoto(tag, rowId);

					photoTag.setText(tag);
				}
				input.setText("");
			}
		});

		// Setting Negative "NO" Button
		addTagDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//actions to complete when clicking cancel
			}
		});
		return addTagDialog.create();
	}
}
