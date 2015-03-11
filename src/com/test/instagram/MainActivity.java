package com.test.instagram;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.test.instagram.network.WebInterface;

public class MainActivity extends Activity {

	private JSONObject imageData;
	private GridView gridView;
	private static int TILE_WIDTH = 220;
	int number = 0;
	RequestImagesTask request;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		gridView = (GridView) findViewById(R.id.image_grid_view);

		request = new RequestImagesTask(
		"https://api.instagram.com/v1/tags/selfie/media/recent/?client_id=5f9365e9f1054aa991726d731c65aa02",
				this);
		request.execute();

		context = this;

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		gridView.setNumColumns(metrics.widthPixels / TILE_WIDTH);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				Intent intent = new Intent(MainActivity.this, ImageActivity.class);

				try {

					String url = imageData.getJSONArray("data")
							.getJSONObject(position).getJSONObject("images")
							.getJSONObject("standard_resolution")
							.getString("url");
					intent.putExtra("url", url);
				} catch (JSONException e) {
					intent.putExtra("url", "");
				}

				startActivity(intent);
			}
		});
		gridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		}
	);

	}

	private class RequestImagesTask extends AsyncTask<Void, Void, Void> {
		private String url;
		private Context c;

		public RequestImagesTask(String url, Context c) {
			super();
			this.url = url;
			this.c = c;
		}

		@Override
		protected Void doInBackground(Void... params) {
			imageData = WebInterface.requestWebService(url);
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			gridView.setAdapter(new ImageStreamAdapter(c, imageData, number));
		}

	}

}