package com.galdita.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.galdita.model.Album;
import com.galdita.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Kelas ini merupakan controller dari album yang ditampilkan termasuk
 * didalamnya menampilkan item-item yang telah dimasukkan ke dalam album.
 * 
 * @author Abi Rheza Nasa'i
 * @author Rezky Ramadiansyah
 * @author Habiburrahman
 * @author Primaningtyas Sukawati
 * 
 */
public class AlbumController extends Activity implements OnItemClickListener {
	// objek database Galdita
	DBGaldita db = new DBGaldita(this);

	// konstanta
	private static final int PICTURE_REQUEST = 1888;

	// konstanta
	private static final int VIDEO_REQUEST = 200;

	// prefix untuk berkas dari penggunaan kamera
	private static final String FILE_PREFIX = "/Camera/Galdita_";

	// path image
	private String itemPath;

	// jumlah file
	private int jumlahFile;

	// kumpulan thumbnail dalam bentuk bitmap
	private Bitmap[] thumbnails;

	// objek Album
	Album album = new Album();

	// kumpulan itemID
	private int[] itemsId;

	// kumpulan path item
	private String[] itemsPath;

	// kumpulan tipe item
	private int[] itemsType;

	/**
	 * Method ini akan dipanggil pertama kali saat activity dijalankan.
	 * 
	 * @param Bundle
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mengambil konten extra yang dikirim dari
		// activity sebelumnya yang menjalankan activity
		// ini
		Bundle b = getIntent().getExtras();
		album.setAlbumID(b.getInt("albumID"));
		album.setAlbumName(b.getString("albumName"));
		album.setAlbumLocation(b.getString("location"));

		// untuk layout menggunakan album.xml
		setContentView(R.layout.album);

		// check path-path image-nya masih valid atau tidak
		pathsCheck();

		db.open();
		Cursor cursor = db.getItems(album.getAlbumID());
		int rows = cursor.getCount();
		if (rows != 0) {
			int index = 0;
			itemsId = new int[rows];
			itemsPath = new String[rows];
			itemsType = new int[rows];
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				itemsId[index] = db.getItemID(cursor);
				itemsPath[index] = db.getItemPath(cursor);
				itemsType[index] = db.getItemType(cursor);
				cursor.moveToNext();
				index++;
			}
		}
		cursor.close();
		db.close();

		jumlahFile = rows;

		// membuat variabel thumbnails sesuai jumlah file
		// yang ada di album
		thumbnails = new Bitmap[jumlahFile];

		// Tampilan grid untuk image-image dalam album
		GridView gridview = (GridView) findViewById(R.id.gridview);
		final ImageAdapter iAdapter = new ImageAdapter(AlbumController.this);
		gridview.setAdapter(iAdapter);
		gridview.setOnItemClickListener(AlbumController.this);

		// Membuat thread
		Thread thread = new Thread(new Runnable() {
			// melakukan decoding pada image-image pada album
			// untuk membuat thumbnail
			public void run() {
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inSampleSize = 10;
				for (int i = 0; i < jumlahFile; i++) {
					if (itemsType[i] == Dashboard.IMAGE) {
						thumbnails[i] = ThumbnailUtils.extractThumbnail(
								BitmapFactory.decodeFile(itemsPath[i], opt),
								96, 96);
					} else {
						thumbnails[i] = ThumbnailUtils.createVideoThumbnail(
								itemsPath[i],
								MediaStore.Images.Thumbnails.MICRO_KIND);
					}
					runOnUiThread(new Runnable() {
						// Mengatur isi layout album
						public void run() {
							iAdapter.notifyDataSetChanged();
						}
					});
				}

				runOnUiThread(new Runnable() {
					// Mengatur isi layout album
					public void run() {
						TextView albname = (TextView) findViewById(R.id.albumname);
						albname.setText("Album Name: " + album.getAlbumName());

						TextView albumLoc = (TextView) findViewById(R.id.albumloc);
						albumLoc.setText("Number of files: "+ jumlahFile);
						
						// Tombol untuk menambah berkas dari kamera
						ImageButton addFromCam = (ImageButton) findViewById(R.id.add_from_cam_button);
						addFromCam.setOnClickListener(chooseCameraMode);

						// Tombol untuk menambah berkas dari media penyimpanan
						ImageButton addItem = (ImageButton) findViewById(R.id.addItem);
						addItem.setOnClickListener(addItemL);

						// Tombol untuk memilih berkas untuk dihapus						ImageButton deleteItem = (ImageButton) findViewById(R.id.del						// Tombol untuk menutup albnum
						ImageButton closeAlbum = (ImageButton) findViewById(R.id.deleteItem);
						closeAlbum.setOnClickListener(deleteItemL);
					}
				});
			}
		});
		thread.start();// menjalankan thread
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Cek jumlah item di dalam database
		db.open();
		Cursor cursorCheck = db.getJumlahItems(album.getAlbumID());
		cursorCheck.moveToFirst();
		int numItems = cursorCheck.getInt(0);

		cursorCheck.close();
		db.close();

		// Jika jumlah item di database berubah, jalankan album activity
		// yang baru
		if (numItems != jumlahFile) {
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}
	}

	/**
	 * Method untuk melakukan validasi path-path image yang ada pada album
	 * 
	 * @return void
	 */
	public void pathsCheck() {
		List<String> allFiles = new ArrayList<String>();
		List<String> allItems = new ArrayList<String>();
		final String[] columns = { MediaColumns.DATA,
				BaseColumns._ID };
		final String orderBy = BaseColumns._ID;

		// scanning all images in sdcard
		Cursor cursorAllimage = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy);
		int numFiles = cursorAllimage.getCount();
		if (numFiles > 0) {
			for (int i = 0; i < numFiles; i++) {
				cursorAllimage.moveToPosition(i);
				int dataColumnIndex = cursorAllimage
						.getColumnIndex(MediaColumns.DATA);
				String path = cursorAllimage.getString(dataColumnIndex);
				allFiles.add(path);
			}
		}
		cursorAllimage.close();
		// end of it

