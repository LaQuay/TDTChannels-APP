package laquay.com.canalestdt;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {
    public static final String TAG = AboutFragment.class.getSimpleName();
    private View rootView;
    private TextView appVersionTV;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about, container, false);

        setUpElements();
        setUpListeners();

        setVersion();

        return rootView;
    }

    private void setVersion() {
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

            appVersionTV.setText("v" + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setUpElements() {
        appVersionTV = rootView.findViewById(R.id.fragment_about_version_name_tv);
    }

    private void setUpListeners() {

    }
}
