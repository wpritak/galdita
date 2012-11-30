package com.galdita.controller;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.Cursor;

/**
 * Class DBGaldita ini adalah kelas yang mengatur penggunaan basis data dalam
 * Aplikasi Galdita.
 * 
 * @author Rezky Ramadiansyah
 * 
 */
public class DBGaldita {
	public static final String DB_NAME = "galdita.db";

	public static final String DB_TABLE1 = "album";
	public static final String ALBUM_ID = "_id";
	public static final String ALBUM_NAME = "name";
	public static final String ALBUM_DESC = "description";
	public static final String ALBUM_COOR = "coordinates";
	public static final String ALBUM_COVER = "albumcover";
	public static final String ALBUM_MARKER = "albummarker";

	public static final String DB_TABLE2 = "item";
	public static final String ITEM_ID = "_id";
	public static final String IALBUM_ID = "album_id";
	public static final int UNASSIGNED = 0;
	public static final String FILE_PATH = "file_path";
	public static final String CAPTION = "caption";
	public static final String FILE_TYPE = "type";

	private SQLiteDatabase db;
	private DBGalditaHelper DBHelper;
	private final Context context;

	/**
	 * Constructor
	 * 
	 * @param ctx
	 */
	public DBGaldita(Context ctx) {
		this.context = ctx;
		DBHelper = new DBGalditaHelper(context);
	}

	/**
	 * Method untuk membuka database yang ada. Sebelum mengakses database harus
	 * memanggil method ini terlebih dahulu
	 * 
	 * @return void
	 */
	public void open() throws SQLException {
		db = DBHelper.getWritableDatabase();
	}

	/**
	 * Method untuk menutup database. Setelah selesai mengakses database, harus
	 * memanggil method ini.
	 * 
	 * @return void
	 */
	public void close() {
		DBHelper.close();
	}

	/**
	 * Method untuk mengosongkan isi database dan membuat ulang database dari
	 * awal.
	 * 
	 * @return void
	 */
	public void reset() {
		DBHelper.onUpgrade(db, 1, 2);
	}

	// method2 untuk query terkait ALBUM
	/**
	 * Method untuk menambahkan suatu album ke dalam database.
	 * 
	 * @param name
	 *            String nama album
	 * @param description
	 *            String deskripsi album
	 * @param location
	 *            String lokasi/koordinat album pada peta
	 * @param coverpath
	 *            String path dari berkas yang menjadi cover album
	 * 
	 * @return boolean apakah album berhasil diditambahkan atau tidak
	 */
	public boolean insertAlbum(String name, String description,
			String location, String coverpath, String markerpath)
			throws SQLiteConstraintException {
		ContentValues insertValues = new ContentValues();
		insertValues.put(ALBUM_NAME, name);
		insertValues.put(ALBUM_DESC, description);
		insertValues.put(ALBUM_COOR, location);
		insertValues.put(ALBUM_COVER, coverpath);
		insertValues.put(ALBUM_MARKER, markerpath);
		return db.insert(DB_TABLE1, null, insertValues) > 0;
	}

