package com.galdita.model;

/**
 * Class ini merupakan suatu model yang merepresentasikan suatu item. Item
 * tersebut berupa image atau video.
 * 
 * @author Primaningtyas Sukawati
 * 
 */
public class Item {

	// list absolute path dari item yang tersimpan dalam sebuah album
	String[] listOfPaths;

	// caption dari item yang sedang dilihat
	String caption;

	// id album dimana item disimpan
	int albumid;

	// id dari item yang sedang dilihat
	int itemid;

	// Menampung path item
	String path;

	// Menampung nama item
	String name;

	/**
	 * Method ini akan mengembalikan array yang berisikan paths yang berada
	 * dalam suatu album.
	 * 
	 * @return the items
	 */
	public String[] getListOfPaths() {
		return listOfPaths;
	}

	/**
	 * Mendapatkan path-path dari item yang berada dalam suatu album
	 * 
	 * @param items
	 *            the items to set
	 */
	public void setListOfPaths(String[] listOfPaths) {
		this.listOfPaths = listOfPaths;
	}

	/**
	 * Mendapatkan caption dari item yang sedang dilihat
	 * 
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Mengatur caption agar sesuai dengan input user
	 * 
	 * @param caption
	 *            the caption to set
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * Mendapatkan id dari album dimana item berada
	 * 
	 * @return the albumid
	 */
	public int getAlbumid() {
		return albumid;
	}

	/**
	 * Mengatur id album
	 * 
	 * @param albumid
	 *            the albumid to set
	 */
	public void setAlbumid(int albumid) {
		this.albumid = albumid;
	}

	/**
	 * Mendapatkan path dari item yang sedang dilihat
	 * 
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Mengatur path item yang sedang dilihat
	 * 
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Mendapatkan id dari item yang sedang dilihat
	 * 
	 * @return the itemid
	 */
	public int getItemid() {
		return itemid;
	}

	/**
	 * Mengatur id dari item yang sedang dilihat
	 * 
	 * @param itemid
	 *            the itemid to set
	 */
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}

	/**
	 * Mendapatkan nama item yang sedang dilihat
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Mengatur nama item yang sedang dilihat
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
