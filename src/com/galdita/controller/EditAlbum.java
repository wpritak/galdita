package com.galdita.controller;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.galdita.R;
import com.galdita.model.Album;
import com.google.android.maps.GeoPoint;

/**
 * Kelas ini merupakan kelas yang berfungsi untuk
 * menampilkan activity yang digunakan oleh user untuk
 * mengubah informasi, cover, dan marker suatu album 
 * yang telah ada di peta
 * 
 * @author Rezky Ramadiansyah
 *
 */
public class EditAlbum extends Activity {
	//Objek DBGaldita untuk akses database
	DBGaldita db = new DBGaldita(this);
	
	// Penampung sementara nama album
	private String albumName = "Default";

	// Penampung sementara deskripsi album
	private String albumDesc = "Default";
	
	// Penampung sementara lokasi album
	private String albumLocation;
	
	// Penampung sementara lokasi album
	private GeoPoint pointLocation;
	
	// Penampung sementara marker album
	static String albumMarker = "Default";
	
	// Penampung cover yang sekarang album
	private String CurrentAlbumMarker;
	
	// Penampung cover yang sekarang album
	private String CurrentAlbumCover;

	// Penampung lokasi long press
	private String location;
	
	// Penampung album id
	private int albumid;
	
	// Penampung album id
	private int albumindex;
	
	//Objek editable text field 1
	EditText e1;
	
	//Objek editable text field 2
	EditText e2;
	
	//Tombol ok
	Button ok;
	
	//Tombol cancel
	Button cancel;
	
	//Tombol untuk cover
	ImageView cover;
	
	//Objek album
	Album album = new Album();
	
	
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
		
		//Mengambil informasi terkait album yang akan
		//di-edit dari database
		db.open();
		Cursor albumcursor = db.getAlbumByID(albumid); 
		albumcursor.moveToFirst();
		albumName = db.getAlbumName(albumcursor);
		albumDesc = db.getAlbumDescription(albumcursor);
		albumLocation = db.getAlbumLocation(albumcursor);
		CurrentAlbumMarker = db.getAlbumMarker(albumcursor);
		CurrentAlbumCover = db.getAlbumCover(albumcursor);
		albumcursor.close();
		db.close();
		
		String[] latLong = albumLocation.split(",");
		
		AddAlbum.albumCover = CurrentAlbumCover;
		
		pointLocation = new GeoPoint(Integer.parseInt(latLong[0]),
				Integer.parseInt(latLong[1]));
		
		//Menentukan marker yang sedang digunakan sekarang
		int markerpos = 0;
		
		for(int i = 0; i < Dashboard.markers.length; i++){
			if(CurrentAlbumMarker.equals(Dashboard.markers[i]))
				markerpos = i;
		}
		
		setContentView(R.layout.edit_album_dialog);
		
		//Spinner berisi pilihan marker
		Spinner spinner = (Spinner) findViewById(R.id.markerspinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.markers_array, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new GalditaOnItemSelectedListener());
	    spinner.setSelection(markerpos);
		
		e1 = (EditText) findViewById(R.id.album_name);
		e1.setText(albumName);
		
		e2 = (EditText) findViewById(R.id.album_desc);
		e2.setText(albumDesc);
		
		ok = (Button) findViewById(R.id.ok_button);
		
		cancel = (Button) findViewById(R.id.cancel_button); 
		
		cover = (ImageView) findViewById(R.id.changeCover);
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		if(AddAlbum.albumCover.equals(""))
			cover.setImageResource(R.drawable.cover);
		else {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inSampleSize = 10;
			Bitmap bm = ThumbnailUtils.extractThumbnail(
					BitmapFactory.decodeFile(AddAlbum.albumCover, opt), 96,
					96);
			cover.setImageBitmap(bm);
		}
	}
	
	/**
	 * Method ini dipanggil ketika user
	 * hendak menyimpan informasi, marker, dan cover
	 * album yang sudah di-edit
	 * 
	 * @param view
	 */
	public void editAlbum(View view) {
		albumName = e1.getText().toString();
		albumDesc = e2.getText().toString();
		
		albumName = albumName.trim();
		
		if(albumName.equals("")){
			System.out.println("Null or whitespaces only");
			Toast.makeText(EditAlbum.this, "Warning: Album name is null or contain whitespaces only!"
					, Toast.LENGTH_LONG).show();
		}
		
		db.open();
		db.updateAlbumName(albumid, albumName);
		db.updateAlbumDescription(albumid, albumDesc);
		if(!CurrentAlbumCover.equals(AddAlbum.albumCover) && !AddAlbum.albumCover.equals("")){
			db.updateAlbumCover(albumid, AddAlbum.albumCover);
			CurrentAlbumCover = AddAlbum.albumCover;
		}
		if(!CurrentAlbumMarker.equals(albumMarker)){
			db.updateAlbumMarker(albumid, albumMarker);
			CurrentAlbumMarker = albumMarker;
		}
		
		album.setAlbumID(albumid);
		album.setAlbumName(albumName);
		album.setAlbumDescription(albumDesc);
		album.setAlbumLocation(location);
		album.setCover(CurrentAlbumCover);
		album.setMarker(CurrentAlbumMarker);

		Cursor cursor2 = db.getSingleItem(0, album.getCover());
		if(cursor2.getCount() > 0){
			cursor2.moveToFirst();
			int itemId = db.getItemID(cursor2);
			db.updateItemAlbumID(itemId, album.getAlbumID());
		}
		cursor2.close();
		db.close();
		AddAlbum.albumCover = "";//dinull kan lagi
		finish();
		
	}
	
	/**
	 * Method ini dipanggil ketika user
	 * membatalkan pengubahan informasi
	 * album
	 * 
	 * @param view
	 */
	public void cancelEdit(View view) {
		AddAlbum.albumCover = "";
		finish();
	}
	
	
	/**
	 * Method untuk mengeset cover album
	 * @param view
	 */
	public void setCover(View view) {
		Log.i("Click", "cover");
		Intent intent = new Intent(this, ItemChooser.class);
		Bundle b = new Bundle();
		b.putInt("albumID", album.getAlbumID());
		b.putInt("mode", ItemChooser.UBAH_COVER);
		intent.putExtras(b);
		startActivity(intent);
	}
	
	//Listener untuk mendeteksi item yang dipilih di spinner marker
	public class GalditaOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	    	
	        albumMarker = parent.getItemAtPosition(pos).toString();
	    }

	    public void onNothingSelected(AdapterView parent) {
	    	albumMarker = CurrentAlbumMarker;
	    }
	}
	
}
