package com.galdita.controller;

import com.galdita.R;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Class Video ini akan dipanggil ketika suatu video
 * dimainkan. Tipe video yang dapat dimainkan atau
 * diputar adalah .mp4 , .3gp, dan .avi
 * 
 * @author Primaningtyas Sukawati
 *
 */
public class Video extends Activity {
	// variabel bertipe String yang merepresentasikan path image
	// yang ditampilkan
	String path = "";
	// variabel bertipe Bundle yang berguna untuk passing data dari acivity lain
	Bundle b = null;
	// objek database, digunakan ketika mengakses database aplikasi
	DBGaldita db = new DBGaldita(this);

	/**
	 * method di bawah ini akan dipanggil pertama kali saat activity dijalankan
	 * 
	 * @param Bundle
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video);

		// membuat objek Bundle yang berisikan berbagai informasi
		Bundle b = getIntent().getExtras();

		// path foto yang sedang dilihat
		path = b.getString("path");

		// menampilkan video view sesuai dengan path yang ada
		VideoView videoView = (VideoView) this.findViewById(R.id.videoView);
		videoView.setVideoURI(Uri.parse(path));
		videoView.requestFocus();

		// memainkan video
		MediaController mc = new MediaController(this);
		videoView.setMediaController(mc);
		videoView.start();
	}
}
