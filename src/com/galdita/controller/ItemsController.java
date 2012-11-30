package com.galdita.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.galdita.R;
import com.galdita.model.Item;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.view.View.OnClickListener;

/**
 * Class ItemsController merupakan controller yang mengatur item-item yang
 * berupa image dan video yang terdapat pada suatu album.
 * 
 * @author Primaningtyas Sukawati
 * 
 */
public class ItemsController extends Activity {

	// konstanta untuk maksimal image yang bisa ditampilkam
	private static final int IMAGE_MAX_SIZE = 500;

	// status yang digunakan sebagai penanda perubahan icon
	// tulis/simpan caption
	int status = 0;

	// path image yang akan dilihat
	String path = "";

	// nama image yang akan dilihat
	String name = null;

	// objek database Galdita
	DBGaldita db = new DBGaldita(this);

	// indeks image yang sedang dilihat
	int position;

	ItemsController it = this;

	// untuk menerima input caption dari user
	EditText captionEdit;

	// menampilkan caption yang tersimpan
	TextView captionText;

	// untuk menampilkan image atau thumbnail video
	Bitmap bMap;

	// objek Item
	Item item = new Item();

	// merepresentasikan album ID tempat item berada
	int albumId;

	// merepresentasikan tipe item, berupa video atau image
	static int tipe;

	// list absolute path dari item yang tersimpan dalam sebuah album
	private String[] listOfPaths;

	// String yang akan menampung tipe item, yang akan ditampilkan pada Toast
	String type = "";

