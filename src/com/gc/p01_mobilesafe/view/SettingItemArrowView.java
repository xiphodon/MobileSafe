package com.gc.p01_mobilesafe.view;

import com.gc.p01_mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * �������ĵ��Զ�����Ͽؼ�
 * @author guochang
 *
 */
public class SettingItemArrowView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.gc.p01_mobilesafe";
	private TextView tv_title;
	private TextView tv_desc;
	private String mTitle;

	public SettingItemArrowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView();
	}

	public SettingItemArrowView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		mTitle = attrs.getAttributeValue(NAMESPACE, "title");
		
		initView();
	}

	public SettingItemArrowView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}
	
	/**
	 * ��ʼ���ؼ�
	 */
	private void initView(){
		View.inflate(getContext(), R.layout.view_setting_item_arrow, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		
		//���ñ���
		setTitle(mTitle);
	}

	/**
	 * ���ÿؼ�����
	 * @param title ��������
	 */
	public void setTitle(String title){
		tv_title.setText(title);
	}
	
	/**
	 * ���ÿؼ���������
	 * @param desc ��������
	 */
	public void setDesc(String desc){
		tv_desc.setText(desc);
	}
	
}
