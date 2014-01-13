package com.example.ygapp_2013;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumericLimitEditText extends EditText{
	private int from,to;
	public NumericLimitEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		from = 0;
		to = 0xFFFFFFFF;
		this.setOnEditorActionListener(new OnEditorActionListener(){
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub			
				try{
					int number = Integer.parseInt(v.getText().toString());
					if( number >= from && number <= to ){
						v.setTextColor(Color.BLACK);
					}
					else{
						v.setTextColor(Color.RED);
					}
				}
				catch(NumberFormatException e){
					e.printStackTrace();					
				}				
				return false;
			}});
	}
    @Override
    public void onTextChanged(CharSequence s, int arg1, int arg2,
            int arg3) {
		try{
			int number = Integer.parseInt(this.getText().toString());
			if( number >= from && number <= to ){
				this.setTextColor(Color.BLACK);
			}
			else{
				this.setTextColor(Color.RED);
			}
		}
		catch(NumberFormatException e){
			e.printStackTrace();					
		}	
    }
	public Boolean setRange(int from,int to){
		if( from > to)
			return false;
		this.from = from;
		this.to = to;
		return true;		
	}
	public int getFrom(){
		return from;		
	}
	public int getTo(){
		return to;		
	}	
}