		// scanning all videos in sdcard
		final String[] videoColumns = { MediaColumns.DATA,
				BaseColumns._ID };
		final String videoOrderBy = BaseColumns._ID;
		Cursor cursorAllvideo = managedQuery(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns,
				null, null, videoOrderBy);
		int numVid = cursorAllvideo.getCount();

		if (numVid > 0) {
			for (int i = 0; i < numVid; i++) {
				cursorAllvideo.moveToPosition(i);
				int dataColumnIndex = cursorAllvideo
						.getColumnIndex(MediaColumns.DATA);
				String path2 = cursorAllvideo.getString(dataColumnIndex);
				allFiles.add(path2);
			}
		}
		cursorAllvideo.close();
		// end of it

		// mengambil path dari semua item yang ada di database
		db.open();
		Cursor cursorCheck = db.getAllItem();
		int numItems = cursorCheck.getCount();

		if (numItems > 0) {
			cursorCheck.moveToFirst();
			while (!cursorCheck.isAfterLast()) {
				allItems.add(db.getItemPath(cursorCheck));
				cursorCheck.moveToNext();
			}
		}
		cursorCheck.close();
		db.close();
		// end of it

		// cari items yang sudah tidak ada di sd card
		int obsoleteItems = 0;
		allItems.removeAll(allFiles);
		if (!allItems.isEmpty()) {
			obsoleteItems = allItems.size();
			db.open();
			for (int i = 0; i < obsoleteItems; i++) {
				db.deleteObsoleteItem(allItems.get(i));
			}
			db.close();
		} else {
			return;
		}
	}

	/**
	 * Jika thumbnail item diklik, akan menampilkan detil item yang dibuat pada
	 * class ItemsController.
	 */
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Bundle b = new Bundle();
		b.putInt("position", position);
		b.putInt("albumid", album.getAlbumID());
		b.putStringArray("listitems", itemsPath);
		Intent intent = new Intent(this, ItemsController.class);
		intent.putExtras(b);
		startActivity(intent);
	}

	// listener untuk tombol menambah berkas dari kamera
	// jika di klik maka akan memanggil fungsi kamera
	private OnClickListener chooseCameraMode = new OnClickListener() {
		public void onClick(View V) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					AlbumController.this);
			builder.setMessage("I want to take..")
					.setCancelable(true)
					.setPositiveButton("Image",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									addImageFromCam();
								}
							})
					.setNegativeButton("Video",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									addVideoFromCam();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	};

	private void addImageFromCam() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());

		String path = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DCIM).getPath();
		new File(path).mkdirs();
		itemPath = path + FILE_PREFIX + timeStamp + ".jpg";
		Log.e("IMGP", itemPath);
		Uri uriSavedImage = Uri.fromFile(new File(itemPath));
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
		startActivityForResult(cameraIntent, PICTURE_REQUEST);
	}

	private void addVideoFromCam() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());

		String path = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DCIM).getPath();
		new File(path).mkdirs();
		itemPath = path + FILE_PREFIX + timeStamp + ".3gp";
		Log.e("IMGP", itemPath);
		Uri uriSavedImage = Uri.fromFile(new File(itemPath));
		Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
		cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(cameraIntent, VIDEO_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICTURE_REQUEST) {
			if (resultCode == RESULT_OK) {
				File f = new File(itemPath);
				if (f.exists()) {
					addImageGallery(f);
					Log.e("ZZZ", itemPath);
					db.open();
					// cek tipe file yang diinsert
					db.insertItem(album.getAlbumID(), itemPath, "", Dashboard.IMAGE);
					db.close();
					Intent intent = getIntent();
					finish();
					startActivity(intent);
				}
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the video capture
	        } else {
	            // Video capture failed, advise user
	        }
		} else if (requestCode == VIDEO_REQUEST) {
			if (resultCode == RESULT_OK) {
				File f = new File(itemPath);
				if (f.exists()) {
					addVideoGallery(f);
					Log.e("VVV", itemPath);
					db.open();
					// cek tipe file yang diinsert
					db.insertItem(album.getAlbumID(), itemPath, "", Dashboard.VIDEO);
					db.close();
					Intent intent = getIntent();
					finish();
					startActivity(intent);
				}
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the video capture
	        } else {
	            // Video capture failed, advise user
	        }
		}
	}

	// listener untuk tombol menambah berkas dari media penyimpanan
	// jika di klik maka akan menjalankan activity ItemChooser
	// dalam mode "TAMBAH ITEM"
	private OnClickListener addItemL = new OnClickListener() {
		public void onClick(View V) {
			Intent intent = new Intent(AlbumController.this, ItemChooser.class);
			Bundle b = new Bundle();
			// album dengan id 0 adalah album berisi item yang belum terassign
			b.putInt("albumID", album.getAlbumID());
			b.putInt("mode", ItemChooser.TAMBAH_ITEM);
			intent.putExtras(b);
			startActivity(intent);
		}
	};

	// listener untuk tombol membuang berkas dari album
	// jika di klik maka akan menjalankan activity ItemChooser
	// dalam mode "HAPUS ITEM"
	private OnClickListener deleteItemL = new OnClickListener() {
		public void onClick(View V) {
			Intent intent = new Intent(AlbumController.this, ItemChooser.class);
			Bundle b = new Bundle();
			// album dengan id 0 adalah album berisi item yang belum terassign
			b.putInt("albumID", album.getAlbumID());
			b.putInt("mode", ItemChooser.HAPUS_ITEM);
			intent.putExtras(b);
			startActivity(intent);
		}
	};

	// listener untuk tombol menutup album
	// jika di klik maka akan mengakhiri activity Album
