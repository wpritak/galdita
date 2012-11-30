package com.galdita.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.maps.GeoPoint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.galdita.R;
import com.galdita.model.Album;

/**
 * Kelas ini digunakan untuk menampilkan activity yang berisi pilihan menu edit
 * album atau delete album
 * 
 * @author Rezky Ramadiansyah
 * 
 */
public class MenuEdit extends Activity {
	// Objek DBGaldita untuk akses database
	DBGaldita db = new DBGaldita(this);

	// Penampung album id
	private int albumid;

	// Penampung album id
	private int albumindex;

	// Penampung sementara nama album
	private String albumName = "Default";

	// Penampung sementara lokasi album
	private String albumLocation;

	// Penampung sementara lokasi album
	private GeoPoint pointLocation;

	// Tombol edit
	Button edit;

	// Tombol delete
	Button delete;

	// Objek album
	Album album = new Album();

	MenuEdit menuEdit = this;
	/**
	 * Method ini akan dipanggil pertama kali saat activity dijalankan.
	 * 
	 * @param Bundle
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		albumid = b.getInt("albumid");
		albumindex = b.getInt("index");

		// mendapatkan informasi mengenai album
		db.open();
		Cursor albumcursor = db.getAlbumByID(albumid);
		albumcursor.moveToFirst();
		albumName = db.getAlbumName(albumcursor);
		albumLocation = db.getAlbumLocation(albumcursor);
		albumcursor.close();
		db.close();

		String[] latLong = albumLocation.split(",");

		pointLocation = new GeoPoint(Integer.parseInt(latLong[0]),
				Integer.parseInt(latLong[1]));
		setContentView(R.layout.menu_edit);

		edit = (Button) findViewById(R.id.edit_button);

		delete = (Button) findViewById(R.id.delete_button);

	}

	/**
	 * Method ini digunakan untuk memanggil activity Edit Album. Dipanggil
	 * ketika tombol Edit ditekan user
	 * 
	 * @param view
	 */
	public void editAlbum(View view) {
		Bundle b = new Bundle();
		b.putInt("albumid", albumid);
		b.putInt("index", albumindex);
		Intent intent = new Intent(this, EditAlbum.class);
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}

	/**
	 * Method ini digunakan untuk menghapus album yang sedang dipilih oleh user
	 * 
	 * @param view
	 */
	public void deleteAlbum(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to delete?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// DELETE
								new Runnable() {
									public void run() {
										db.open();
										boolean b = db.deleteAlbum(albumName,
												pointLocation.toString());
										db.close();
										Intent intent = new Intent(menuEdit, Dashboard.class);
										//intent.putExtras(b);
										startActivity(intent);
										finish();
										//finish();
									}
								}.run();
								Log.v(ACTIVITY_SERVICE, "Panggil Yang Delete");

							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
