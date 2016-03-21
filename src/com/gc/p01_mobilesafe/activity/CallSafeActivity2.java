package com.gc.p01_mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
 * 通讯卫士 （分批加载）
 * 
 * @author guochang
 * 
 */
public class CallSafeActivity2 extends Activity {
	private ListView lv_callsafe;
	private BlackNumberDAO dao;
	private List<BlackNumberInfo> list;
	/**
	 * 开始的位置
	 */
	private int mStartIndex = 0;
	/**
	 * 每页展示20条数据
	 */
	private int mCount = 20;
	private int totalNumber;
	private CallSafeAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsafe2);

		initUI();
		initData();
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		ll_ProgressBar = (LinearLayout) findViewById(R.id.ll_ProgressBar);
		lv_callsafe = (ListView) findViewById(R.id.lv_callsafe);

		// 设置ListView的滚动监听
		lv_callsafe.setOnScrollListener(new OnScrollListener() {
			// 状态改变时回调的方法
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				// 闲置状态 SCROLL_STATE_IDLE
				// 惯性滚动状态 SCROLL_STATE_FLING
				// 触摸滑动状态 SCROLL_STATE_TOUCH_SCROLL
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 获取最后一条显示的数据
					int lastVisiblePosition = lv_callsafe
							.getLastVisiblePosition();
					if (lastVisiblePosition + 1 >= totalNumber) {
						Toast.makeText(CallSafeActivity2.this, "已经没有数据了",
								Toast.LENGTH_SHORT).show();
						return;
					} else if (lastVisiblePosition + 1 == list.size()
							&& lastVisiblePosition + 1 != totalNumber) {
						mStartIndex += mCount;
						initData();
					}
					break;

				default:
					break;
				}
			}

			// ListView滚动时回调的方法（实时调用）
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			// 隐藏进度条
			ll_ProgressBar.setVisibility(View.INVISIBLE);

			//滑动加载数据时条目不会再次回到第一行
			if(adapter == null){
				adapter = new CallSafeAdapter();
				lv_callsafe.setAdapter(adapter);
			}else{
				adapter.notifyDataSetChanged();
			}
		};
	};
	private LinearLayout ll_ProgressBar;

	/**
	 * 初始化数据（异步加载数据）
	 */
	private void initData() {
		dao = new BlackNumberDAO(CallSafeActivity2.this);
		new Thread() {

			public void run() {
				totalNumber = dao.getTotalNumber();

				// 分批加载数据、追加数据
				if (list == null) {
					list = dao.findPar2(mStartIndex, mCount);
				} else {
					list.addAll(dao.findPar2(mStartIndex, mCount));
				}

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
				convertView = View.inflate(CallSafeActivity2.this,
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
					if (result) {
						Toast.makeText(CallSafeActivity2.this, "删除成功",
								Toast.LENGTH_SHORT).show();
						list.remove(info);
						// 刷新界面
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(CallSafeActivity2.this, "删除失败",
								Toast.LENGTH_SHORT).show();
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
	 * 添加黑名单号码(按钮)
	 * 
	 * @param view
	 */
	public void addBlackNumber(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        
        View dialog_view = View.inflate(this, R.layout.dialog_add_blacknumber, null);
        
        final EditText et_number = (EditText) dialog_view.findViewById(R.id.et_add_number);
        Button btn_ok = (Button) dialog_view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) dialog_view.findViewById(R.id.btn_cancel);
        final CheckBox cb_phone = (CheckBox) dialog_view.findViewById(R.id.cb_phone);
        final CheckBox cb_sms = (CheckBox) dialog_view.findViewById(R.id.cb_sms);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_number = et_number.getText().toString().trim();
                if(TextUtils.isEmpty(str_number)){
                  Toast.makeText(CallSafeActivity2.this,"请输入黑名单号码",Toast.LENGTH_SHORT).show();
                    return;
                }

                String mode = "";

                if(cb_phone.isChecked()&& cb_sms.isChecked()){
                    mode = "1";
                }else if(cb_phone.isChecked()){
                    mode = "2";
                }else if(cb_sms.isChecked()){
                    mode = "3";
                }else{
                    Toast.makeText(CallSafeActivity2.this,"请勾选拦截模式",Toast.LENGTH_SHORT).show();
                    return;
                }
                BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                blackNumberInfo.setNumber(str_number);
                blackNumberInfo.setMode(mode);
                //把新添加的数据放在链表第一个位置
                list.add(0,blackNumberInfo);
                //把电话号码和拦截模式添加到数据库。
                dao.add(str_number,mode);

                if(adapter == null){
                	adapter = new CallSafeAdapter();
        			lv_callsafe.setAdapter(adapter);
                }else{
                    adapter.notifyDataSetChanged();
                }

                dialog.dismiss();
            }
        });
        dialog.setView(dialog_view);
        dialog.show();
	}

}
