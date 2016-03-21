package com.gc.p01_mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.bean.BlackNumberInfo;
import com.gc.p01_mobilesafe.db.dao.BlackNumberDAO;

/**
 * 通讯卫士
 * （分页加载）
 * @author guochang
 * 
 */
public class CallSafeActivity extends Activity {
	private ListView lv_callsafe;
	private BlackNumberDAO dao;
	private List<BlackNumberInfo> list;
	/**
	 * 当前页
	 */
	private int mPageNow = 1;
	/**
	 * 每页展示20条数据
	 */
	private int mPageSize = 20;
	/**
	 * 总页数
	 */
	private int totalPages;
	private CallSafeAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsafe);

		initUI();
		initData();
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		ll_ProgressBar = (LinearLayout) findViewById(R.id.ll_ProgressBar);
		lv_callsafe = (ListView) findViewById(R.id.lv_callsafe);
		tv_showPage = (TextView) findViewById(R.id.tv_showPage);
		et_pageNumber = (EditText) findViewById(R.id.et_pageNumber);
	}

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			// 隐藏进度条
			ll_ProgressBar.setVisibility(View.INVISIBLE);

			tv_showPage.setText(mPageNow + "/" + totalPages);

			adapter = new CallSafeAdapter();
			lv_callsafe.setAdapter(adapter);
		};
	};
	private LinearLayout ll_ProgressBar;
	private TextView tv_showPage;
	private EditText et_pageNumber;

	/**
	 * 初始化数据（异步加载数据）
	 */
	private void initData() {

		new Thread() {

			public void run() {

				dao = new BlackNumberDAO(CallSafeActivity.this);
				int total = dao.getTotalNumber();
				totalPages = total % mPageSize == 0 ? total / mPageSize : total
						/ mPageSize + 1;
				// list = dao.findAll();
				list = dao.findPar(mPageNow, mPageSize);
				handler.sendEmptyMessage(0);
			};
		}.start();

	}

	/**
	 * 适配器
	 * 
	 * @author guochang
	 * 
	 */
	private class CallSafeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(CallSafeActivity.this,
						R.layout.callsafe_list_item, null);
				holder = new ViewHolder();
				holder.tv_number = (TextView) convertView
						.findViewById(R.id.tv_number);
				holder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tv_number.setText(list.get(position).getNumber());
			String mode = list.get(position).getMode();
			if ("1".equals(mode)) {
				mode = "全部拦截（电话+短信）";
			} else if ("2".equals(mode)) {
				mode = "电话拦截";
			} else if ("3".equals(mode)) {
				mode = "短信拦截";
			}
			holder.tv_mode.setText(mode);
			
			final BlackNumberInfo info = list.get(position);
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String number = info.getNumber();
					boolean result = dao.delete(number);
					if(result){
						Toast.makeText(CallSafeActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
						list.remove(info);
						//刷新界面
						adapter.notifyDataSetChanged();
					}else{
						Toast.makeText(CallSafeActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			return convertView;
		}

	}

	static class ViewHolder {
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_delete;
	}

	/**
	 * 上一页
	 * 
	 * @param view
	 */
	public void prePage(View view) {
		if (mPageNow <= 1) {
			Toast.makeText(this, "已经是第一页啦", Toast.LENGTH_SHORT).show();
			return;
		}
		mPageNow--;
		initData();
	}

	/**
	 * 下一页
	 * 
	 * @param view
	 */
	public void nextPage(View view) {
		if (mPageNow >= totalPages) {
			Toast.makeText(this, "已经是最后一页啦", Toast.LENGTH_SHORT).show();
			return;
		}
		mPageNow++;
		initData();
	}

	/**
	 * 跳转到指定页
	 * 
	 * @param view
	 */
	public void jump(View view) {
		String str_pageNumber = et_pageNumber.getText().toString().trim();
		if (TextUtils.isEmpty(str_pageNumber)
				|| !TextUtils.isDigitsOnly(str_pageNumber)) {
			Toast.makeText(this, "请输入正确的页码", Toast.LENGTH_SHORT).show();
			return;
		}
		int number = Integer.parseInt(str_pageNumber);
		if (number > 0 && number <= totalPages) {
			mPageNow = number;
			initData();
		} else {
			Toast.makeText(this, "请输入正确的页码", Toast.LENGTH_SHORT).show();
			return;
		}
	}
}
