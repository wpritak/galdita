package com.galdita.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

/**
 * Kelas ini berfungsi untuk menampilkan peta ke layar dan menangani interaksi
 * yang terjadi pada peta
 * 
 * @author Abi Rheza Nasa'i
 * 
 */
public class DashboardView extends MapView {

	/**
	 * Waktu dalam ms sebelum OnLongpressListener terpicu.
	 */
	static final int LONGPRESS_THRESHOLD = 600;

	/**
	 * Waktu dalam ms sebagai batas toleransi selisih waktu antara kedua tab
	 */
	static final int DOUBLETAB_TOLERANCE = 250;

	/**
	 * Menyimpan informasi pusat peta, untuk mengetahui apabila peta digeser
	 */
	private GeoPoint lastMapCenter;

	/**
	 * Mendefinisikan listener untuk menanggapi long press
	 */
	private DashboardView.OnLongpressListener longpressListener;

	/**
	 * Menghitung lamanya melakukan long press
	 */
	private Timer longpressTimer = new Timer();

	/**
	 * Menyimpan waktu system saat dilakukan sentuhan pertama pada peta,
	 * Digunakan dalam pengecekan Double tap pada peta
	 */
	private long lastTouchTime = -1;

	/**
	 * Mendefinisikan interface untuk interaksi antara MapView dan Map
	 * 
	 * @author Abi Rheza Nasa'i
	 * 
	 */
	public interface OnLongpressListener {
		public void onLongpress(MapView view, GeoPoint longpressLocation);
	}

	/**
	 * Default Constructor untuk kelas ini
	 * 
	 * @param context
	 *            Context asal yang memanggil
	 * @param attrs
	 *            attributset yang diperlukan oleh superclass
	 */
	public DashboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Methos ini dipanggil oleh MapActivity untuk mengeset long press listener
	 * 
	 * @param listener
	 *            listener untuk menangani long press
	 */
	public void setOnLongpressListener(
			DashboardView.OnLongpressListener listener) {
		longpressListener = listener;
	}

	/**
	 * Method ini dipanggil setiap kali user menyentuh peta, menyeret jari pada
	 * peta atau melepaskan sentuhan dari peta.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		handleDoubleclickAndLongpress(event);
		return super.onTouchEvent(event);
	}

	/**
	 * Method ini mengambil MotionEvents dan menentukan apakah double click
	 * dilakukan oleh user atau sebuah long press telah terdeteksi.
	 * 
	 * Untuk Double click method ini akan mengecek selisih waktu dan selisih
	 * jarak antara dua tab. Jika selisihnya melebihi toleransi maka bukan
	 * double click, Selain itu double tab akan memicu zoom in pada map.
	 * 
	 * Untuk Longpress Kelas Timer akan mengeksekusi sebuah TimerTask setelah
	 * waktu yang ditentukan dan timer dimulai ketika sebuah jari menyentuh
	 * layar.
	 * 
	 * Kemudian kita perhatikan pergerakan jari atau pelepasan jari dari layar.
	 * Jika terdapat satu dari event tersebut terjadi sebelum TimerTask
	 * dieksekusi maka long press dibatalkan, selain itu long press event akan
	 * terpicu.
	 * 
	 * @param event
	 *            MotionEvent yang berkaitan
	 */
	private void handleDoubleclickAndLongpress(final MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			final int eventX = (int) event.getX();
			final int eventY = (int) event.getY();

			// Double Click to Zoom in
			long thisTime = System.currentTimeMillis();
			if (thisTime - lastTouchTime < DOUBLETAB_TOLERANCE) {
				// Double tap
				this.getController().zoomInFixing(eventX, eventY);
				lastTouchTime = -1;
			} else {
				// Too slow
				lastTouchTime = thisTime;
			}

			// Finger has touched screen.
			longpressTimer = new Timer();
			longpressTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					GeoPoint longpressLocation = getProjection().fromPixels(
							eventX, eventY);

					/*
					 * Fire the listener. We pass the map location of the
					 * longpress as well, in case it is needed by the caller.
					 */
					longpressListener.onLongpress(DashboardView.this,
							longpressLocation);
				}

			}, LONGPRESS_THRESHOLD);

			lastMapCenter = getMapCenter();
		}

		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (!getMapCenter().equals(lastMapCenter)) {
				// User is panning the map, this is no longpress
				longpressTimer.cancel();
			}
			lastMapCenter = getMapCenter();
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {
			// User has removed finger from map.
			longpressTimer.cancel();
		}
		if (event.getPointerCount() > 1) {
			// This is a multitouch event, probably zooming.
			longpressTimer.cancel();
		}
	}
}