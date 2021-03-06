package com.danieru.miraie.nds;

/*
	Copyright (C) 2012 Jeffrey Quesnelle

	This file is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 2 of the License, or
	(at your option) any later version.

	This file is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with the this software.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import com.actionbarsherlock.view.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


//This originally came from http://code.google.com/p/android-file-dialog/

public class FileDialog extends SherlockListActivity {
	
	public static class SelectionMode {
		public static final int MODE_CREATE = 0;

		public static final int MODE_OPEN = 1;
	}


	private static final String ITEM_KEY = "key";


	private static final String ITEM_IMAGE = "image";
	private static final String ROOT = "/";

	public static final String START_PATH = "START_PATH";
	public static final String FORMAT_FILTER = "FORMAT_FILTER";
	public static final String RESULT_PATH = "RESULT_PATH";
	public static final String SELECTION_MODE = "SELECTION_MODE";
	public static final String CAN_SELECT_DIR = "CAN_SELECT_DIR";
	public static final String ROM_DIR = "ROMDIR";

	private List<String> path = null;
	private TextView myPath;
	private ArrayList<HashMap<String, Object>> mList;

	private String parentPath;
	private String currentPath = ROOT;

	//private int selectionMode = SelectionMode.MODE_CREATE;

	private String[] formatFilter = null;

	private boolean canSelectDir = false;

	private File selectedFile;
	private HashMap<String, Integer> lastPositions = new HashMap<String, Integer>();
	private SharedPreferences prefs;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED, getIntent());

		setContentView(R.layout.file_dialog_main);
		myPath = (TextView) findViewById(R.id.path);

		formatFilter = getIntent().getStringArrayExtra(FORMAT_FILTER);
		canSelectDir = getIntent().getBooleanExtra(CAN_SELECT_DIR, false);

		// Select the dir most likely to contain user's ROMS
		String openPath;
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.contains(ROM_DIR)) {
			openPath = prefs.getString(ROM_DIR, ROOT);
			
		} else if (Filespace.isGameFolderUsed()) {
			openPath = Filespace.getGameFolder();
			
		} else {
			openPath = Filespace.getSDcard();
		}
		
		if (canSelectDir) {
			File file = new File(openPath);
			selectedFile = file;
		}
		getDir(openPath);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    return true;
	}
	
	@Override
	public boolean onMenuItemSelected (int featureId, MenuItem item) {
		switch(item.getItemId()) {
        case android.R.id.home:
        	finish();
            return true;
		default:
			return false;
		}
	}

	private void getDir(String dirPath) {

		boolean useAutoSelection = dirPath.length() < currentPath.length();

		Integer position = lastPositions.get(parentPath);

		getDirImpl(dirPath);

		if (position != null && useAutoSelection) {
			getListView().setSelection(position);
		}

	}


	private void getDirImpl(final String dirPath) {

		currentPath = dirPath;

		final List<String> item = new ArrayList<String>();
		path = new ArrayList<String>();
		mList = new ArrayList<HashMap<String, Object>>();

		File f = new File(currentPath);
		File[] files = f.listFiles();
		if (files == null) {
			currentPath = Filespace.getSDcard();
			f = new File(currentPath);
			files = f.listFiles();
		}
		StringBuilder pathString = new StringBuilder(getText(R.string.location) + ": " + currentPath);
		if(formatFilter != null) {
			if(formatFilter.length > 0) {
				pathString.append(" (");
				for(int i = 0 ; i < formatFilter.length ; ++i) {
					pathString.append(formatFilter[i]);
					if(i != formatFilter.length - 1)
						pathString.append(", ");
				}
				pathString.append(")");
			}
		}
		myPath.setText(pathString.toString());

		if (!currentPath.equals(ROOT)) {

			item.add("../");
			addItem("../", R.drawable.updirectory);
			path.add(f.getParent());
			parentPath = f.getParent();

		}

		TreeMap<String, String> dirsMap = new TreeMap<String, String>();
		TreeMap<String, String> dirsPathMap = new TreeMap<String, String>();
		TreeMap<String, String> filesMap = new TreeMap<String, String>();
		TreeMap<String, String> filesPathMap = new TreeMap<String, String>();
		for (File file : files) {
			if (file.isDirectory()) {
				String dirName = file.getName();
				if (dirName.charAt(0) == '.')
					break;
				
				dirsMap.put(dirName, dirName);
				dirsPathMap.put(dirName, file.getPath());
				
			} else {
				final String fileName = file.getName();
				final String fileNameLwr = fileName.toLowerCase(Locale.ENGLISH);
				
				if (formatFilter != null) {
					boolean contains = false;
					for (int i = 0; i < formatFilter.length; i++) {
						final String formatLwr = formatFilter[i].toLowerCase(Locale.ENGLISH);
						if (fileNameLwr.endsWith(formatLwr)) {
							contains = true;
							break;
						}
					}
					
					if (contains) {
						filesMap.put(fileName, fileName);
						filesPathMap.put(fileName, file.getPath());
					}
					
				} else {
					filesMap.put(fileName, fileName);
					filesPathMap.put(fileName, file.getPath());
				}
			}
		}
		item.addAll(dirsMap.tailMap("").values());
		item.addAll(filesMap.tailMap("").values());
		path.addAll(dirsPathMap.tailMap("").values());
		path.addAll(filesPathMap.tailMap("").values());

		SimpleAdapter fileList = new SimpleAdapter(this, mList, R.layout.file_dialog_row, new String[] {
				ITEM_KEY, ITEM_IMAGE }, new int[] { R.id.fdrowtext, R.id.fdrowimage });

		for (String dir : dirsMap.tailMap("").values()) {
			addItem(dir, R.drawable.folder);
		}

		for (String file : filesMap.tailMap("").values()) {
			addItem(file, R.drawable.dscartridge);
		}

		fileList.notifyDataSetChanged();

		setListAdapter(fileList);

	}

	private void addItem(String fileName, int imageId) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put(ITEM_KEY, fileName);
		item.put(ITEM_IMAGE, imageId);
		mList.add(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		File file = new File(path.get(position));


		if (file.isDirectory()) {
			if (file.canRead()) {
				lastPositions.put(currentPath, position);
				getDir(path.get(position));
				if (canSelectDir) {
					selectedFile = file;
					v.setSelected(true);
				}
			} else {
				new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher)
						.setTitle("[" + file.getName() + "] " + getText(R.string.cant_read_folder))
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();
			}
		} else {
			selectedFile = file;
			prefs.edit().putString(ROM_DIR, file.getParent()).commit();
			v.setSelected(true);
			getIntent().putExtra(RESULT_PATH, selectedFile.getPath());
			setResult(RESULT_OK, getIntent());
			finish();
		}
	}

	

}
