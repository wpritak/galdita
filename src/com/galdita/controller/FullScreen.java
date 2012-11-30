package com.galdita.controller;

import java.io.File;

import com.galdita.R;
import com.galdita.model.Item;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Class Fullscreen ini akan menampilkan image atau foto secara full screen.
 * Tampilan image secara full screen dapat digeser atau di-fling ke kanan atau
 * ke kiri.
 * 
 * @author Primaningtyas Sukawati
 * 
 */
public class FullScreen extends Activity {
	// variabel bertipe String yang merepresentasikan path image
	// yang ditampilkan
	String path = null;

	// variabel bertipe Bundle yang berguna untuk passing data dari acivity lain
	Bundle b = null;

	// variabel bertipe GestureDetector yang mendeteksi adanya gerakan
	private GestureDetector gestureDetector;

	// suatu konstanta sebagai ukuran gerakan ke kanan atau ke kiri
	private static final int LARGE_MOVE = 60;

	// posisi image pada array of string yang berisikan paths image
	int position;

	// ID album yang sedang dilihat
	int albumid;

	// objek database, digunakan ketika mengakses database aplikasi
	DBGaldita db = new DBGaldita(this);

	// ID item atau image yang sedang dilihat saat ini
	int itemid;

	// posisi image yang sedang dilihat dan di-fling
	int posisiFling;

	FullScreen fs = this;

	// Objek Item
	Item item = new Item();

	// array of string yang berisikan paths image yang terdapat pada album yang
	// sedang dilihat
	String[] listOfPaths;

	// untuk menampilkan image
	Bitmap bMap;

	// merepresentasikan tipe item, berupa video atau image
	int tipe;

	/**
	 * method di bawah ini akan dipanggil pertama kali saat activity dijalankan
	 * 
	 * @param Bundle
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// activity ini menggunakan layout full_screen.xml
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.full_screen);

		// membuat objek Bundle yang berisikan berbagai informasi
		Bundle b = getIntent().getExtras();
		// posisi path foto pada array of string
		position = b.getInt("position");
		// path-path yang terdapat pada album yang sedang dilihat
		listOfPaths = b.getStringArray("listitems");
		// id album yang sedang dilihat
		albumid = b.getInt("albumid");

		// path foto yang sedang dilihat
		path = b.getString("path");

		// membuka database aplikasi
		db.open();
		// membuat objek Cursor yang akan menunjuk path foto sesuai
		// dengan indeks path yang sedang dilihat
		Cursor cursor = db.getSingleItem(albumid, listOfPaths[position]);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			// mengambil id dari item sesuai yang ditunjuk oleh cursor
			itemid = db.getItemID(cursor);
		}

		// mengambil tipe item, video atau image
		tipe = db.getItemType(cursor);
		// menutup cursor
		cursor.close();
		// menutup database
		db.close();

		// mengambil path sesuai dengan indeks path yang sedang dilihat
		item.setPath(listOfPaths[position]);
		// mengambil posisi indeks path sebagai posisi path foto yang
		// sedang dilihat
		posisiFling = position;

		// path item yang akan ditampilkan
		ImageView img = (ImageView) findViewById(R.id.Image_full);

		// membuat tampilan saat full screen
		bMap = setImageType(tipe);
		img.setImageBitmap(bMap);

		// mengecek adanya gesture saat fling
		gestureDetector = new GestureDetector(this,
				new SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {

						// jika fling ke kiri
						if (e2.getX() - e1.getX() > LARGE_MOVE) {
							// jika indeks image == 0, image yang ditampilkan
							// adalah
							// image di indeks terakhir
							if (posisiFling == 0) {
								posisiFling = listOfPaths.length - 1;
							} else {
								posisiFling = (posisiFling - 1)
										% listOfPaths.length;
							}

							// membuka database aplikasi
							db.open();
							// membuat objek Cursor yang akan menunjuk path foto
							// sesuai
							// dengan indeks path yang sedang dilihat
							Cursor cursor = db.getSingleItem(albumid,
									listOfPaths[posisiFling]);
							cursor.moveToFirst();
							tipe = db.getItemType(cursor);
							// menutup cursor
							cursor.close();
							// menutup database
							db.close();

							// path item yang akan ditampilkan
							item.setPath(listOfPaths[posisiFling]);

							// membuat tampilan saat full screen
							// jika item berupa video, maka akan dibuat
							// thumbnailnya
							// jika item berupa image, akan dibuat image view
							// biasa

							ImageView img = (ImageView) findViewById(R.id.Image_full);

							// membuat tampilan saat full screen
							bMap = setImageType(tipe);
							img.setImageBitmap(bMap);

							return true;
						}
						// jika fling ke kanan
						else if (e1.getX() - e2.getX() > LARGE_MOVE) {

							posisiFling = (posisiFling + 1)
									% listOfPaths.length;

							// membuka database aplikasi
							db.open();
							// membuat objek Cursor yang akan menunjuk path foto
							// sesuai
							// dengan indeks path yang sedang dilihat
							Cursor cursor = db.getSingleItem(albumid,
									listOfPaths[posisiFling]);
							cursor.moveToFirst();
							tipe = db.getItemType(cursor);
							// menutup cursor
							cursor.close();
							// menutup database
							db.close();

							// path item yang akan ditampilkan
							item.setPath(listOfPaths[posisiFling]);

							// membuat tampilan saat full screen
							// jika item berupa video, maka akan dibuat
							// thumbnailnya
							// jika item berupa image, akan dibuat image view
							// biasa
							ImageView img = (ImageView) findViewById(R.id.Image_full);

							// membuat tampilan saat full screen
							bMap = setImageType(tipe);
							img.setImageBitmap(bMap);

							return true;
						}

						return false;
					}
				});
	}

	/**
	 * method ini akan mendeteksi gesture yang terjadi pada screen
	 * 
	 * @param event
	 *            bertipe MotionEvent yaitu event yang muncul akibat gesture
	 * @return true jika terdapat gesture
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	/**
	 * jika pengguna aplikasi menekan tombol close maka activity akan selesai
	 * dan kembali ke tampilan detil item
	 * 
	 * @param v
	 *            : sebuah view
	 * @return void
	 */
	public void close(View v) {
		finish();
	}

	/**
	 * Method di bawah ini dipanggil untuk membedakan tampilan pada saat
	 * fullscreen. Untuk image, item yang ditampilkan berupa ImageView. Untuk
	 * video, yang ditampilkan saat fullscreen adalah thumbnail dari video
	 * tersebut.
	 * 
	 * @param tipe
	 *            , berupa video atau image
	 * @return
	 */
	public Bitmap setImageType(int tipe) {
		Bitmap bMap;
		if (tipe == Dashboard.VIDEO) {
			bMap = ThumbnailUtils.createVideoThumbnail(item.getPath(),
					MediaStore.Video.Thumbnails.MINI_KIND);
		} else {
			File file = new File(item.getPath());
			bMap = ItemsController.decodeFile(file);
		}

		return bMap;
	}
}
