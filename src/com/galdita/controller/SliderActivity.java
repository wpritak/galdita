package com.galdita.controller;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.os.Handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.galdita.R;

/**
 * Class SliderActivity ini merupakan sebuah activity yang akan menjalankan
 * proses slideshow foto. Foto-foto yang terdapat dalam suatu album yang telah
 * dibuka oleh pengguna aplikasi akan ditampilkan secara bergantian dan kontinu.
 * Slideshow akan berhenti jika pengguna menekan tombol close.
 * 
 * @author Primaningtyas Sukawati
 * @author Habiburrahman
 */
public class SliderActivity extends Activity {

	// objek database
	DBGaldita db = new DBGaldita(this);

	// variabel yang merepresentasikan index foto yang sedang ditampilkan
	public int currentimageindex = 0;

	// variabel timer yang akan mengatur waktu selama slideshow
	Timer timer;
	TimerTask task;

	// variabel bertipe ImageView yang merepresentasikan foto yang ditampilkan
	ImageView slidingimage;

	// variabel bertipe String yang merepresentasikan path foto
	String path;

	// variabel bertipe integer yang merepresentasikan posisi path saat ini
	int position;

	// variabel bertipe integer yang merepresentasikan id dari album yang
	// sedang dibuka
	int albumId;

	// variabel bertipe Bitmap yang merupakan tipe foto yang akan ditampilkan
	// secara slideshow
	Bitmap bmap;

	// Array yang berisi paths item yang akan ditampilkan selama slideshow
	ArrayList<String> itemPaths = new ArrayList<String>();

	/**
	 * method di bawah ini akan dipanggil pertama kali saat activity dijalankan
	 * 
	 * @param Bundle
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// activity ini menggunakan layout slideshow.xml
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.slideshow);

		// membuat objek Bundle yang berisikan berbagai informasi
		Bundle b = getIntent().getExtras();
		// path foto yang sedang dilihat
		path = b.getString("path");
		// id album yang sedang dilihat
		albumId = b.getInt("albumId");
		// indeks image yang sedang dilihat
		db.open();
		Cursor cursor = db.getItems(albumId);
		int count = 0;
		itemPaths = new ArrayList<String>();

		// mengambil item yang berupa image saja
		// item berupa video tidak akan ditampilkan saat slideshow
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			int type;
			while (!cursor.isAfterLast()) {
				type = db.getItemType(cursor);
				if (type == Dashboard.IMAGE) {
					String thisPath = db.getItemPath(cursor);
					itemPaths.add(thisPath);
					if (thisPath == path) {
						position = count;
					}
					count++;
				}
				cursor.moveToNext();
			}
		}
		cursor.close();

		currentimageindex = position;

		// membuat objek Handler
		final Handler mHandler = new Handler();

		// membuat objek Runnable yang digunakan untuk menampilkan foto secara
		// slideshow
		final Runnable mUpdateResults = new Runnable() {
			// memanggil fungsi untuk menampilkan foto-foto di mana
			// path dari foto-foto tersebut tersimpan pada array of string
			// imagepaths
			public void run() {
				AnimateandSlideShow();
			}
		};

		// delay selama 1 detik
		int delay = 1000;
		// mengulangi slideshow dari awal setiap 3 detik
		int period = delay * 3;

		// membuat objek Timer untuk mengatur waktu selama slideshow
		Timer timer = new Timer();

		// ketika waktu tertentu, akan handler akan dipanggil
		// animasi slideshow pun dipanggil
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				mHandler.post(mUpdateResults);
			}
		}, delay, period);
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
	 * method di bawah ini akan menampilkan animasi slideshow
	 * 
	 * @param : array of string yang berisikan path-path foto yang terdapat di
	 *        dalam album yang sedang dilihat.
	 * @return void
	 */
	private void AnimateandSlideShow() {

		slidingimage = (ImageView) findViewById(R.id.Image);
		// membuat objek File dari foto sesuai dengan path yang terdapat
		// pada array of string
		File file = new File(itemPaths.get(currentimageindex));
		// mengubah objek File tersebut menjadi objek Bitmap
		bmap = ItemsController.decodeFile(file);
		// menampilkan objek ImageView
		slidingimage.setImageBitmap(bmap);

		// indeks path bertambah 1, path foto yang ditampilkan
		// selanjutnya adalah path foto dengan indeks sekarang ditambah 1
		currentimageindex++;
		if (currentimageindex >= itemPaths.size()) {
			currentimageindex = 0;
		}

		// memanggil animasi yang diatur menjadi animasi slideshow
		Animation rotateimage = AnimationUtils.loadAnimation(this,
				R.anim.fade_in);

		// menjalankan animasi slideshow tersebut
		slidingimage.startAnimation(rotateimage);
	}
}