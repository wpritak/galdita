package com.galdita.view;

import com.galdita.R;
import com.galdita.model.Album;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Kelas yang merupakan kustomisasi dari FrameLayout untuk menampilkan overlay
 * item pada peta.
 * 
 * @author Abi Rheza Nasa'i
 * @author Rezky Ramadiansyah
 * 
 * @param <Item>
 */
public class CustomOverlayView<Item extends Album> extends
		FrameLayout {

	// linear layout untuk overlay item
	private LinearLayout layout;

	// Judul Overlay Item yang merupakan nama album
	private TextView title;

	// Snippet Overlay Item yang merupakan deskripsi album
	private TextView snippet;

	// image as cover
	private ImageView image;

	/**
	 * Membuat sebuah BalloonOverlayView.
	 * 
	 * @param context
	 *            - The activity context.
	 * @param balloonBottomOffset
	 *            - The bottom padding (in pixels) to be applied when rendering
	 *            this view.
	 */
	public CustomOverlayView(Context context, int balloonBottomOffset) {
		super(context);
		setPadding(10, 0, 10, balloonBottomOffset);

		layout = new LimitLinearLayout(context);
		layout.setVisibility(VISIBLE);

		setupView(context, layout);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.NO_GRAVITY;

		addView(layout, params);
	}

	/**
	 * Melakukan Inflate dan inisialisasi BalloonOverlayView UI.
	 * 
	 * @param context
	 *            - The activity context.
	 * @param parent
	 *            - The root layout into which you must inflate your view.
	 */
	protected void setupView(Context context, final ViewGroup parent) {

		// inflate our custom layout into parent
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.custom_balloon_overlay, parent);

		// setup our fields
		title = (TextView) v.findViewById(R.id.balloon_item_title);
		snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
		image = (ImageView) v.findViewById(R.id.balloon_item_image);

	}

	/**
	 * Mengeset view data dari overlay item. yang diberikan
	 * 
	 * @param item
	 *            - The overlay item containing the relevant view data.
	 */
	public void setData(Item item) {
		layout.setVisibility(VISIBLE);
		setBalloonData(item, layout);
	}

	/**
	 * Mengeset data view ballon dari overlay item yang diberikan
	 * 
	 * @param item
	 *            - The overlay item containing the relevant view data.
	 * @param parent
	 *            - The parent layout for this BalloonOverlayView.
	 */
	protected void setBalloonData(Album item, ViewGroup parent) {

		// map our custom item data to fields
		title.setText(item.getTitle());
		snippet.setText(item.getSnippet());
		// set cover di sini

		// sementara dimatikan dulu
		if (item.getCover().equals(""))
			image.setImageResource(R.drawable.cover);
		else {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inSampleSize = 10;
			Bitmap bm = ThumbnailUtils
					.extractThumbnail(
							BitmapFactory.decodeFile(item.getCover(), opt),
							96, 96);
			image.setImageBitmap(bm);
		}
	}

	/**
	 * Kelas untuk mengatur layout dari ballon view
	 * 
	 * @author Abi Rheza Nasa'i
	 * 
	 */
	private class LimitLinearLayout extends LinearLayout {

		private static final int MAX_WIDTH_DP = 280;

		final float SCALE = getContext().getResources().getDisplayMetrics().density;

		public LimitLinearLayout(Context context) {
			super(context);
		}

		public LimitLinearLayout(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int mode = MeasureSpec.getMode(widthMeasureSpec);
			int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
			int adjustedMaxWidth = (int) (MAX_WIDTH_DP * SCALE + 0.5f);
			int adjustedWidth = Math.min(measuredWidth, adjustedMaxWidth);
			int adjustedWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
					adjustedWidth, mode);
			super.onMeasure(adjustedWidthMeasureSpec, heightMeasureSpec);
		}
	}
}
