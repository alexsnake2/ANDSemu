package com.danieru.miraie.nds;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;

public class HomeActivity extends SherlockActivity implements OnSharedPreferenceChangeListener {

	SharedPreferences prefs = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Filespace.prepFolders();

		Settings.applyDefaults(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.home);
	    updateQuickSettings(prefs);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.home, menu);
	    return true;
	}
	
	public void gotoSettings(View view) {
        Intent settingsIntent = new Intent(view.getContext(), Settings.class);
        startActivityForResult(settingsIntent, 0);
	}
	
	/* Play new game */
	public void pickRom(View view) {
		Intent i = new Intent(this, FileDialog.class);
		i.setAction(Intent.ACTION_PICK);
		i.putExtra(FileDialog.START_PATH, Filespace.getGameFolder() + '/');
		i.putExtra(FileDialog.FORMAT_FILTER, new String[] {".nds", ".zip", ".7z"});
		startActivityForResult(i, EmulateActivity.PICK_ROM);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EmulateActivity.PICK_ROM) {
		     if(resultCode == RESULT_OK) {
		    	 playRom(Uri.parse(data.getStringExtra(FileDialog.RESULT_PATH)));
		     }
		}
	}
	
	public void playRom(Uri romPath) {
        Intent playIntent = new Intent(this, EmulateActivity.class);
        playIntent.setAction(EmulateActivity.IntentActions.LOAD);
        playIntent.setData(romPath);
        startActivity(playIntent);
        
        android.widget.Button resumeBtn = (android.widget.Button) findViewById(R.id.resumeGame);
        resumeBtn.setVisibility(View.VISIBLE);
        resumeBtn.setTextSize(32);
        resumeBtn.setPadding(0, 30, 0, 30);
	}
	
	/* Resume current game */
	public void resumeGame(View view) {
        Intent playIntent = new Intent(this, EmulateActivity.class);
        playIntent.setAction(EmulateActivity.IntentActions.RESUME);
        startActivity(playIntent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.menu_settings:
	            Intent settingsIntent = new Intent(this, Settings.class);
	            startActivity(settingsIntent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	/* Quick Settings */
	public void setSound(View view) {
		SharedPreferences.Editor editor = prefs.edit();
	    editor.putBoolean(Settings.ENABLE_SOUND, ((CheckBox) view).isChecked());
	    editor.commit();
	}
	
	public void setVsync(View view) {
		SharedPreferences.Editor editor = prefs.edit();
	    editor.putBoolean(Settings.VSYNC, ((CheckBox) view).isChecked());
	    editor.commit();
	}
	
	public void updateQuickSettings(SharedPreferences prefs) {
		CheckBox vsync = (CheckBox) findViewById(R.id.vsyncToggle);
		vsync.setChecked(prefs.getBoolean(Settings.VSYNC, true));
		CheckBox sound = (CheckBox) findViewById(R.id.soundToggle);
		sound.setChecked(prefs.getBoolean(Settings.ENABLE_SOUND, true));
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
		updateQuickSettings(prefs);
	}
}