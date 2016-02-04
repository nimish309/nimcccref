package com.coruscate.centrecourt.UserInterface.Activity;

import android.Manifest;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.fastaccess.permission.base.activity.BasePermissionActivity;
import com.fastaccess.permission.base.model.PermissionModel;

import java.util.ArrayList;
import java.util.List;

public class PermissionIntro extends BasePermissionActivity {

    @NonNull
    @Override
    protected List<PermissionModel> permissions() {
        List<PermissionModel> permissions = new ArrayList<>();
        PermissionModel model = getDefaultInstance();
        model.setCanSkip(true);
        model.setPermissionName(Manifest.permission.GET_ACCOUNTS);
        model.setTitle("GRANT CONTACT PERMISSION");
        model.setMessage("Contact permission is needed to access user's contacts and profile.");
        model.setExplanationMessage("Contact permission is needed to access user's contacts and profile.");
        model.setLayoutColor(getResources().getColor(R.color.actionbar));
        model.setImageResourceId(R.drawable.icon_contact_per);
        permissions.add(model);
        model = getDefaultInstance();
        model.setCanSkip(true);
        model.setPermissionName(Manifest.permission.CAMERA);
        model.setTitle("GRANT CAMERA PERMISSSION");
        model.setMessage("Camera permission is needed to access device camera.");
        model.setExplanationMessage("Camera permission is needed to access device camera.");
        model.setLayoutColor(getResources().getColor(R.color.loginFont));
        model.setImageResourceId(R.drawable.icon_camera_per);
        permissions.add(model);
        model = getDefaultInstance();
        model.setCanSkip(true);
        model.setPermissionName(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        model.setTitle("GRANT STORAGE PERMISSION");
        model.setMessage("Storage permission is needed to access shared external storage.");
        model.setExplanationMessage("Storage permission is needed to access shared external storage.");
        model.setLayoutColor(getResources().getColor(R.color.actionbar));
        model.setImageResourceId(R.drawable.icon_storage_per);
        permissions.add(model);
        return permissions;
    }

    @Override
    protected int theme() {
        return 0;
    }

    /**
     * Intro has finished.
     */
    @Override
    protected void onIntroFinished() {
        setResult(1);
        UserDataPreferences.saveIsFirstTime(this,true);
        this.finish();
    }

    @Nullable
    @Override
    protected ViewPager.PageTransformer pagerTransformer() {
        return null;
    }

    @NonNull
    @Override
    protected Boolean backPressIsEnabled() {
        return false;
    }

    /**
     * used to notify you that the permission is permanently denied. so you can decide whats next!
     *
     * @param permissionName
     */
    @Override
    protected void permissionIsPermanentlyDenied(String permissionName) {

    }

    /**
     * used to notify that the user ignored the permission
     * <p/>
     * note: if the {@link PermissionModel#isCanSkip()} return false, we could display the explanation immediately.
     *
     * @param permissionName
     */
    @Override
    protected void onUserDeclinePermission(String permissionName) {

    }

    private PermissionModel getDefaultInstance() {
        Resources res = getResources();
        PermissionModel model = new PermissionModel();
        model.setTextColor(Color.WHITE);
        model.setTextSize(res.getDimensionPixelSize(R.dimen.unit_16dp));
        model.setRequestIcon(R.drawable.ic_arrow_done);
        model.setPreviousIcon(R.drawable.ic_arrow_left);
        return model;
    }
}
