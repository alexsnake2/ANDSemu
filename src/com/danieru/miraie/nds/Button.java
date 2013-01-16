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

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.Log;

@SuppressLint("UseSparseArrays")
class Button {
	
	Button(int id) {
		this.id = id;
		this.position = null;
		this.bitmap = null;
	}
	
	Button(Rect position, int id) {
		this.position = position;
		this.id = id;
		this.bitmap = null;
	}
	
	Button(Rect position, int id, Bitmap bitmap) {
		this.position = position;
		this.id = id;
		this.bitmap = bitmap;
	}
	static int screen_height;
	static int screen_width;
	static int R_height = 90, L_height = 90;
	static int R_width = 160, L_width = 160;
	static int DPAD_height = 314;
	static int DPAD_width = 334;
	static int ABXY_height = 317;
	static int ABXY_width = 362;
	static int START_height = 65;
	static int START_width = 96;
	static int SELECT_height = 63;
	static int SELECT_width = 85;
	static int TOUCH_height = 69;
	static int TOUCH_width = 121;
	
	static final Button L_PORT_DEFAULT = new Button(new Rect(0, 590, 160, 680), Button.BUTTON_L);
	static final Button R_PORT_DEFAULT = new Button(new Rect(610, 590, 768, 680), Button.BUTTON_R);
	static final Button TOUCH_PORT_DEFAULT = new Button(new Rect(320, 590, 441, 659), Button.BUTTON_TOUCH);
	static final Button DPAD_PORT_DEFAULT = new Button(new Rect(0, 760, 334, 1074), Button.BUTTON_DPAD);
	static final Button ABXY_PORT_DEFAULT = new Button(new Rect(397, 755, 759, 1072), Button.BUTTON_ABXY);
	static final Button START_PORT_DEFAULT = new Button(new Rect(270, 1082, 366, 1147), Button.BUTTON_START);
	static final Button SELECT_PORT_DEFAULT = new Button(new Rect(400, 1082, 485, 1145), Button.BUTTON_SELECT);

	static Button L_LAND_DEFAULT;
	static Button R_LAND_DEFAULT;
	static Button DPAD_LAND_DEFAULT;
	static Button ABXY_LAND_DEFAULT;
	static Button START_LAND_DEFAULT;
	static Button TOUCH_LAND_DEFAULT;
	static Button SELECT_LAND_DEFAULT;
	
	public static void generateDefaultLandscape(int height, int width) {
		screen_height = height;
		screen_width = width;

		int sep = 25;
		L_LAND_DEFAULT = new Button(new Rect(sep, sep, L_width + sep, L_height + sep), Button.BUTTON_L);
		R_LAND_DEFAULT = new Button(new Rect(width - R_width - sep, sep, width - sep, R_height + sep), Button.BUTTON_R);
		DPAD_LAND_DEFAULT = new Button(new Rect(sep, height - DPAD_height - sep,
												DPAD_width + sep, height - sep), Button.BUTTON_DPAD);
		ABXY_LAND_DEFAULT = new Button(new Rect(width - ABXY_width - sep, height - ABXY_height - sep,
												width - sep, height - sep), Button.BUTTON_ABXY);
		int mid = width / 2;
		int off = TOUCH_width / 2;
		START_LAND_DEFAULT = new Button(new Rect(mid + off, height - START_height - sep,
												 mid + off + START_width, height - sep), Button.BUTTON_START);
		TOUCH_LAND_DEFAULT = new Button(new Rect(mid - off, height - TOUCH_height - sep, mid + off, height - sep), Button.BUTTON_TOUCH);
		SELECT_LAND_DEFAULT = new Button(new Rect(mid - off - SELECT_width, height - sep - SELECT_height,
												  mid - off, height - sep), Button.BUTTON_SELECT);
	}
	
	
	static HashMap<Integer, Button> portraitToDefault;
	static HashMap<Integer, Button> landscapeToDefault;
	
	static void genHashmap() {
		portraitToDefault = new HashMap<Integer, Button>();
		portraitToDefault.put(Button.BUTTON_L, L_PORT_DEFAULT);
		portraitToDefault.put(Button.BUTTON_R, R_PORT_DEFAULT);
		portraitToDefault.put(Button.BUTTON_TOUCH, TOUCH_PORT_DEFAULT);
		portraitToDefault.put(Button.BUTTON_DPAD, DPAD_PORT_DEFAULT);
		portraitToDefault.put(Button.BUTTON_ABXY, ABXY_PORT_DEFAULT);
		portraitToDefault.put(Button.BUTTON_START, START_PORT_DEFAULT);
		portraitToDefault.put(Button.BUTTON_SELECT, SELECT_PORT_DEFAULT);
		
		landscapeToDefault = new HashMap<Integer, Button>();
		landscapeToDefault.put(Button.BUTTON_L, L_LAND_DEFAULT);
		landscapeToDefault.put(Button.BUTTON_R, R_LAND_DEFAULT);
		landscapeToDefault.put(Button.BUTTON_TOUCH, TOUCH_LAND_DEFAULT);
		landscapeToDefault.put(Button.BUTTON_DPAD, DPAD_LAND_DEFAULT);
		landscapeToDefault.put(Button.BUTTON_ABXY, ABXY_LAND_DEFAULT);
		landscapeToDefault.put(Button.BUTTON_START, START_LAND_DEFAULT);
		landscapeToDefault.put(Button.BUTTON_SELECT, SELECT_LAND_DEFAULT);
	}
	
