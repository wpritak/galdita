package com.galdita.controller;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.galdita.R;
import com.galdita.model.Album;
import com.galdita.model.CustomItemizedOverlay;
import com.galdita.view.DashboardView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Kelas ini merupakan controller dari peta yang ditampilkan termasuk didalamnya
 * menampilkan album-album yang telah dibuat sebagai Overlay item dari map yang
 * ditampilkan.
 * 
 * @author Abi Rheza Nasa'i
 * @author Rezky Ramadiansyah
 * @author Primaningtyas Sukawati
 * @author Habiburrahman
 * 
 */
public class Dashboard extends MapActivity {
	// untuk menu help dan credit
	static final int HELP = Menu.FIRST;
	static final int CREDIT = Menu.FIRST + 1;
	static final int MAPTYPE = Menu.FIRST + 2;
	
	private Dashboard ds = this;
	// MapView yang digunakan untuk menampilkan peta
	private DashboardView mapView;

	// List yang menampung overlay yang dimiliki oleh map yang ditampilkan
	private List<Overlay> mapOverlays;

	// Item-item Overlay yang merepresentasikan album yang terikat pada suatu
	// lokasi tertentu
	private CustomItemizedOverlay<Album> itemizedOverlay;

	// Penampung sementara cover album
	static String albumCover = "";

	// Penampung lokasi long press
	private GeoPoint location;

	// Objek Pengakses Database pada kelas ini
	private DBGaldita db = new DBGaldita(this);

	// Objek album sebagai container informasi sebuah album
	Album album = new Album();

	// File Gambar
	static final int IMAGE = 0;

	// File Video
	static final int VIDEO = 1;

	// pilihan marker
	static String[] markers = {"Yellow", "Red", "Blue", "Green", "Grey" };

	/**
	 * Method yang dipanggil pertama kali sebagai inisiasi aplikasi khususnya
	 * untuk menampilkan peta dan ikon-ikon album yang telah dibuat
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(ACTIVITY_SERVICE, "Panggil Yang Create");
		setContentView(R.layout.main);

		new Scan().execute(this);

		mapView = (DashboardView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		mapOverlays = mapView.getOverlays();

		// Set Listener untuk menanggapi long press
		mapView.setOnLongpressListener(new DashboardView.OnLongpressListener() {
			public void onLongpress(final MapView view,
					final GeoPoint longpressLocation) {
				runOnUiThread(new Runnable() {
					public void run() {
						// Insert your longpress action here
						location = longpressLocation;
						mapView.getController().animateTo(location);
						Bundle bundle = new Bundle();
						bundle.putString("location", location.toString());
						Intent intent = new Intent(Dashboard.this,
								AddAlbum.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
			}
		});
	}

	/**
	 * untuk membuat pilihan menu Help dan Credit
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, HELP, 0, "Help");
		menu.add(0, CREDIT, 0, "Credit");
		menu.add(0, MAPTYPE, 0, "Map Type");
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
		case HELP:
			intent = new Intent(this, Help.class);
			startActivity(intent);
			return true;
		case CREDIT:
			intent = new Intent(this, Credit.class);
			startActivity(intent);
			return true;
		case MAPTYPE:
			int check = 0;
			
			if(mapView.isSatellite()) check = 1; 
			
			final CharSequence[] items = {"Street","Satelite"};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Set Map View");
			builder.setSingleChoiceItems(items, check, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			        switch (item) {
			        case 0:
			        	mapView.setSatellite(false);
						mapView.invalidate();
						break;
			        case 1:
						mapView.setSatellite(true);
						mapView.invalidate();
						break;
					default:
						break;
					}
			        dialog.dismiss();
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/**
	 * Method untuk mengembalikan MapView yang dimiliki kelas ini
	 * 
	 * @return the mapView
	 */
	public DashboardView getMapView() {
		return mapView;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(ACTIVITY_SERVICE, "Panggil Yang Resume");
		// set overlay
		// semua overlay sebelumnya di hilangkan dulu
		mapOverlays.clear();

		Drawable drawablez = getResources().getDrawable(R.drawable.marker_1);
		itemizedOverlay = new CustomItemizedOverlay<Album>(this,drawablez);
		
		Rect bound = drawablez.getBounds();

		db.open();
		Cursor cursor = db.getAlbums();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			album.setAlbumID(db.getAlbumID(cursor));
			album.setAlbumName(db.getAlbumName(cursor));
			album.setAlbumDescription(db.getAlbumDescription(cursor));
			album.setCover(db.getAlbumCover(cursor));
			album.setAlbumLocation(db.getAlbumLocation(cursor));
			album.setMarker(db.getAlbumMarker(cursor));

			String[] latLong = album.getAlbumLocation().split(",");

			GeoPoint location = new GeoPoint(Integer.parseInt(latLong[0]),
					Integer.parseInt(latLong[1]));

			Album overlayItem = new Album(
					album.getAlbumID(), location, album.getAlbumName(),
					album.getAlbumDescription(), album.getCover());

			int marker = 0;

			// Di sini buat definisikan semua resource marker
			// format = pin_<warna>
			if (album.getMarker().equals("")
					|| album.getMarker().equals("Yellow")) {
				marker = R.drawable.marker_1;
			} else if (album.getMarker().equals("Red")) {
				marker = R.drawable.marker_2;
			} else if (album.getMarker().equals("Blue")) {
				marker = R.drawable.marker_3;
			} else if (album.getMarker().equals("Green")) {
				marker = R.drawable.marker_4;
			} else if (album.getMarker().equals("Grey")) {
				marker = R.drawable.marker_5;
			}

			Drawable drawable = getResources().getDrawable(marker);
			drawable.setBounds(bound);
			
			overlayItem.setMarker(drawable);
			itemizedOverlay.addOverlay(overlayItem);

			cursor.moveToNext();
		}

