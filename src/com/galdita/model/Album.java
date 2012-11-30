package com.galdita.model;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * Kelas yang merepresentasikan objek sebuah Album
 * 
 * @author Primaningtyas Sukawati
 * @author Abi Rheza Nasa'i
 * 
 */
public class Album extends OverlayItem {


	// Menampung ID album
	private int albumID;

	// Menampung Nama album
	private String albumName;

	// Menampung Lokasi album
	private String albumLocation;

	// Menampung Deskripsi album
	private String albumDescription;

	// Menampung Absolut path dari Cover album
	private String cover;

	// Menampung Absolut path dari marker album
	private String marker;
	
	// Menampung lokasi album
	private GeoPoint point;
	
	public Album(){
		super(null, null, null);
	}
	
	/**
	 * Default konstruktor untuk kelas ini, berfungsi juga untuk memberikan
	 * nilai awal untuk atribut-atribut yang dimiliki.
	 * 
	 * @param albumID
	 *            ID album yang akan disimpan
	 * @param point
	 *            lokasi album yang akan disimpan
	 * @param title
	 *            nama album yang akan disimpan
	 * @param snippet
	 *            deskripsi album yang akan disimpan
	 * @param cover
	 *            path cover album yang akan disimpan
	 */
	public Album(int albumID, GeoPoint point, String title,
			String snippet, String cover) {
		super(point, title, snippet);
		setAlbumID(albumID);
		setPoint(point);
		setAlbumName(title);
		setAlbumDescription(snippet);
		this.cover = cover;
	}

	/**
	 * Mengambil path dari cover album yang dipilih
	 * 
	 * @return the cover path
	 */
	public String getCover() {
		return cover;
	}

	/**
	 * Mengatur cover album sesuai dengan image path yang dipilih
	 * 
	 * @param cover
	 *            the cover absolute path to set
	 */
	public void setCover(String cover) {
		this.cover = cover;
	}

	/**
	 * Mendapatkan marker yang sekarang digunakan
	 * 
	 * @return the marker path
	 */
	public String getMarker() {
		return marker;
	}

	/**
	 * Mengatur agar marker sesuai dengan pilihan
	 * 
	 * @param cover
	 *            the marker absolute path to set
	 */
	public void setMarker(String marker) {
		this.marker = marker;
	}

	/**
	 * Mengembalikan deskripsi album
	 * 
	 * @return the album's Description
	 */
	public String getAlbumDescription() {
		return albumDescription;
	}

	/**
	 * Mengatur agar deskripsi sesuai dengan input dari user
	 * 
	 * @param albumDescription
	 *            the album's Description to set
	 */
	public void setAlbumDescription(String albumDescription) {
		this.albumDescription = albumDescription;
	}

	/**
	 * Mendapatkan id dari album
	 * 
	 * @return the album's ID
	 */
	public int getAlbumID() {
		return albumID;
	}

	/**
	 * Mengatur id album
	 * 
	 * @param albumID
	 *            the album's ID to set
	 */
	public void setAlbumID(int albumID) {
		this.albumID = albumID;
	}

	/**
	 * Mendapatkan nama album
	 * 
	 * @return the album's Name
	 */
	public String getAlbumName() {
		return albumName;
	}

	/**
	 * Mengatur nama album sesuai dengan input user
	 * 
	 * @param albumName
	 *            the album's Name to set
	 */
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	/**
	 * Mendapatkan lokasi dari album
	 * 
	 * @return the album's Location
	 */
	public String getAlbumLocation() {
		return albumLocation;
	}

	/**
	 * Mengatur lokasi album
	 * 
	 * @param albumLocation
	 *            the album's Location to set
	 */
	public void setAlbumLocation(String albumLocation) {
		this.albumLocation = albumLocation;
	}
	
	/**
	 * Mengembalikan geopoint
	 * 
	 * @return the point
	 */
	@Override
	public GeoPoint getPoint() {
		return point;
	}

	/**
	 * Mengatur point
	 * 
	 * @param point
	 *            the point to set
	 */
	public void setPoint(GeoPoint point) {
		this.point = point;
	}

}
