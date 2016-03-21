package com.gc.p01_mobilesafe.activity;

import com.gc.p01_mobilesafe.R;
import com.gc.p01_mobilesafe.utils.MD5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主界面
 * 
 * @author guochang
 * 
 */
public class HomeActivity extends Activity {

	private GridView gv_home;
	private String[] mItem = new String[] { "手机防盗", "通讯卫士", "软件管理", "进程管理",
			"流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };

	private int[] mPics = new int[] { R.drawable.home_safe,
			R.drawable.home_callmsgsafe, R.drawable.home_apps,
			R.drawable.home_taskmanager, R.drawable.home_netmanager,
			R.drawable.home_trojan, R.drawable.home_sysoptimize,
			R.drawable.home_tools, R.drawable.home_settings };

	private SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		gv_home = (GridView) findViewById(R.id.gv_home);
		gv_home.setAdapter(new HomeAdapter());

		// 设置项目点击事件监听
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				// 跳转到手机防盗
				case 0:
					showPasswordDialog();
					break;

				// 通讯卫士
				case 1:
					startActivity(new Intent(HomeActivity.this,
							CallSafeActivity2.class));
					break;

				// 软件管理
				case 2:
					startActivity(new Intent(HomeActivity.this,
							AppManagerActivity.class));
					break;

				// 进程管理
				case 3:
					startActivity(new Intent(HomeActivity.this,
							TaskManagerActivity.class));
					break;
					
				// 病毒查杀
				case 5:
					startActivity(new Intent(HomeActivity.this,
							AntivirusActivity.class));
					break;

				// 高级工具
				case 7:
					startActivity(new Intent(HomeActivity.this,
							AToolsActivity.class));
					break;

				// 跳转到设置中心
				case 8:
					startActivity(new Intent(HomeActivity.this,
							SettingActivity.class));
					break;

				default:
					break;
				}
			}

		});

	}

	/**
	 * 显示密码弹窗(是否已经设置过密码)
	 */
	private void showPasswordDialog() {
		// TODO Auto-generated method stub
		String savedPassword = mPref.getString("password", null);
		// 判断是否已经设置过密码
		if (TextUtils.isEmpty(savedPassword)) {
			// 没有设置过密码
			showPasswordSetDailog();
		} else {
			// 设置过密码，请输入密码
			showPasswordInputDailog();
		}

	}

	/**
	 * 显示登陆密码弹窗
	 */
	private void showPasswordInputDailog() {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		// 将自定义的布局文件填充到dialog中
		View view = View.inflate(this, R.layout.dialog_input_password, null);
		// dialog.setView(view);
		// 推荐！填充时把边距都调至0
		dialog.setView(view, 0, 0, 0, 0);

		final EditText et_password = (EditText) view
				.findViewById(R.id.et_password);

		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		// 确定按钮监听
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = et_password.getText().toString();

				if (TextUtils.isEmpty(password)) {
					Toast.makeText(HomeActivity.this, "输入框内容不能为空！",
							Toast.LENGTH_SHORT).show();
				} else {
					String savedPassword = mPref.getString("password", null);
					if (MD5Utils.encode(password).equals(savedPassword)) {
						// Toast.makeText(HomeActivity.this, "登陆成功！",
						// Toast.LENGTH_SHORT).show();

						dialog.dismiss();

						// 跳转到 手机防盗 页面
						startActivity(new Intent(HomeActivity.this,
								LostFindActivity.class));
					} else {
						Toast.makeText(HomeActivity.this, "密码错误！",
								Toast.LENGTH_SHORT).show();
					}
				}

			}
		});

		// 取消按钮监听
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	/**
	 * 设置密码的弹窗
	 */
	private void showPasswordSetDailog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		// 将自定义的布局文件填充到dialog中
		View view = View.inflate(this, R.layout.dialog_set_password, null);
		// dialog.setView(view);
		// 推荐！填充时把边距都调至0
		dialog.setView(view, 0, 0, 0, 0);

		final EditText et_password = (EditText) view
				.findViewById(R.id.et_password);
		final EditText et_password_confirm = (EditText) view
				.findViewById(R.id.et_password_confirm);

		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		// 确定按钮监听
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String password = et_password.getText().toString();
				String passwordConfirm = et_password_confirm.getText()
						.toString();

				// 判断是否为空串 （ password != null && !password.equals("") ）
				if (!TextUtils.isEmpty(password)
						&& !TextUtils.isEmpty(passwordConfirm)) {
					if (password.equals(passwordConfirm)) {
						// Toast.makeText(HomeActivity.this, "登陆成功",
						// Toast.LENGTH_SHORT).show();

						mPref.edit()
								.putString("password",
										MD5Utils.encode(password)).commit();

						dialog.dismiss();

						// 跳转到 手机防盗 页面
						startActivity(new Intent(HomeActivity.this,
								LostFindActivity.class));
					} else {
						Toast.makeText(HomeActivity.this, "两次密码输入不一致！",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(HomeActivity.this, "输入框内容不能为空!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// 取消按钮监听
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	class HomeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mItem.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mItem[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = View.inflate(HomeActivity.this,
					R.layout.home_list_item, null);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);

			tv_item.setText(mItem[position]);
			iv_item.setImageResource(mPics[position]);
			return view;
		}

	}
}
