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

    private ReadyListener readyListener;
    private SeekBar seekbar = null;
    private TextView tvPermsRadius = null;
    private int intMaxRadius = 100; // max "fuzz" in metres around your exact location...
    private int intFuzzRadius = AddressBookEntry.PERM_SHOWN; // set to default, which is just shown, with no "fuzz"
    private int iCurrentEntry = 0;

    public DialogBoxPermissions(Context context,
            ReadyListener readyListener, int CurrentEntry) {
        super(context);
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
		
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		public void onStartTrackingTouch(SeekBar seekBar) {}
		
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			intFuzzRadius = progress;
			tvPermsRadius.setText(Integer.toString(progress) + " metres");
		}
	};
	//
	////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permsdialogbox);
        
        setTitle("   Smudge my position by...");
        
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
            readyListener.ready("");
            DialogBoxPermissions.this.dismiss();
        }
    }

}