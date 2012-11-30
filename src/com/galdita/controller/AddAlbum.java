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
/**
 * Kelas ini merupakan kelas yang berfungsi untuk
 * menampilkan activity yang digunakan oleh user untuk
 * menambahkan album baru di peta
 * 
 * @author Rezky Ramadiansyah
 *
 */
public class AddAlbum extends Activity {
	//Objek DBGaldita untuk akses database
	DBGaldita db = new DBGaldita(this);
	// Penampung sementara nama album
	private String albumName = "Default";

	// Penampung sementara deskripsi album
	private String albumDesc = "Default";
	
	// Penampung sementara marker album
	static String albumMarker = "Default";
	
	// Penampung sementara cover album
	static String albumCover = "";

	// Penampung lokasi long press
	private String location;
	
	//Objek editable text field 1
	EditText e1;
	EditText zz;
	
	//Objek editable text field 2
	EditText e2;
	
	//Tombol ok
	Button ok;
	
	//Tombol cancel
	Button cancel;
	
	//Tombol Cover
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
		location = b.getString("location");
		
		setContentView(R.layout.add_album_dialog);
		
		//inisialisasi spinner marker
		Spinner spinner = (Spinner) findViewById(R.id.markerspinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.markers_array, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new GalditaOnItemSelectedListener());
		
		e1 = (EditText) findViewById(R.id.album_name);
		
		e2 = (EditText) findViewById(R.id.album_desc);
		
		ok = (Button) findViewById(R.id.ok_button);
		
		cancel = (Button) findViewById(R.id.cancel_button);
		
		cover = (ImageView) findViewById(R.id.selectCover);
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		if(albumCover.equals(""))
			cover.setImageResource(R.drawable.cover);
		else {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inSampleSize = 10;
			Bitmap bm = ThumbnailUtils.extractThumbnail(
					BitmapFactory.decodeFile(albumCover, opt), 96,
					96);
			cover.setImageBitmap(bm);
		}
	}
	
	/**
	 * Method ini dipanggil ketika tombol OK
	 * ditekan oleh user dalam form menambah
	 * album
	 * 
	 * @param view
	 */
	public void addAlbum(View view) {
		albumName = e1.getText().toString();
		albumDesc = e2.getText().toString();
		
		albumName = albumName.trim();
		
		if(albumName.equals("")){
			System.out.println("Null or whitespaces only");
			Toast.makeText(AddAlbum.this, "Warning: Album name is null or contain whitespaces only!"
					, Toast.LENGTH_LONG).show();
		}
		
		db.open();
		db.insertAlbum(albumName, albumDesc,
				location, albumCover, albumMarker);
		album.setAlbumName(albumName);
		album.setAlbumDescription(albumDesc);
		album.setAlbumLocation(location);
		album.setCover(albumCover);
		album.setMarker(albumMarker);

		Cursor cursor = db.getAlbumFromMap(albumName,
				location);
		cursor.moveToFirst();
		album.setAlbumID(db.getAlbumID(cursor));
		cursor.close();
		Cursor cursor2 = db.getSingleItem(0, album.getCover());
		if(cursor2.getCount() > 0){
			cursor2.moveToFirst();
			int itemId = db.getItemID(cursor2);
			db.updateItemAlbumID(itemId, album.getAlbumID());
		}
		cursor2.close();
		db.close();
		albumCover = "";//dinull kan lagi
		finish();
	}
	
	/**
	 * Method ini dipanggil ketika
	 * tombol "Cancel" ketika user
	 * tidak jadi menambahkan album
	 * 
	 * @param view
	 */
	public void cancelAdd(View view) {
		albumCover = "";
		finish();
	}
	
	/**
	 * Method untuk meng-eset cover album
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
	    	albumMarker = "Default";
	    }
	}

		
}