//	private OnClickListener closeAlbumL = new OnClickListener() {
//		public void onClick(View V) {
//			finish();
//		}
//	};

	/**
	 * Method untuk menambahkan image ke gallery
	 * 
	 * @param file
	 *            image yang akan ditambahkan
	 * 
	 * @return void
	 */
	private void addImageGallery(File file) {
		ContentValues values = new ContentValues();
		values.put(MediaColumns.DATA, file.getAbsolutePath());
		values.put(MediaColumns.MIME_TYPE, "image/jpeg");
		getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}

	/**
	 * Method untuk menambahkan image ke gallery
	 * 
	 * @param file
	 *            image yang akan ditambahkan
	 * 
	 * @return void
	 */
	private void addVideoGallery(File file) {
		ContentValues values = new ContentValues();
		values.put(MediaColumns.DATA, file.getAbsolutePath());
		values.put(MediaColumns.MIME_TYPE, "video/3gp");
		getContentResolver().insert(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
	}

	// Class yang bertugas untuk menampilkan image-image dalam album
	public class ImageAdapter extends BaseAdapter {
		private Context context;

		public ImageAdapter(Context c) {
			context = c;
		}

		// menghitung jumlah image yang akan ditampilkan
		// oleh ImageAdapter
		public int getCount() {
			int ret = 0;
			if (itemsPath != null)
				ret = itemsPath.length;
			return ret;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		// method yang men-set image (thumbnails) yang akan
		// digunakan oleh ImageAdapter
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageview;
			if (convertView == null) {
				imageview = new ImageView(context);
			} else {
				imageview = (ImageView) convertView;
			}
			Bitmap bmap = thumbnails[position];
			imageview.setImageBitmap(bmap);
			return imageview;
		}
	}

	/**
	 * untuk membuat pilihan menu Help dan Credit
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Dashboard.HELP, 0, "Help");
		menu.add(0, Dashboard.CREDIT, 0, "Credit");
		return true;
	}

	/**
	 * Menentukan intent mana yang akan dijalankan sesuai dengan pilihan menu
	 * (help atau credit).
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
