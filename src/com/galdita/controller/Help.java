package com.galdita.controller;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Class Help
 * Class ini merupakan activity yang dipanggil untuk menampilkan
 * pilihan menu help
 * 
 * 
 * @author Habiburrahman
 * 
 */
public class Help extends ListActivity {

	/**
	 * onCreate
	 * 
	 * Method ini merupakan method yang akan di override dan dipanggil saat
	 * activity pertama kali d create
	 * 
	 * @param Bundle
	 */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// membuat tampilan menu Help
		super.onCreate(savedInstanceState);
		// generate list
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mStrings));
		getListView().setTextFilterEnabled(true);
		
		
	}
	
	// pilihan menu help berupa array string
	private String[] mStrings = { "Membuat Album", "Menghapus Album", "Mengubah Album", "Melihat Isi Album", 
			"Menambah Foto/Video", "Melihat Foto", "Melihat Video",
			"Menghapus Foto/Video", "Mengambil Foto/Video" };

	/**
	 * onListItemClick
	 * 
	 * Method ini merupakan method yang dipanggil ketika ada salah satumenu help
	 * yang dipilih oleh user
	 * 
	 * @param ListView, View, int, long
	 * 
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// panggil method onListItemClick
		super.onListItemClick(l, v, position, id);
		// dapatkan item yang dipilih
		Object o = this.getListAdapter().getItem(position);
		// ubah ke dalam string
		String keyword = o.toString();
		for (int i = 0; i < mStrings.length; i++) {
			// jika keyword sama dengan salah satu menu help yang ada, maka
			// menu tersebut akan ditampilkan dengan meng-intent class HelpContent
			if (keyword.equals(mStrings[i])) {
				Intent intent = new Intent(this, HelpContent.class);
				intent.putExtra("Choice", i);
				startActivity(intent);
			}
		}

	}
}