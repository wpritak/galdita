package com.galdita.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
//import android.widget.Toast;

import com.galdita.R;
import com.galdita.controller.Dashboard;
import com.galdita.view.CustomOverlayView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Kelas untuk mengontrol overlay item dan berfungsi juga untuk berinteraksi
 * dengan peta.
 * 
 * @author Abi Rheza Nasa'i
 * @author Rezky Ramadiansyah
 * 
 * @param <CustomOverlayItem>
 */
public class CustomItemizedOverlay<CustomOverlayItem extends Album>
		extends ItemizedOverlay<CustomOverlayItem> {
	
	// MapController milik dashboard
	private final MapController mc;
	
	// MapView yang digunakan pada dashboard
	private MapView mapView;
	
	// View yang menampilkan ballon saat overlay item di tekan
	private CustomOverlayView<Album> balloonView;
	
	// area yang akan menanggapi suatu click pada ballon
	private View clickRegion;
	
	// area yang akan menanggapi perintah hide pada ballon
	private View closeRegion;
	
	// jarak antara marker dan ballon secara horisontal
	private int viewOffset;
	
	// Item yang sedang menjadi fokus saat ini
	private CustomOverlayItem currentFocusedItem;
	
	// indeks Item yang sedang menjadi fokus saat ini
	private int currentFocusedIndex;
	
	// Objek dashboard untuk aplikasi ini
	private Dashboard dashboard;
	
	// List custom item yang akan ditampilkan pada peta
	private ArrayList<CustomOverlayItem> m_overlays = new ArrayList<CustomOverlayItem>();
	
	// Sebagai penanda apakah terjadi longpress
	private boolean isLongPressed;

	public CustomItemizedOverlay(Dashboard dashboard, Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
		viewOffset = 0;
		this.dashboard = dashboard;
		mapView = this.dashboard.getMapView();
		mc = mapView.getController();
	}

	/**
	 * Mengeset jarak horisontal antara marker dan tepi bawah dari ballon
	 * informasi.
	 * 
	 * @param pixels
	 *            - The padding between the center point and the bottom of the
	 *            information balloon.
	 */
	public void setBalloonBottomOffset(int pixels) {
		viewOffset = pixels;
	}

	/**
	 * Mengembalikan offset bawah dari ballon
	 * 
	 * @return offset bawah dari ballon
	 */
	public int getBalloonBottomOffset() {
		return viewOffset;
	}

	/**
	 * Method yang dilakukan jika ballon informasi di tab
	 * 
	 * @param item
	 *            CustomOverlayItem yang ballonnya di tab
	 */
	protected void onBalloonTap(CustomOverlayItem item) {
		// Open Album
		dashboard.openAlbum(item.getAlbumID(), item.getAlbumName(), item
				.getPoint().toString());
		hideBalloon();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
	@Override
	// protected final boolean onTap(int index) {
	public final boolean onTap(int index) {
		isLongPressed = false;
		currentFocusedIndex = index;
		currentFocusedItem = createItem(index);
		setLastFocusedIndex(index);

		createAndDisplayBalloonOverlay();

		mc.animateTo(currentFocusedItem.getPoint());

		return true;
	}

	/**
	 * Method untuk membuat sebuah OverlayView
	 * 
	 * @return sebuah CustomOverlayView
	 */
	protected CustomOverlayView<Album> createBalloonOverlayView() {
		// use our custom balloon view with our custom overlay item type:
		return new CustomOverlayView<Album>(getMapView()
				.getContext(), getBalloonBottomOffset());
	}

	/**
	 * Mengexpose map view kepada subclasses. Membantu dalam pembuatan balloon
	 * views.
	 */
	protected MapView getMapView() {
		return mapView;
	}

	/**
	 * Mengeset visibilitas ballon view overlay ini menjadi hilang dan
	 * menghilangkan fokus pada item tertentu
	 */
	public void hideBalloon() {
		if (balloonView != null) {
			balloonView.setVisibility(View.GONE);
		}
		currentFocusedItem = null;
	}

	/**
	 * Menyembunyikan balloon view selain pada item yang menjadi fokus saat ini
	 * 
	 * @param overlays
	 *            - list of overlays (including this) on the MapView.
	 */
	private void hideOtherBalloons(List<Overlay> overlays) {
		for (Overlay overlay : overlays) {
			if (overlay instanceof CustomItemizedOverlay<?> && overlay != this) {
				((CustomItemizedOverlay<?>) overlay).hideBalloon();
			}
		}

	}

	/**
	 * Mengeset onTouchListener untuk ballon yang sedang ditampilkan, method ini
	 * akan memanggil method overridden {@link #onBalloonTap}.
	 */
	private OnTouchListener createBalloonTouchListener() {
		return new OnTouchListener() {

			float startX;
			float startY;

			public boolean onTouch(View v, MotionEvent event) {

				View p = ((View) v.getParent())
						.findViewById(R.id.balloon_main_layout);
				Drawable d = p.getBackground();

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					System.currentTimeMillis();

					int[] states = { android.R.attr.state_pressed };
					if (d.setState(states)) {
						d.invalidateSelf();
					}
					startX = event.getX();
					startY = event.getY();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					int newStates[] = {};
					if (d.setState(newStates)) {
						d.invalidateSelf();
					}

					if (isLongPressed == false
							&& Math.abs(startX - event.getX()) < 40
							&& Math.abs(startY - event.getY()) < 40) {
						// call overridden method
						onBalloonTap(currentFocusedItem);
					}
				}
				return false;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.ItemizedOverlay#getFocus()
	 */
	@Override
	public CustomOverlayItem getFocus() {
		return currentFocusedItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.ItemizedOverlay#setFocus(Item)
	 */
	@Override
	public void setFocus(CustomOverlayItem item) {
		super.setFocus(item);
		currentFocusedIndex = getLastFocusedIndex();
		currentFocusedItem = item;
		if (currentFocusedItem == null) {
			hideBalloon();
		} else {
			createAndDisplayBalloonOverlay();
		}
	}

	/**
	 * Membuat dan menampilkan balloon overlay dengan menggunakan ulang ballon
	 * yang ada atau menciptakannya dari xml
	 * 
	 * @return true if the balloon was recycled false otherwise
	 */
	private boolean createAndDisplayBalloonOverlay() {
		boolean isRecycled;
		if (balloonView == null) {
			balloonView = createBalloonOverlayView();
			clickRegion = balloonView
					.findViewById(R.id.balloon_inner_layout);
			clickRegion.setOnTouchListener(createBalloonTouchListener());
			clickRegion.setOnLongClickListener(new OnLongClickListener() {

				public boolean onLongClick(View v) {
					isLongPressed = true;
					Log.d("LOL", "longpress LP = " + isLongPressed);
					dashboard.MenuEdit(currentFocusedIndex, currentFocusedItem.getAlbumID());
					hideBalloon();
					return true;
				}
			});
			closeRegion = balloonView.findViewById(R.id.balloon_close);
			if (closeRegion != null) {
				closeRegion.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						hideBalloon();
					}
				});
			}
			isRecycled = false;
		} else {
			isRecycled = true;
		}

		balloonView.setVisibility(View.GONE);

		List<Overlay> mapOverlays = mapView.getOverlays();
		if (mapOverlays.size() > 1) {
			hideOtherBalloons(mapOverlays);
		}

		if (currentFocusedItem != null)
			balloonView.setData(currentFocusedItem);

		GeoPoint point = currentFocusedItem.getPoint();
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;

		balloonView.setVisibility(View.VISIBLE);

		if (isRecycled) {
			balloonView.setLayoutParams(params);
		} else {
			mapView.addView(balloonView, params);
		}

		return isRecycled;
	}

	/**
	 * Method untuk menambahkan overlay item pada peta
	 * 
	 * @param overlay
	 */
	public void addOverlay(CustomOverlayItem overlay) {
		m_overlays.add(overlay);
		populate();
	}

	/**
	 * Method untuk menghapus sebuah overlay item pada index tertentu
	 * 
	 * @param index
	 */
	public void removeOverlay(int index) {
		m_overlays.remove(index);
		populate();
	}

	@Override
	protected CustomOverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

}
