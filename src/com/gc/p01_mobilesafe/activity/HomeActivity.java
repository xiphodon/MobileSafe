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
 * ������
 * 
 * @author guochang
 * 
 */
public class HomeActivity extends Activity {

	private GridView gv_home;
	private String[] mItem = new String[] { "�ֻ�����", "ͨѶ��ʿ", "�������", "���̹���",
			"����ͳ��", "�ֻ�ɱ��", "��������", "�߼�����", "��������" };

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

		// ������Ŀ����¼�����
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				// ��ת���ֻ�����
				case 0:
					showPasswordDialog();
					break;

				// ͨѶ��ʿ
				case 1:
					startActivity(new Intent(HomeActivity.this,
							CallSafeActivity2.class));
					break;

				// �������
				case 2:
					startActivity(new Intent(HomeActivity.this,
							AppManagerActivity.class));
					break;

				// ���̹���
				case 3:
					startActivity(new Intent(HomeActivity.this,
							TaskManagerActivity.class));
					break;
					
				// ������ɱ
				case 5:
					startActivity(new Intent(HomeActivity.this,
							AntivirusActivity.class));
					break;

				// �߼�����
				case 7:
					startActivity(new Intent(HomeActivity.this,
							AToolsActivity.class));
					break;

				// ��ת����������
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
	 * ��ʾ���뵯��(�Ƿ��Ѿ����ù�����)
	 */
	private void showPasswordDialog() {
		// TODO Auto-generated method stub
		String savedPassword = mPref.getString("password", null);
		// �ж��Ƿ��Ѿ����ù�����
		if (TextUtils.isEmpty(savedPassword)) {
			// û�����ù�����
			showPasswordSetDailog();
		} else {
			// ���ù����룬����������
			showPasswordInputDailog();
		}

	}

	/**
	 * ��ʾ��½���뵯��
	 */
	private void showPasswordInputDailog() {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		// ���Զ���Ĳ����ļ���䵽dialog��
		View view = View.inflate(this, R.layout.dialog_input_password, null);
		// dialog.setView(view);
		// �Ƽ������ʱ�ѱ߾඼����0
		dialog.setView(view, 0, 0, 0, 0);

		final EditText et_password = (EditText) view
				.findViewById(R.id.et_password);

		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		// ȷ����ť����
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = et_password.getText().toString();

				if (TextUtils.isEmpty(password)) {
					Toast.makeText(HomeActivity.this, "��������ݲ���Ϊ�գ�",
							Toast.LENGTH_SHORT).show();
				} else {
					String savedPassword = mPref.getString("password", null);
					if (MD5Utils.encode(password).equals(savedPassword)) {
						// Toast.makeText(HomeActivity.this, "��½�ɹ���",
						// Toast.LENGTH_SHORT).show();

						dialog.dismiss();

						// ��ת�� �ֻ����� ҳ��
						startActivity(new Intent(HomeActivity.this,
								LostFindActivity.class));
					} else {
						Toast.makeText(HomeActivity.this, "�������",
								Toast.LENGTH_SHORT).show();
					}
				}

			}
		});

		// ȡ����ť����
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
	 * ��������ĵ���
	 */
	private void showPasswordSetDailog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		// ���Զ���Ĳ����ļ���䵽dialog��
		View view = View.inflate(this, R.layout.dialog_set_password, null);
		// dialog.setView(view);
		// �Ƽ������ʱ�ѱ߾඼����0
		dialog.setView(view, 0, 0, 0, 0);

		final EditText et_password = (EditText) view
				.findViewById(R.id.et_password);
		final EditText et_password_confirm = (EditText) view
				.findViewById(R.id.et_password_confirm);

		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		// ȷ����ť����
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String password = et_password.getText().toString();
				String passwordConfirm = et_password_confirm.getText()
						.toString();

				// �ж��Ƿ�Ϊ�մ� �� password != null && !password.equals("") ��
				if (!TextUtils.isEmpty(password)
						&& !TextUtils.isEmpty(passwordConfirm)) {
					if (password.equals(passwordConfirm)) {
						// Toast.makeText(HomeActivity.this, "��½�ɹ�",
						// Toast.LENGTH_SHORT).show();

						mPref.edit()
								.putString("password",
										MD5Utils.encode(password)).commit();

						dialog.dismiss();

						// ��ת�� �ֻ����� ҳ��
						startActivity(new Intent(HomeActivity.this,
								LostFindActivity.class));
					} else {
						Toast.makeText(HomeActivity.this, "�����������벻һ�£�",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(HomeActivity.this, "��������ݲ���Ϊ��!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// ȡ����ť����
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
