package de.effnerapp.effner.ui.fragments.timetable;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.utils.ApiClient;
import de.effnerapp.effner.ui.models.timetableview.Schedule;
import de.effnerapp.effner.ui.models.timetableview.Time;
import de.effnerapp.effner.ui.models.timetableview.TimetableView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimetableFragment extends Fragment {


    public TimetableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        Context context = requireContext();

        TimetableView timetable = view.findViewById(R.id.timetable);
        TextView timetableInfoText = view.findViewById(R.id.timetable_info_text);

        try {
            SimpleDateFormat originFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
            Date lastUpdate = originFormat.parse(ApiClient.getInstance().getData().getTimetable().getCreatedAt());
            assert lastUpdate != null;
            String text = "Zuletzt aktualisiert: " + targetFormat.format(lastUpdate);
            timetableInfoText.setText(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[][] data = new Gson().fromJson(ApiClient.getInstance().getData().getTimetable().getValue(), String[][].class);

        if (data != null && data.length != 0) {
            ArrayList<Schedule> schedules = new ArrayList<>();

            int dayI = 0;
            for (String[] day : data) {
                for (int i = 1; i <= 10; i++) {
                    String lesson = day[i];
                    if (!lesson.isEmpty()) {
                        Schedule schedule = new Schedule();
                        schedule.setSubject(lesson);
                        schedule.setDay(dayI);
                        schedule.setStartTime(new Time(i, 0));
                        schedule.setEndTime(new Time(i + 1, 0));
                        schedule.setBackgroundColor(ApiClient.getInstance().getData().getColorByKey("COLOR_TIMETABLE_" + lesson).getColorValue());
                        schedules.add(schedule);
                    }
                }

                dayI++;
            }
            timetable.add(schedules);
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.d_timetable_unavailable_title)
                    .setMessage(R.string.d_timetable_unavailable_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.d_button_timetable_upload, (dialogInterface, i) -> uploadTimetable())
                    .setNegativeButton(R.string.button_cancel, (dialogInterface, i) -> {
                        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);

                        // workaround to uncheck the currently selected item
                        navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
                        navView.getMenu().getItem(0).setCheckable(false);

                        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

                        // navigate to news fragment
                        navController.navigate(R.id.navigation_home);
                    });
            dialog.show();
        }
        return view;
    }

    private void uploadTimetable() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, APP_CAMERA_PERMISSION_REQUEST_CODE);
//        } else {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////            intent.putExtra(MediaStore.EXTRA_OUTPUT, )
//            startActivityForResult(intent, APP_CAMERA_PICTURE_ID);
//        }
    }
}