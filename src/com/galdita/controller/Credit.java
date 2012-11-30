package com.galdita.controller;

import java.io.File;

import com.galdita.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Class Credit
 * Class ini digunakan untuk menampilkan credit dari aplikasi 
 * Galdita 1.0
 * 
 * @author Habiburrahman
 * 
 */
public class Credit extends Activity {

	/**
	 * onCreate
	 * 
	 * Method ini merupakan method yang akan di override dan dipanggil saat
	 * activity pertama kali di-create
	 * 
	 * @param Bundle
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// memanggil method onCreate pada class Activity
		super.onCreate(savedInstanceState);
		// set layout
		setContentView(R.layout.credit);
		// set string header credit
		String header = "\nGaldita 1.0";
		// set body credit
		String body= "\n\n" +
				"Galdita 1.0 ialah sebuah aplikasi album yang berbasiskan peta\n" +
				"Aplikasi dapat digunakan untuk megatur penyimpanan foto dan video " +
				"sehingga dapat disusun secara manual berdasarkan lokasi dimana " +
				"foto atau video tersebut di ambil\n\n" +
				"Oleh\n\n" +
				"Abi Rheza N\n" +
				"Habiburrahman\n" +
				"Rezky R\n" +
				"Primaningtyas S\n\n";
		// set footer credit
		String footer = "copyright(2012)";
		
		// set Image

		// inisialisasi textview
        TextView a = (TextView)findViewById(R.id.bold);
        TextView b = (TextView)findViewById(R.id.italic);
        TextView c = (TextView)findViewById(R.id.normal);
        
        // set text pada textview
        a.setText(header);
        b.setText(body);
        c.setText(footer);
	}
}