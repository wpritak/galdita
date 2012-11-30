package com.galdita.controller;

import com.galdita.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Class HelpContent
 * Class ini merupakan activity yang dipanggil untuk menampilkan
 * pilihan content dari menu help yang dipilih oleh user
 * 
 * 
 * @author Habiburrahman
 * 
 */

public class HelpContent extends Activity {
	// choice, untuk menyimpan pilihan user
	private int choice;
	// content, berisi content help
	private String content;
	private String title;
	private String content1;
	private String content2;
	private String content3;
	private String content4;
	private String content5;

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
		// panggil method onCreate
		super.onCreate(savedInstanceState);
		// set layout
		setContentView(R.layout.help);
		// ambil bundle
		Bundle b = getIntent().getExtras();
		// set nilai choice dari integer yang di-bundle
		choice = b.getInt("Choice");
		// lakukan proses untuk men-set nilai content
		
		//ImageView img = 
		
		switch (choice) {
			// jika pilihan adalah 0, Membuat Album
			case 0:
				title = "Membuat Album\n";
				content =
						"1. Tekan suatu lokasi pada peta. Kemudian akan muncul sebuah form.\n" + 
						"2. Isi nama album dan deskripsi album.\n" +
						"3. Pilih cover album dengan cara menekan menu Add Album Cover. Pilih salah satu image untuk dijadikan cover. Tekan Select.\n" +
						"4. Pilih marker sesuai dengan yang diinginkan. Marker akan default jika tidak ada marker yang dipilih.\n" +
						"5. Tekan OK.";
				break;
				
			// jika pilihan adalah 1, Menghapus Album
			case 1:
				title = "Menghapus Album\n";
				content = 
						"1. Pilih album yang akan dihapus dengan cara menekan marker album tersebut. Sebuah balloon akan muncul.\n" +
						"2. Tekan balloon yang muncul tersebut selama beberapa detik.\n" +
						"3. Pilih menu Delete.";
				break;
				
			// jika pilihan adalah 2, Edit Album
			case 2:
				title = "Mengubah Album\n";
				content = 
						"1. Pilih album yang akan diedit dengan cara menekan marker album tersebut. Sebuah balloon akan muncul.\n" +
						"2. Tekan balloon yang muncul tersebut selama beberapa detik.\n" +
						"3. Edit album sesuai dengan keinginan. (Ubah cover, nama, deskripsi, atau marker)\n" +
						"4. Pilih OK.";
				break;
				
			// jika pilihan adalah 3, Open Album
			case 3:
				title = "Melihat Isi Album\n";
				content =
						"1. Pilih suatu album yang telah terdapat pada Galdita. Tekan marker album tersebut.\n" +
						"2. Tekan balloon yang muncul." +
						"3. List foto/video yang tersimpan pada album tersebut ditampilkan di layar";
				break;
				
			// jika pilihan adalah 4, Add Photo/Video
			case 4:
				title = "Menambah Foto/Video\n";
				content =
						"1. Buka album.\n " +
						"Dari Media Penyimpanan :\n" +
						"1. Pilih menu Add Item. Pilih items yang akan dimasukkan ke dalam album. Tekan Select.\n\n" +
						"Dari Kamera :\n" +
						"1. Pilih menu Camera. Ambil gambar yang diinginkan. Pilih OK.";
				break;
				
			// jika pilihan adalah 5, View Photo
			case 5:
				title = "Melihat Foto\n";
				content =
						"1. Buka album.\n " +
						"2. Pilih foto yang akan dilihat.\n" +
						"3. Foto akan ditampilkan bersama detil foto.\n" +
						"4. Tap foto untuk melihat foto secara slideshow atau fullscreen.";
				break;
			
			// jika pilihan adalah 6, View Video
			case 6:
				title = "Melihat Video\n";
				content =
						"1. Buka album.\n " +
						"2. Pilih video yang akan dilihat.\n" +
						"3. Foto akan ditampilkan bersama detil video.\n" +
						"4. Tap foto untuk memainkan video.";
				break;
				
			// jika pilihan adalah 7, Delete Photo/Video
			case 7:
				title = "Menghapus Foto/Video\n";
				content =
						"Menghapus Banyak Foto/Video :\n" +
						"1. Buka album.\n " +
						"2. Pilih menu Delete Photo/Video.\n" +
						"3. Pilih foto/video yang akan dihapus. Tekan Select.\n\n" +
						"Menghapus Sebuah Foto/Video :\n" +
						"1. Buka album.\n" +
						"2. Pilih salah satu foto/video yang akan dihapus.\n" +
						"3. Pilih menu Delete Photo/Video.";
				break;
				
			// jika pilihan adalah 8, Take Photo
			case 8:
				title = "Mengambil Foto/Video\n";
				content =
						"1. Buka album.\n" +
						"2. Pilih menu Camera\n" +
						"3. Pilih Image atau Video.\n" +
						"4. Ambil gambar yang diinginkan. Pilih OK.";
				break;
			
			// selain dari pilihan yang ada
			default:
				content = "default_content";
		}
		TextView tv2 = (TextView) findViewById(R.id.title);
		TextView tv = (TextView) findViewById(R.id.content);
		// set textview
		tv2.setText(title);
		tv.setText(content);
	}
	
	
}