	/**
	 * method di bawah ini akan dipanggil pertama kali saat activity dijalankan
	 * 
	 * @param Bundle
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// menggunakan item.xml
		super.onCreate(savedInstanceState);

		// membuat objek Bundle yang berisikan berbagai informasi
		Bundle b = getIntent().getExtras();

		// posisi path foto pada array of string
		position = b.getInt("position");

		// list images yang terdapat pada album yang sedang dibuka
		listOfPaths = b.getStringArray("listitems");

		// id album yang sedang dibuka
		albumId = b.getInt("albumid");

		// mengeset album id
		item.setAlbumid(albumId);

		// membuka database
		db.open();
		// membuat objek Cursor yang akan menunjuk path foto sesuai
		// dengan indeks path yang sedang dilihat
		Cursor cursor = db.getSingleItem(albumId, listOfPaths[position]);
		cursor.moveToFirst();

		// mengambil tipe item
		tipe = db.getItemType(cursor);
		// int type = db.getItemType(cursor);

		// jika item yang dilihat berupa image, akan memanggil layout
		// item_image.xml
		// jika video, akan memanggil layout item_video.xml
		if (tipe == Dashboard.IMAGE) {
			setContentView(R.layout.item_image);
		} else {
			setContentView(R.layout.item_video);
		}

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			// mengatur caption sesuai yang terdapat pada database
			item.setCaption(db.getItemCaption(cursor));
			// mengatur id item sesuai yang terdapat pada database
			item.setItemid(db.getItemID(cursor));
		}

		// menutup cursor
		cursor.close();
		// menutup database
		db.close();

		// mengambil path image yang sedang dilihat
		item.setPath(listOfPaths[position]);

		// mengambil nama item yang sedang dilihat
		String nama = "";
		String[] array = item.getPath().split("/");

		// mengambil nama image yang telah displit
		nama = array[array.length - 1];

		// menampilkan nama item
		TextView textView = (TextView) findViewById(R.id.nama_foto);
		textView.setText(nama);

		// jika item yang sedang dilihat berupa video, akan dibuat thumbnail
		// dari video
		// tersebut.
		// selain video, akan ditampilkan image tersebut.
		if (tipe == Dashboard.VIDEO) {
			ImageView vid = (ImageView) findViewById(R.id.video);
			bMap = ThumbnailUtils.createVideoThumbnail(item.getPath(),
					MediaStore.Video.Thumbnails.MINI_KIND);
			vid.setImageBitmap(bMap);
		} else {
			ImageView img = (ImageView) findViewById(R.id.foto);
			File file = new File(item.getPath());

			// decode image dengan tujuan untuk mengurang memori
			bMap = decodeFile(file);
			img.setImageBitmap(bMap);
		}

		// menampilkan caption
		captionText = (TextView) findViewById(R.id.clickable_text_view);
		captionEdit = (EditText) findViewById(R.id.caption);

		// menampilkan caption yang sesuai
		if (item.getCaption().equals("")) {
			captionText.setText("Edit caption here...",
					TextView.BufferType.EDITABLE);
		} else {
			captionText.setText(item.getCaption());
			captionEdit.setHint(item.getCaption());
			captionEdit.setText(item.getCaption());
		}

		status = 0;
	}

	/**
	 * Method ini digunakan untuk menampilkan kotak dialog untuk mengkonfirmasi
	 * penghapusan item. Jika setuju untuk menghapus item, maka item yang
	 * bersangkutan akan dihapus dari database.
	 * 
	 * @param view
	 */
	public void remove(View view) {
		// mengatur output type pada alert dialog
		if (tipe == Dashboard.VIDEO) {
			type = "video";
		} else {
			type = "photo";
		}

		// membuat alertDialog
		AlertDialog alertDialog = new AlertDialog.Builder(ItemsController.this)
				.create();
		alertDialog.setTitle("Confirm");
		alertDialog.setMessage("Are you sure to remove this " + type + "?");

		// membuka database
		db.open();
		Cursor c = db.getItemByID(item.getItemid());
		c.moveToFirst();
		// mengatur id item yang sedang dibuka
		item.setItemid(db.getItemID(c));
		// menutup cursor dan database
		c.close();
		db.close();

		// jika OK, maka item yang bersangkutan akan dihapus
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				db.open();
				db.deleteItem(item.getItemid());
				db.close();
				if (tipe == Dashboard.VIDEO) {
					type = "Video";
				} else {
					type = "Photo";
				}
				Toast.makeText(ItemsController.this,
						type + " has been removed", Toast.LENGTH_LONG).show();
				finish();
			}
		});
		// jika Cancel, tidak melakukan apapun
		alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// nothing
			}
		});
		alertDialog.show();
	}

	/**
	 * Method ini digunakan untuk menampilkan pilihan menu untuk menampilkan
	 * item secara full screen atau slide show
	 * 
	 * @param view
	 */
	public void showMenuScreen(View view) {

		final Context context = this;
		// custom dialog
		final Dialog dialog = new Dialog(context);
		dialog.getWindow();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.view_item_dialog);

		// menampilkan image
		ImageView image = (ImageView) dialog
				.findViewById(R.id.image_full_screen);
		image.setImageResource(R.drawable.fullscreen_icon2);

		// menambahkan listener pada image icon full screen
		// jika ditekan, maka image yang sedang dilihat akan
		// tampil secara full screen
		image.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				dialog.dismiss();
				Intent intent = new Intent(ItemsController.this,
						FullScreen.class);
				Bundle b = new Bundle();
				b.putString("nama", item.getPath());
				b.putInt("position", position);
				b.putInt("albumid", item.getAlbumid());
				b.putStringArray("listitems", listOfPaths);
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.putExtras(b);
				startActivity(intent);
			}

		});

		ImageView image2 = (ImageView) dialog
				.findViewById(R.id.image_slide_show);
		image2.setImageResource(R.drawable.slide_show);

		// menambahkan listener pada image icon slide show
		// jika ditekan, maka image-image yang terdapat pada album yang sedang
		// dilihat akan
		// tampil secara slide show
		image2.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				dialog.dismiss();
				Intent intent = new Intent(ItemsController.this,
						SliderActivity.class);
				Bundle b = new Bundle();
				b.putString("path", item.getPath());
				b.putInt("albumId", albumId);
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.putExtras(b);
				startActivity(intent);
			}
		});

		dialog.show();
	}

	/**
	 * Method untuk menukar antara dua view. Jadi view yang digunakan untuk
	 * caption memiliki dua macam, yaitu TextView untuk menampilkan caption yang
	 * telah disimpan ke dalam database, dan EditText untuk tempat user
	 * memasukkan caption yang baru (saat mengubah caption).
	 * 
	 * @param view
	 */
	public void TextViewClicked(View view) {

		ImageButton img;

		// saat user mengubah caption, edit button akan berubah
		// menjadi button save.
		// saat user tidak mengubah caption, save button akan berubah
		// menjadi button edit.
		switch (status) {
		case 0:
			img = (ImageButton) findViewById(R.id.edit_button);
			img.setImageResource(R.drawable.save);
			status = 1;
			break;
		case 1:
			img = (ImageButton) findViewById(R.id.edit_button);
			img.setImageResource(R.drawable.edit);
			status = 0;
			break;
		}

		// menukar antara dua view
		ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.my_switcher);
		switcher.showNext();
		String value = captionEdit.getText().toString();

		// jika caption belum pernah di-edit
		if (item.getCaption().equals("Edit caption here...")) {
			captionEdit.setHint(item.getCaption());
			captionEdit.setText(item.getCaption());
		}
		// jika caption sudah pernah di-edit
		// database akan diupdate sesuai dengan caption yang paling
		else {
			db.open();
			db.updateItemCaption(item.getItemid(), value);
			item.setCaption(item.getCaption());
			db.close();
		}

		// jika caption yang telah ada dihapus
		if (value.equals("")) {
			captionText.setText("Edit caption here...");
		} else {
			captionText.setText(value);
		}
	}

	/**
	 * Method ini akan men-decode ulang sebuah file Decode dilakukan dengan cara
	 * men-scale suatu image disesuaikan dengan image aslinya.
	 * 
	 * @param f
	 * @return image bertipe Bitmap yang memiliki ukuran lebih kecil
	 */
	public static Bitmap decodeFile(File f) {
		Bitmap b = null;
		try {

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			// mendecode file stream menjadi BitMap
			FileInputStream fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();

			int scale = 1;
			// membuat skala yang sesuai dengan image asli
			if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(
						2,
						(int) Math.round(Math.log(IMAGE_MAX_SIZE
								/ (double) Math.max(o.outHeight, o.outWidth))
								/ Math.log(0.5)));
			}
			Log.i("SCALE", "" + scale);
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis = new FileInputStream(f);
			b = BitmapFactory.decodeStream(fis, null, o2);
			fis.close();
		} catch (IOException e) {
		}
		return b;
	}

	/**
	 * jika pengguna aplikasi menekan tombol close maka activity akan selesai
	 * dan kembali ke tampilan detil item
	 * 
	 * @param v
	 *            : sebuah view
	 * @return void
	 */
	public void close(View view) {
		bMap.recycle();
		finish();
	}

	/**
	 * Method ini akan dipanggil jika thumbnail video di tap oleh user Video
	 * yang sedang diilihat detilnya akan dimainkan.
	 * 
	 * @param view
	 */
	public void playVideo(View view) {
		Intent intent = new Intent(ItemsController.this, Video.class);
		Bundle b = new Bundle();
		b.putString("path", item.getPath());
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.putExtras(b);
		startActivity(intent);
	}

	/**
	 * jika pengguna aplikasi menekan tombol cover maka activity akan selesai
	 * dan kembali ke tampilan detil item
	 * 
	 * @param v
	 *            : sebuah view
	 * @return void
	 */
	public void setAsCover(View view) {
		AlertDialog alertDialog = new AlertDialog.Builder(ItemsController.this)
				.create();
		alertDialog.setTitle("Confirm");
		alertDialog.setMessage("Are you sure to set this image as a cover?");

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				db.open();
				db.updateAlbumCover(item.getAlbumid(), item.getPath());
				db.close();
				finish();

			}

		});
		// jika Cancel, tidak melakukan apapun
		alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// nothing
			}
		});
		alertDialog.show();
	}

	/**
	 * Method ini akan menampilkan pilihan menu Help atau Credit
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Dashboard.HELP, 0, "Help");
		menu.add(0, Dashboard.CREDIT, 0, "Credit");
		return true;
	}

	/**
	 * Method ini akan dipanggil untuk memanggil intent sesuai dengan pilihan
	 * menu yang dipilih oleh user.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case Dashboard.HELP:
			intent = new Intent(this, Help.class);
			startActivity(intent);
			return true;
		case Dashboard.CREDIT:
			intent = new Intent(this, Credit.class);
			startActivity(intent);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
}