		db.close();
		cursor.close();

		if (itemizedOverlay.size() > 0)
			mapOverlays.add(itemizedOverlay);
		
		mapView.refreshDrawableState();
	}

	/**
	 * Method ini harus diimplementasikan untuk memberikan informasi pada server
	 * mengenai ditampilkan atau tidaknya route pada peta.
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * Method untuk membuka sebuah album
	 * 
	 * @param albumID
	 *            ID milik Album yang akan dibuka
	 * @param albumName
	 *            Nama Album yang akan dibuka
	 * @param location
	 *            Lokasi Album yang akan dibuka
	 */
	public void openAlbum(int albumID, String albumName, String location) {
		Intent intent = new Intent(this, AlbumController.class);
		Bundle b = new Bundle();
		b.putInt("albumID", albumID);
		b.putString("albumName", albumName);
		b.putString("location", location);
		intent.putExtras(b);
		startActivity(intent);
	}

	/**
	 * Method untuk meng-edit sebuah album pada aplikasi galdita
	 * 
	 * @param index
	 *            index dari album dari yang akan dihapus
	 * @param albumid
	 *            id album yang akan dihapus
	 */
	public void EditAlbum(final int index, int albumid) {
		Bundle b = new Bundle();
		b.putInt("albumid", albumid);
		b.putInt("index", index);
		Intent intent = new Intent(this, EditAlbum.class);
		intent.putExtras(b);
		startActivity(intent);
	}

	/**
	 * Method ini dipanggil untuk menampilkan pilihan menu edit
	 * 
	 * @param index
	 * @param albumid
	 */
	public void MenuEdit(final int index, int albumid) {
		Bundle b = new Bundle();
		b.putInt("albumid", albumid);
		b.putInt("index", index);
		Intent intent = new Intent(this, MenuEdit.class);
		intent.putExtras(b);
		startActivity(intent);
	}

	/**
	 * Kelas untuk melakukan scanning media dengan menggunakan thread yang
	 * terpisah agar pengguna tidak terlalu lama menunggu
	 * 
	 * @author Abi Rheza Nasa'i
	 * 
	 */
	private class Scan extends AsyncTask<Context, Void, Void> {
		@Override
		protected Void doInBackground(Context... params) {
			// TODO Auto-generated method stub
			DBGaldita tdb = new DBGaldita(params[0]);
			// spesial karena method dipanggil dalam thread
			// Scanning ALL files in sdcard
			// Scanning ALL images in sdcard
			String[] imageColumns = { MediaColumns.DATA,
					BaseColumns._ID };
			String imageOrderBy = BaseColumns._ID;
			Cursor cursorAllimage = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns,
					null, null, imageOrderBy);
			int temp = cursorAllimage.getCount();
			tdb.open();

			if (temp > 0) {
				for (int i = 0; i < temp; i++) {
					cursorAllimage.moveToPosition(i);
					int dataColumnIndex = cursorAllimage
							.getColumnIndex(MediaColumns.DATA);
					String path = cursorAllimage.getString(dataColumnIndex);
					// cek tipe file di sini, sementara image dulu
					tdb.insertItem(0, path, "", IMAGE);
					if (ds.isFinishing())
						break;
				}
			}

			cursorAllimage.close();

			// scanning all videos in sdcard
			final String[] videoColumns = { MediaColumns.DATA,
					BaseColumns._ID };
			String videoOrderBy = BaseColumns._ID;
			Cursor cursorAllvideo = managedQuery(
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns,
					null, null, videoOrderBy);
			temp = cursorAllvideo.getCount();

			if (temp > 0) {
				for (int i = 0; i < temp; i++) {
					cursorAllvideo.moveToPosition(i);
					int dataColumnIndex = cursorAllvideo
							.getColumnIndex(MediaColumns.DATA);
					String path = cursorAllvideo.getString(dataColumnIndex);
					Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(
							path, MediaStore.Images.Thumbnails.MICRO_KIND);
					if (thumbnail != null)
						tdb.insertItem(0, path, "", VIDEO);
					if (ds.isFinishing())
						break;
				}
			}

			cursorAllvideo.close();
			tdb.close();

			return null;
		}
	}
}