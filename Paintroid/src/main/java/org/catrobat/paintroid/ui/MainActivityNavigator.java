/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.common.MainActivityConstants.ActivityRequestCode;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.dialog.AboutDialog;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.InfoDialog;
import org.catrobat.paintroid.dialog.PermissionInfoDialog;
import org.catrobat.paintroid.dialog.SaveBeforeFinishDialog;
import org.catrobat.paintroid.dialog.SaveBeforeFinishDialog.SaveBeforeFinishDialogType;
import org.catrobat.paintroid.dialog.SaveBeforeLoadImageDialog;
import org.catrobat.paintroid.dialog.SaveBeforeNewImageDialog;
import org.catrobat.paintroid.tools.ToolReference;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainActivityNavigator implements MainActivityContracts.Navigator {
	private MainActivity mainActivity;
	private final ToolReference toolReference;

	public MainActivityNavigator(MainActivity mainActivity, ToolReference toolReference) {
		this.mainActivity = mainActivity;
		this.toolReference = toolReference;
	}

	@Override
	public void showColorPickerDialog() {
		FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(Constants.COLOR_PICKER_DIALOG_TAG);
		if (fragment == null) {
			ColorPickerDialog dialog = ColorPickerDialog.newInstance(toolReference.get().getDrawPaint().getColor());
			setupColorPickerDialogListeners(dialog);
			dialog.show(fragmentManager, Constants.COLOR_PICKER_DIALOG_TAG);
		}
	}

	private void setupColorPickerDialogListeners(ColorPickerDialog dialog) {
		dialog.addOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				toolReference.get().changePaintColor(color);
				mainActivity.getPresenter().setTopBarColor(color);
			}
		});
	}

	@Override
	public void startLoadImageActivity(@ActivityRequestCode int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		mainActivity.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startImportImageActivity(@ActivityRequestCode int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		mainActivity.startActivityForResult(intent, requestCode);
	}

	@Override
	public void showAboutDialog() {
		AboutDialog about = AboutDialog.newInstance();
		about.show(mainActivity.getSupportFragmentManager(), Constants.ABOUT_DIALOG_FRAGMENT_TAG);
	}

	private Fragment getIndeterminateProgressFragment() {
		FragmentManager supportFragmentManager = mainActivity.getSupportFragmentManager();
		return supportFragmentManager.findFragmentByTag(Constants.INDETERMINATE_FRAGMENT_TAG);
	}

	@Override
	public void showIndeterminateProgressDialog() {
		Fragment fragment = getIndeterminateProgressFragment();
		if (fragment == null) {
			AppCompatDialogFragment dialog = IndeterminateProgressDialog.newInstance();
			dialog.show(mainActivity.getSupportFragmentManager(), Constants.INDETERMINATE_FRAGMENT_TAG);
		}
	}

	@Override
	public void dismissIndeterminateProgressDialog() {
		Fragment fragment = getIndeterminateProgressFragment();
		if (fragment != null) {
			AppCompatDialogFragment dialog = (AppCompatDialogFragment) fragment;
			dialog.dismissAllowingStateLoss();
		}
	}

	@Override
	public void showToast(int resId, int duration) {
		ToastFactory.makeText(mainActivity, resId, duration).show();
	}

	@Override
	public void showSaveErrorDialog() {
		AppCompatDialogFragment dialog = InfoDialog.newInstance(InfoDialog.DialogType.WARNING,
				R.string.dialog_error_sdcard_text, R.string.dialog_error_save_title);
		dialog.show(mainActivity.getSupportFragmentManager(), Constants.SAVE_DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void showLoadErrorDialog() {
		AppCompatDialogFragment dialog = InfoDialog.newInstance(InfoDialog.DialogType.WARNING,
				R.string.dialog_loading_image_failed_title, R.string.dialog_loading_image_failed_text);
		dialog.show(mainActivity.getSupportFragmentManager(), Constants.LOAD_DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void showRequestPermissionRationaleDialog(PermissionInfoDialog.PermissionType permissionType, String[] permissions, int requestCode) {
		AppCompatDialogFragment dialog = PermissionInfoDialog.newInstance(permissionType, permissions, requestCode);
		dialog.show(mainActivity.getSupportFragmentManager(), Constants.PERMISSION_DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void askForPermission(String[] permissions, int requestCode) {
		ActivityCompat.requestPermissions(mainActivity, permissions, requestCode);
	}

	@Override
	public boolean isSdkAboveOrEqualM() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}

	@Override
	public boolean doIHavePermission(String permission) {
		return ContextCompat.checkSelfPermission(mainActivity,
				permission) == PackageManager.PERMISSION_GRANTED;
	}

	@Override
	public void finishActivity() {
		mainActivity.finish();
	}

	@Override
	public void showSaveBeforeFinishDialog() {
		AppCompatDialogFragment dialog = SaveBeforeFinishDialog.newInstance(
				SaveBeforeFinishDialogType.FINISH);
		dialog.show(mainActivity.getSupportFragmentManager(), Constants.SAVE_QUESTION_FRAGMENT_TAG);
	}

	@Override
	public void showSaveBeforeNewImageDialog() {
		AppCompatDialogFragment dialog = SaveBeforeNewImageDialog.newInstance();
		dialog.show(mainActivity.getSupportFragmentManager(), Constants.SAVE_QUESTION_FRAGMENT_TAG);
	}

	@Override
	public void showSaveBeforeLoadImageDialog() {
		AppCompatDialogFragment dialog = SaveBeforeLoadImageDialog.newInstance();
		dialog.show(mainActivity.getSupportFragmentManager(), Constants.SAVE_QUESTION_FRAGMENT_TAG);
	}

	@Override
	public void showToolChangeToast(int offset, int idRes) {
		Toast toolNameToast = ToastFactory.makeText(mainActivity, idRes, Toast.LENGTH_SHORT);
		int gravity = Gravity.TOP | Gravity.CENTER;
		if (mainActivity.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
			offset = 0;
		}
		toolNameToast.setGravity(gravity, 0, offset);
		toolNameToast.show();
	}

	@Override
	public void broadcastAddPictureToGallery(Uri uri) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(uri);
		mainActivity.sendBroadcast(mediaScanIntent);
	}

	@Override
	public void restoreFragmentListeners() {
		FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(Constants.COLOR_PICKER_DIALOG_TAG);
		if (fragment != null) {
			setupColorPickerDialogListeners((ColorPickerDialog) fragment);
		}
	}
}