	/**
	 * Method untuk menghapus album dari database berdasarkan id album.
	 * 
	 * @param id
	 *            int ID dari album yang ingin dihapus
	 * 
	 * @return boolean apakah album berhasil dihapus atau tidak
	 */
	public boolean deleteAlbum(int id) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(IALBUM_ID, UNASSIGNED);
		db.update(DB_TABLE2, updateValues, IALBUM_ID + "=" + id, null);
		return db.delete(DB_TABLE1, ALBUM_ID + "=" + id, null) > 0;
	}

	/**
	 * Method untuk menghapus album dari database berdasarkan nama dan lokasi
	 * album
	 * 
	 * @param name
	 *            String nama album yang ingin dihapus
	 * @param location
	 *            String lokasi album yang ingin dihapus
	 * 
	 * @return boolean apakah album berhasil dihapus atau tidak
	 */
	public boolean deleteAlbum(String name, String location) {
		Cursor c = getAlbumFromMap(name, location);
		c.moveToFirst();
		int id = getAlbumID(c);
		c.close();
		return deleteAlbum(id);
	}

	/**
	 * Method untuk memperbarui cover album.
	 * 
	 * @param id
	 *            int ID album yang ingin diperbarui cover-nya
	 * @param newCover
	 *            String path berkas yang akan menjadi cover baru
	 * 
	 * @return boolean cover album berhasil diperbarui atau tidak
	 */
	public boolean updateAlbumCover(int id, String newCover) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(ALBUM_COVER, newCover);
		return db.update(DB_TABLE1, updateValues, ALBUM_ID + "=" + id, null) > 0;
	}

	/**
	 * Method untuk memperbarui nama album.
	 * 
	 * @param id
	 *            int ID album yang ingin diperbarui namanya
	 * @param newName
	 *            String nama baru untuk album tersebut
	 * 
	 * @return boolean nama album berhasil diperbarui atau tidak
	 */
	public boolean updateAlbumName(int id, String newName) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(ALBUM_NAME, newName);
		return db.update(DB_TABLE1, updateValues, ALBUM_ID + "=" + id, null) > 0;
	}

	/**
	 * Method untuk memperbarui deskripsi album.
	 * 
	 * @param id
	 *            int ID album yang ingin diperbarui deskripsinya
	 * @param newDesc
	 *            String deskripsi baru untuk album tersebut
	 * 
	 * @return boolean deskripsi album berhasil diperbarui atau tidak
	 */
	public boolean updateAlbumDescription(int id, String newDesc) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(ALBUM_DESC, newDesc);
		return db.update(DB_TABLE1, updateValues, ALBUM_ID + "=" + id, null) > 0;
	}

	/**
	 * Method untuk memperbarui marker album.
	 * 
	 * @param id
	 *            int ID album yang ingin diperbarui markernya
	 * @param newMarker
	 *            String deskripsi baru untuk album tersebut
	 * 
	 * @return boolean marker album berhasil diperbarui atau tidak
	 */
	public boolean updateAlbumMarker(int id, String newMarker) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(ALBUM_MARKER, newMarker);
		return db.update(DB_TABLE1, updateValues, ALBUM_ID + "=" + id, null) > 0;
	}

	/**
	 * Method untuk mendapatkan semua album yang ada pada database.
	 * 
	 * @return Cursor tabel hasil query yang berisi semua album
	 */
	public Cursor getAlbums() throws SQLException {
		return db.query(DB_TABLE1, null, null, null, null, null, null);
	}

	/**
	 * Method untuk mendapatkan suatu album berdasarkan ID album.
	 * 
	 * @param id
	 *            int ID album yang dicari
	 * 
	 * @return Cursor berisi tabel hasil query yang berisi album yang sesuai
	 */
	public Cursor getAlbumByID(int id) throws SQLException {
		return db.rawQuery("SELECT * FROM " + DB_TABLE1 + " " + "WHERE "
				+ ALBUM_ID + "= ?", new String[] { Integer.toString(id) });
	}

	/**
	 * Method untuk mendapatkan suatu album berdasarkan nama album dan lokasi
	 * pada peta.
	 * 
	 * @param albumName
	 *            String nama album yang dicari
	 * @param location
	 *            String lokasi album pada peta
	 * 
	 * @return Cursor berisi tabel hasil query yang berisi album yang sesuai
	 */
	public Cursor getAlbumFromMap(String albumName, String location)
			throws SQLException {
		Cursor cursor = db.rawQuery("SELECT * FROM " + DB_TABLE1 + " "
				+ "WHERE " + ALBUM_NAME + "= ? AND " + ALBUM_COOR + "= ?",
				new String[] { albumName, location });
		return cursor;
	}

	/**
	 * Method untuk mendapatkan ID album dari suatu record pada hasil query.
	 * 
	 * @param cursor
	 *            Cursor berisi tabel hasil query
	 * 
	 * @return int ID album pada record yang ditunjuk cursor
	 */
	public int getAlbumID(Cursor cursor) {
		return cursor.getInt(0);
	}

	/**
	 * Method untuk mendapatkan nama album dari suatu record pada hasil query.
	 * 
	 * @param cursor
	 *            Cursor berisi tabel hasil query
	 * 
	 * @return String nama album pada record yang ditunjuk cursor
	 */
	public String getAlbumName(Cursor cursor) {
		return cursor.getString(1);

	}

	/**
	 * Method untuk mendapatkan deskripsi album dari suatu record pada hasil
	 * query.
	 * 
	 * @param cursor
	 *            Cursor berisi tabel hasil query
	 * 
	 * @return String deskripsi album pada record yang ditunjuk cursor
	 */
	public String getAlbumDescription(Cursor cursor) {
		return cursor.getString(2);
	}

	/**
	 * Method untuk mendapatkan lokasi album pada peta dari suatu record pada
	 * hasil query.
	 * 
	 * @param cursor
	 *            Cursor berisi tabel hasil query
	 * 
	 * @return String lokasi/koordinat album pada record yang ditunjuk cursor
	 */
	public String getAlbumLocation(Cursor cursor) {
		return cursor.getString(3);
	}

	/**
	 * Method untuk mendapatkan path cover album dari suatu record pada hasil
	 * query.
	 * 
	 * @param cursor
	 *            Cursor berisi tabel hasil query
	 * 
	 * @return String path cover album pada record yang ditunjuk cursor
	 */
	public String getAlbumCover(Cursor cursor) {
		return cursor.getString(4);
	}

	/**
	 * Method untuk mendapatkan marker album dari suatu record pada hasil query.
	 * 
	 * @param cursor
	 *            Cursor berisi tabel hasil query
	 * 
	 * @return String path marker album pada record yang ditunjuk cursor
	 */
	public String getAlbumMarker(Cursor cursor) {
		return cursor.getString(5);
	}

	// method2 untuk query terkait ITEM
	/**
	 * Method untuk menambahkan suatu item ke dalam database.
	 * 
	 * @param albumID
	 *            int ID album di mana item berada
	 * @param filepath
	 *            String path berkas terkait item
	 * @param caption
	 *            String caption item
	 * 
	 * @return boolean apakah item berhasil ditambahkan atau tidak
	 */
	public boolean insertItem(int albumID, String filepath, String caption,
			int type) throws SQLiteConstraintException {
		System.out.println("Masuk Insert");
		// mendapatkan item-item yang tidak terassigned
		String[] items = getPathItemsFromAlbumID(UNASSIGNED);
		int i = 0;
		boolean unassigned = false;
		ContentValues insertValues = new ContentValues();
		insertValues.put(IALBUM_ID, Integer.toString(albumID));
		insertValues.put(FILE_PATH, filepath);
		insertValues.put(CAPTION, caption);
		insertValues.put(FILE_TYPE, Integer.toString(type));
		// cek apakah item terassigned atau tidak
		if (items != null) {
			while (i < items.length) {
				unassigned = filepath.equals(items[i]);
				if (unassigned == true)
					break;
				i++;
			}
			// jika sudah ada di table item, cukup update album id
			if (unassigned == true) {
				ContentValues updateValues = new ContentValues();
				updateValues.put(IALBUM_ID, albumID);
				Cursor cursor = getSingleItem(UNASSIGNED, filepath);
				cursor.moveToFirst();
				int itemid = getItemID(cursor);
				cursor.close();
				return db.update(DB_TABLE2, updateValues, ITEM_ID + "="
						+ itemid, null) > 0;
			}
			// jika belum ada, buat record baru
			else {
				return db.insert(DB_TABLE2, null, insertValues) > 0;
			}
		} else {
			// jika belum ada item sama sekali, buat record baru
			return db.insert(DB_TABLE2, null, insertValues) > 0;
		}
	}

	/**
	 * Method untuk meng"hapus" suatu item dari database. Record item sebenarnya
	 * tidak dihapus, melainkan mengperbarui album ID item ke ID album yang
	 * UNASSIGNED
	 * 
	 * @param id
	 *            int ID item yang ingin di"hapus"
	 * 
	 * @return boolean apakah item berhasil di"hapus" atau tidakk
	 */
	public boolean deleteItem(int id) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(IALBUM_ID, UNASSIGNED);
		return db.update(DB_TABLE2, updateValues, ITEM_ID + "=" + id, null) > 0;
	}

	/**
	 * Method untuk meng"hapus" suatu item dari database berdasarkan ID album
	 * dan path berkas item tersebut. Record item sebenarnya tidak dihapus,
	 * melainkan mengperbarui album ID item ke ID album yang UNASSIGNED
	 * 
	 * @param albumid
	 *            int ID album di mana item berada
	 * @param filepath
	 *            String path berkas terkait item
	 * 
	 * @return boolean apakah item berhasil di"hapus" atau tidak
	 */
	public boolean deleteItemByPath(int albumid, String filepath) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(IALBUM_ID, UNASSIGNED);
		return db.update(DB_TABLE2, updateValues, FILE_PATH + "=" + filepath,
				null) > 0;
	}

	/**
	 * Method untuk menghapus suatu item dari database. Digunakan untuk membuang
	 * item yang path nya sudah tidak valid dari database.
	 * 
	 * @param filepath
	 *            String path berkas terkait item
	 * 
	 * @return boolean apakah item berhasil dihapus atau tidak
	 */
	public boolean deleteObsoleteItem(String filepath) {
		return db.delete(DB_TABLE2, FILE_PATH + "= ?",
				new String[] { filepath }) > 0;
	}

	/**
	 * Method untuk memperbarui caption sebuah item.
	 * 
	 * @param id
	 *            int ID item yang ingin diperbarui caption-nya
	 * @param newCaption
	 *            String caption baru untuk item tersebut
	 * 
	 * @return boolean caption item tersebut berhasil diperbarui atau tidak
	 */
	public boolean updateItemCaption(int id, String newCaption) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(CAPTION, newCaption);
		return db.update(DB_TABLE2, updateValues, ITEM_ID + "=" + id, null) > 0;
	}

	/**
	 * Method untuk mengupdate album id suatu item.
	 * 
	 * @param id
	 *            int id item
	 * @param newAlbumId
	 *            int id album baru
	 * 
	 * @return boolean query berhasil atau tidak
	 */
	public boolean updateItemAlbumID(int id, int newAlbumId) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(IALBUM_ID, newAlbumId);
		return db.update(DB_TABLE2, updateValues, ITEM_ID + "=" + id, null) > 0;
	}

	/**
	 * Method untuk mendapatkan semua item dalam suatu album.
	 * 
	 * @param albumID
	 *            int album di mana item-item berada
	 * 
	 * @return Cursor berisi hasil query
	 */
	public Cursor getItems(int albumID) throws SQLException {
		return db
				.rawQuery("SELECT * FROM " + DB_TABLE2 + " " + "WHERE "
						+ IALBUM_ID + "= ?",
						new String[] { Integer.toString(albumID) });
	}

	/**
	 * Method untuk mendapatkan jumlah item dalam suatu album.
	 * 
	 * @param albumID
	 *            int album di mana item-item berada
	 * 
	 * @return Cursor berisi hasil query
	 */
	public Cursor getJumlahItems(int albumID) throws SQLException {
		return db
				.rawQuery("SELECT COUNT(*) FROM " + DB_TABLE2 + " " + "WHERE "
						+ IALBUM_ID + "= ?",
						new String[] { Integer.toString(albumID) });
	}

	/**
	 * Method untuk mendapatkan suatu item berdasarkan ID-nya.
	 * 
	 * @param id
	 *            int ID item yang dicari
	 * 
	 * @return Cursor berisi hasil query, yaitu item yang dicari
	 */
	public Cursor getItemByID(int id) throws SQLException {
		return db.rawQuery("SELECT * FROM " + DB_TABLE2 + " " + "WHERE "
				+ ITEM_ID + "= ?", new String[] { Integer.toString(id) });
	}

	/**
	 * Method untuk mendapatkan suatu item berdasarkan album ID dan path
	 * berkasnya.
	 * 
	 * @param albumID
	 *            int ID album tempat item berada
	 * @param filpath
	 *            String path berkas item yang dimaksud
	 * 
	 * @return Cursor berisi hasil query, yaitu item yang dicari
	 */
	public Cursor getSingleItem(int albumID, String filepath) {
		Cursor cursor = db.rawQuery("SELECT * FROM " + DB_TABLE2 + " "
				+ "WHERE " + IALBUM_ID + "= ? AND " + FILE_PATH + "= ?",
				new String[] { Integer.toString(albumID), filepath });
		return cursor;
	}

	/**
	 * Method untuk mendapatkan path dari semua item dalam album
	 * 
	 * @param id
	 *            int ID album tempat item-item berada
	 * 
	 * @return String[] berisi path-path berkas item dalam album
	 */
	public String[] getPathItemsFromAlbumID(int id) {
		Cursor cursor = getItems(id);
		String[] imagePaths = null;
		int rows = cursor.getCount();

		if (rows != 0) {
			int index = 0;
			imagePaths = new String[rows];
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				imagePaths[index] = getItemPath(cursor);
				cursor.moveToNext();
				index++;
			}
		}
		cursor.close();
		return imagePaths;
	}

	/**
	 * Method untuk mendapatkan semua item yang ada dalam database.
	 * 
	 * @return Cursor berisi hasil query, yaitu semua item
	 */
	public Cursor getAllItem() throws SQLException {
		return db.query(DB_TABLE2, null, null, null, null, null, null);
	}

	/**
	 * Method untuk mendapatkan ID item dari suatu record pada hasil query.
	 * 
	 * @param cursor
	 *            Cursor berisi tabel hasil query
	 * 
	 * @return int ID item pada record yang ditunjuk cursor
	 */
	public int getItemID(Cursor cursor) {
		return cursor.getInt(0);
	}

	/**
	 * Method untuk mendapatkan ID album dari suatu record (item) pada hasil
	 * query.
	 * 
	 * @param cursor
	 *            Cursor berisi tabel hasil query, yaitu record- record item
	 * 
	 * @return int ID album pada record yang ditunjuk cursor
	 */
	public int getItemParent(Cursor cursor) {
		return cursor.getInt(1);
	}

	/**
	 * Method untuk mendapatkan path berkas dari suatu record pada hasil query.
	 * 
	 * @param cursor
	 *            Cursor berisi tabel hasil query
	 * 
	 * @return String path berkas pada record yang ditunjuk cursor
	 */
	public String getItemPath(Cursor cursor) {
		return cursor.getString(2);
	}

	/**
	 * Method untuk mendapatkan caption milik item dari suatu record pada hasil
	 * query.
	 * 
	 * @param cursor
	 *            Cursor berisi tabel hasil query
	 * 
	 * @return String caption milik item pada record yang ditunjuk cursor
	 */
	public String getItemCaption(Cursor cursor) {
		return cursor.getString(3);
	}

	/**
	 * Method untuk mendapatkan ti[e item dari suatu record pada hasil query.
	 * 
	 * @param cursor
	 *            Cursor berisi tabel hasil query
	 * 
	 * @return String tipe item pada record yang ditunjuk cursor
	 */
	public int getItemType(Cursor cursor) {
		return cursor.getInt(4);
	}

	/**
	 * Method untuk menghapus semua isi database
	 * 
	 * @return boolean apakah database berhasil dikosongkan atau tidak
	 */
	public boolean resetDatabase() {
		return (db.delete(DB_TABLE1, null, null) > 0)
				&& (db.delete(DB_TABLE2, null, null) > 0);
	}

	/**
	 * Method untuk mencoba melakukan query pada database secara manual
	 * 
	 * @param query
	 *            String query yang dimasukkan manual
	 * 
	 * @return Cursor berisi tabel hasil query
	 */
	public Cursor rawQuery(String query) {
		Cursor c = null;
		try {
			c = db.rawQuery(query, null);
		} catch (Exception e) {
		}
		return c;
	}

	// class database helper
	private static class DBGalditaHelper extends SQLiteOpenHelper {
		// Constructor DBGalditaHelper
		public DBGalditaHelper(Context context) {
			super(context, DB_NAME, null, 1);
		}

		// Menthod yang menciptakan struktur database
		// Membuat tabel-tabel dan constraint dalam database
		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql1 = "create table " + DB_TABLE1 + " "
					+ "(_id integer primary key autoincrement,"
					+ "name text not null," + "description text,"
					+ "coordinates text not null," + "albumcover text,"
					+ "albummarker text," + "UNIQUE(coordinates));";
			db.execSQL(sql1);

			String sql2 = "create table " + DB_TABLE2 + " "
					+ "(_id integer primary key autoincrement,"
					+ "album_id integer," + "file_path text," + "caption text,"
					+ "type integer not null," + "UNIQUE(file_path));";
			db.execSQL(sql2);
		}

		// Method untuk memperbarui database
		// Namun dalam hal ini menghapus seluruh isi database dan tabel-
		// tabelnya, lalu membuat database dengan struktur yang sama
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
			db.execSQL("drop table if exists " + DB_TABLE1 + ";");
			db.execSQL("drop table if exists " + DB_TABLE2 + ";");
			onCreate(db);
		}
	}
}
