package com.advsofteng.app1;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

// ref: http://about-android.blogspot.com/2010/02/create-custom-dialog.html

public class DialogBoxPermissions extends Dialog {

    public interface ReadyListener {
        public void ready(String name);
    }

    private String name;
    private ReadyListener readyListener;
    private SeekBar seekbar = null;
    private TextView tvPermsRadius = null;
    private int intMaxRadius = 100; // max "fuzz" in metres around your exact location...
    private int intFuzzRadius = AddressBookEntry.PERM_SHOWN; // set to default, which is just shown, with no "fuzz"
    private int iCurrentEntry = 0;

    public DialogBoxPermissions(Context context, String name,
            ReadyListener readyListener, int CurrentEntry) {
        super(context);
        this.name = name;
        this.readyListener = readyListener;
        this.iCurrentEntry = CurrentEntry;
    }
    
    public int getFuzz(){
    	return (intFuzzRadius);
    }
    
    public int getContactEntryNumber(){
    	return (iCurrentEntry);
    }
    
    //////////////////////////
	// create, instantiate and override implemented seekbar methods...
	private SeekBar.OnSeekBarChangeListener seekBarListen = new SeekBar.OnSeekBarChangeListener() {
		
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			}
		
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}
		
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			intFuzzRadius = progress;
			tvPermsRadius.setText(Integer.toString(progress) + " m");
		}
	};
	//
	////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permsdialogbox);
        
        setTitle("Radius around your exact location...");
        
        Button buttonOK = (Button) findViewById(R.id.ButtonPermsOK);
        buttonOK.setOnClickListener(new OKListener());
        
        seekbar = (SeekBar) findViewById(R.id.seekBarPermsRadius);
        seekbar.setOnSeekBarChangeListener(seekBarListen);
        seekbar.setMax(intMaxRadius);
        
        tvPermsRadius = (TextView) findViewById(R.id.textViewFuzz);
    }

    private class OKListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.ready("Test"/*String.valueOf(etName.getText())*/);
            DialogBoxPermissions.this.dismiss();
        }
    }

}