	static Button load(Context context, int id, int resId, boolean landscape, boolean is565, Rect screen, Rect space, boolean forceLoad) {
		
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		final String prefLayoutBase = "Controls." + (landscape ? "Landscape." : "Portrait.");
		final String prefBase = prefLayoutBase + getButtonName(id) + "." ;
		final Button template = landscape ? landscapeToDefault.get(id) : portraitToDefault.get(id);
		if(forceLoad || prefs.getBoolean(prefLayoutBase + "Draw" , true)) {
			float controlxscale = (float)screen.width() / (float)space.width();
			float controlyscale = (float)screen.height() / (float)space.height();

			final Rect finalRect = new Rect(prefs.getInt(prefBase + "Left", (int) (template.position.left * controlxscale)),
					prefs.getInt(prefBase + "Top", (int) (template.position.top * controlyscale)),
					prefs.getInt(prefBase + "Right", (int) (template.position.right * controlxscale)),
					prefs.getInt(prefBase + "Bottom", (int) (template.position.bottom * controlyscale)));
	
			final BitmapFactory.Options controlOptions = new BitmapFactory.Options();
			if(is565)
				controlOptions.inPreferredConfig = Bitmap.Config.RGB_565;
			final Bitmap originalControls = BitmapFactory.decodeResource(context.getResources(), resId, controlOptions);
			final Bitmap controls = Bitmap.createScaledBitmap(originalControls, finalRect.width(), finalRect.height(), true);
			return new Button(finalRect, id, controls);
		}
		else
			return new Button(template.position, id);
		
	}
	
	static String getButtonName(int id) {
		switch(id) {
		case BUTTON_RIGHT: return "Right";
		case BUTTON_DOWN: return "Down";
		case BUTTON_UP: return "Up";
		case BUTTON_LEFT: return "Left";
		case BUTTON_A: return "A";
		case BUTTON_B: return "B";
		case BUTTON_X: return "X";
		case BUTTON_Y: return "Y";
		case BUTTON_L: return "L";
		case BUTTON_R: return "R";
		case BUTTON_START: return "Start";
		case BUTTON_SELECT: return "Select";
		case BUTTON_TOUCH: return "Touch";
		case BUTTON_DPAD: return "DPad";
		case BUTTON_ABXY: return "ABXY";
		case BUTTON_UPLEFT: return "UpLeft";
		case BUTTON_UPRIGHT: return "UpRight";
		case BUTTON_DOWNLEFT: return "DownLeft";
		case BUTTON_DOWNRIGHT: return "DownRight";
		default: return "Unknown";
		}
	}
	
	void apply(int[] states, boolean on) {
		final int val = on ? 1 : 0;
		switch(id) {
		case BUTTON_UPLEFT:
			states[Button.BUTTON_UP] = states[Button.BUTTON_LEFT] = val;
			break;
		case BUTTON_UPRIGHT:
			states[Button.BUTTON_UP] = states[Button.BUTTON_RIGHT] = val;
			break;
		case BUTTON_DOWNLEFT:
			states[Button.BUTTON_DOWN] = states[Button.BUTTON_LEFT] = val;
			break;
		case BUTTON_DOWNRIGHT:
			states[Button.BUTTON_DOWN] = states[Button.BUTTON_RIGHT] = val;
			break;
		default:
			if(id >= 0 && id < states.length)
				states[id] = val;
			break;
		}
	}
	
	void applyToPrefs(SharedPreferences prefs, boolean landscape, boolean overwrite) {
		final String prefLayoutBase = "Controls." + (landscape ? "Landscape." : "Portrait.");
		final String prefBase = prefLayoutBase + getButtonName(id) + "." ;
		if(prefs.contains(prefBase + "Left") && !overwrite)
			return;
		prefs.edit().putInt(prefBase + "Left", position.left).putInt(prefBase + "Top", position.top).putInt(prefBase + "Right", position.right).putInt(prefBase + "Bottom", position.bottom).apply();
	}
	
	static final int BUTTON_RIGHT = 0;
	static final int BUTTON_DOWN = 1;
	static final int BUTTON_UP = 2;
	static final int BUTTON_LEFT = 3;
	static final int BUTTON_A = 4;
	static final int BUTTON_B = 5;
	static final int BUTTON_X = 6;
	static final int BUTTON_Y = 7;
	static final int BUTTON_L = 8;
	static final int BUTTON_R = 9;
	static final int BUTTON_START = 10;
	static final int BUTTON_SELECT = 11;
	
	//meta buttons, aren't actually passed down
	static final int BUTTON_TOUCH = 12;
	static final int BUTTON_DPAD = 13;
	static final int BUTTON_ABXY = 14;
	static final int BUTTON_UPLEFT = 15;
	static final int BUTTON_UPRIGHT = 16;
	static final int BUTTON_DOWNLEFT = 17;
	static final int BUTTON_DOWNRIGHT = 18;
	
	final Bitmap bitmap;
	Rect position;
	final int id;
}
