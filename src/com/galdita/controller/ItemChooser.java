package com.galdita.controller;

import java.util.ArrayList;

import com.galdita.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Class ItemsChooser
 * 
 * class ini adalah class bagian controller yang mengatur selection item-item
 * yang yang diperlukan seperti pada penghapusan dan penambahan item
 * 
 * @author Habiburrahman
 * 
 */

public class ItemChooser extends Activity {
	// konstanta yang menunjukkan mode tambah item
	public static final int TAMBAH_ITEM = 0;
	// konstanta yang menunjukkan mode hapus item
	public static final int HAPUS_ITEM = 1;
	// konstanta yang menunjukkan mode ubah cover
	public static final int UBAH_COVER = 2;
	// arraylist untuk menyimpan path yang dipilih
	ArrayList<Integer> itemSelected;
	// variabel count untuk menghitung jumlah item
	private int count;
	// array Bitmap untuk menyimpan thumbnails berupa bitmap
	private Bitmap[] thumbnails;
	// array boolean yang menunjukkan thumbnail yang dipilih 
	private boolean[] thumbnailsselection;
	// imageAdapter untuk mengadapsi gambar
	private ImageAdapter imageAdapter;
	// lastCheckBox, untuk mengetahui check yang terakhir
	CheckBox lastCheckBox;
	// thumbnailSelected untuk mengetahui thumbnail yang dipilih
	private int thumbnailSelected;
	// albumID sebagai id album yang dipilih
	private int albumID;
	// thisAlbumID sebagai id album yang sedang berjalan
	private int thisAlbumID;
	// mode, untuk menyimpan mode yang dipilih
	private int mode;
	// ic, untuk merefer ke objek item chooser yang sedang berjalan
	ItemChooser ic = this;
	// inisialisasi database
	DBGaldita db = new DBGaldita(this);
	// itemIds, menyimpan id-id yang dipilih pada mode UBAH_COVER
	ArrayList<Integer> itemIds;
	// itemPaths, menyimpan path-path yang dipilih pada mode UBAH_COVER
	ArrayList<String> itemPaths;
	// itemsId, menyimpan item-item yang dipilih secara umum
	private int[] itemsId;
	private String[] itemsPath;
	private int[] itemsType;
	private GridView imagegrid;

