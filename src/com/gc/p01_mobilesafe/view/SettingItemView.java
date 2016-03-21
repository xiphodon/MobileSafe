package com.gc.p01_mobilesafe.view;

import com.gc.p01_mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 设置中心的自定义组合控件
 * @author guochang
 *
 */
public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.gc.p01_mobilesafe";
	private TextView tv_title;
	private TextView tv_desc;
	private CheckBox cb_status;
	private String mTitle;
	private String mDesc_on;
	private String mDesc_off;

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView();
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		mTitle = attrs.getAttributeValue(NAMESPACE, "title");
		mDesc_on = attrs.getAttributeValue(NAMESPACE, "desc_on");
		mDesc_off = attrs.getAttributeValue(NAMESPACE, "desc_off");
		
		initView();
	}

	public SettingItemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}
	
	/**
	 * 初始化控件
	 */
	private void initView(){
		View.inflate(getContext(), R.layout.view_setting_item, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		
		setTitle(mTitle);
	}

	/**
	 * 设置控件标题
	 * @param title 标题内容
	 */
	public void setTitle(String title){
		tv_title.setText(title);
	}
	
	/**
	 * 设置控件描述内容
	 * @param desc 描述内容
	 */
	public void setDesc(String desc){
		tv_desc.setText(desc);
	}
	
	/**
	 * 返回复选框勾选状态
	 * @return
	 */
	public boolean isChecked(){
		return cb_status.isChecked();
	}
	
	/**
	 * 设置复选框勾选状态
	 * @param check
	 */
	public void setChecked(boolean check){
		cb_status.setChecked(check);
		
		//根据选择的状态更新文本描述
		if(check){
			setDesc(mDesc_on);
		}else{
			setDesc(mDesc_off);
		}
	}
}