	/**
	 * onCreate
	 * 
	 * Method ini  merupakan method yang akan di override dan
	 * dipanggil saat activity pertama kali d create
	 * 
	 * @param Bundle
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooser);

		Bundle b = getIntent().getExtras();
		mode = b.getInt("mode");
		thisAlbumID = b.getInt("albumID");
		if (mode == HAPUS_ITEM)
			albumID = thisAlbumID;
		else {
			albumID = 0;
			thumbnailSelected = -1;
		}
		imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
		
		//buat thread baru
		Thread thread = new Thread(new Runnable() {
			int rows;
			public void run() {
				BitmapFactory.Options bmpFacOpt = new BitmapFactory.Options();
				bmpFacOpt.inSampleSize = 10;
				db.open();
				Cursor cursor = db.getItems(albumID);
				rows = cursor.getCount();
				
				// jika mode adalah ubah cover
				if (mode != UBAH_COVER) {
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
					count = rows;
					thumbnails = new Bitmap[count];
					thumbnailsselection = new boolean[count];

					runOnUiThread(new Runnable() {
						public void run() {
							imageAdapter = new ImageAdapter(thumbnails,
									thumbnailsselection);
							imagegrid.setAdapter(imageAdapter);
						}
					});

					for (int i = 0; i < count; i++) {
						if (itemsType[i] == Dashboard.IMAGE) {
							thumbnails[i] = ThumbnailUtils.extractThumbnail(
									BitmapFactory.decodeFile(itemsPath[i],
											bmpFacOpt), 96, 96);
						} else {
							System.out.println("ada video atassss");
							thumbnails[i] = ThumbnailUtils
									.createVideoThumbnail(
											itemsPath[i],
											MediaStore.Images.Thumbnails.MICRO_KIND);
						}
						runOnUiThread(new Runnable() {
							public void run() {
								imageAdapter.notifyDataSetChanged();
							}
						});
					}

				} else { //selain mode ubah cover
					itemIds = new ArrayList<Integer>();
					itemPaths = new ArrayList<String>();
					if (rows != 0) {
						cursor.moveToFirst();
						int type;
						while (!cursor.isAfterLast()) {
							type = db.getItemType(cursor);
							if (type == Dashboard.IMAGE) {
								itemIds.add(db.getItemID(cursor));
								itemPaths.add(db.getItemPath(cursor));
							}
							cursor.moveToNext();
						}
					}
					count = itemIds.size();
					thumbnails = new Bitmap[count];
					thumbnailsselection = new boolean[count];

					runOnUiThread(new Runnable() {
						public void run() {
							imageAdapter = new ImageAdapter(thumbnails,
									thumbnailsselection);
							imagegrid.setAdapter(imageAdapter);
						}
					});

					for (int i = 0; i < count; i++) {
						thumbnails[i] = ThumbnailUtils.extractThumbnail(
								BitmapFactory.decodeFile(itemPaths.get(i),
										bmpFacOpt), 96, 96);

						runOnUiThread(new Runnable() {
							public void run() {
								imageAdapter.notifyDataSetChanged();
							}
						});
					}
				}
				cursor.close();
				db.close();
			}
		});
		// mulai thread
		thread.start();
		
		// inisialisasi button select
		final Button selectBtn = (Button) findViewById(R.id.selectBtn);
		selectBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final ProgressDialog dialog = ProgressDialog.show(
						ItemChooser.this, "", "Loading. Please wait...", true);

				Thread thread = new Thread(new Runnable() {
					public void run() {
						if (mode == UBAH_COVER) {
							if (thumbnailSelected != -1) {
								AddAlbum.albumCover = itemPaths
										.get(thumbnailSelected);
							}
						} else {
							int cnt = 0;
							itemSelected = new ArrayList<Integer>();
							for (int i = 0; i < count; i++) {
								if (thumbnailsselection[i]) {
									itemSelected.add(itemsId[i]);
									cnt++;
								}
							}
							if (cnt == 0) {
							} else {
								db.open();
								if (mode == TAMBAH_ITEM) {
									for (int i = 0; i < itemSelected.size(); i++) {
										db.updateItemAlbumID(
												itemSelected.get(i),
												thisAlbumID);
										Cursor c = db.getItemByID(itemSelected
												.get(i));
										c.moveToFirst();
										String st = db.getItemPath(c);
										System.out.println(st);
										c.close();

									}
								} else if (mode == HAPUS_ITEM) {
									for (int i = 0; i < itemSelected.size(); i++) {
										db.updateItemAlbumID(
												itemSelected.get(i), 0);
										Log.i("ItemChooser", "Delete = "
												+ itemSelected);
									}

								}
								db.close();
							}
						}
						runOnUiThread(new Runnable() {
							public void run() {
								if (dialog.isShowing())
									dialog.dismiss();
								// Kembali ke album
								finish();
							}
						});
					}
				});
				thread.start();
			}
		});
	}

	/**
	 * class ImageAdapter
	 * 
	 * merupakan class yang berfungsi untuk mengatur thumbnails yang
	 * dipilih oleh user
	 * 
	 * @author Habiburrahman
	 *
	 */
	class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Bitmap[] thumbnails;
		private boolean[] thumbnailsselection;

		public ImageAdapter(Bitmap[] thumbnails, boolean[] thumbnailsselection) {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.thumbnails = thumbnails;
			this.thumbnailsselection = thumbnailsselection;
		}

		public int getCount() {
			return count;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}
		
		/**
		 * method getView
		 * method untuk menampilkan view
		 * 
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.chooser_content, null);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.thumbImage);
				holder.checkbox = (CheckBox) convertView
						.findViewById(R.id.itemCheckBox);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkbox.setId(position);
			holder.imageview.setId(position);
			holder.checkbox.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (mode == UBAH_COVER) {
						if (thumbnailSelected != id) {
							cb.setChecked(true);
							thumbnailSelected = id;
							if (lastCheckBox != null)
								lastCheckBox.setChecked(false);
							lastCheckBox = cb;
						}
					} else {
						if (thumbnailsselection[id]) {
							cb.setChecked(false);
							thumbnailsselection[id] = false;
						} else {
							cb.setChecked(true);
							thumbnailsselection[id] = true;
						}
					}
				}
			});
			holder.imageview.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					CheckBox cb = holder.checkbox;
					int id = cb.getId();
					if (mode == UBAH_COVER) {
						if (thumbnailSelected != id) {
							cb.setChecked(true);
							thumbnailSelected = id;
							if (lastCheckBox != null)
								lastCheckBox.setChecked(false);
							lastCheckBox = cb;
						}
					} else {
						if (thumbnailsselection[id]) {
							cb.setChecked(false);
							thumbnailsselection[id] = false;
						} else {
							cb.setChecked(true);
							thumbnailsselection[id] = true;
						}
					}
				}
			});
			holder.imageview.setImageBitmap(thumbnails[position]);
			holder.checkbox.setChecked(thumbnailsselection[position]);
			holder.id = position;
			return convertView;
		}
		
		/**
		 * class ViewHolder
		 * berisi ImageView dan CheckBox serta id
		 * 
		 * @author Habiburrahman
		 *
		 */
		class ViewHolder {
			ImageView imageview;
			CheckBox checkbox;
			int id;
		}
	}
}