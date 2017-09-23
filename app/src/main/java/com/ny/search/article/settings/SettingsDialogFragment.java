package com.ny.search.article.settings;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ny.search.article.R;
import com.ny.search.article.models.Filter;
import com.ny.search.article.storage.FilterSettingsStorage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * To set search filter settings
 *
 * @author tejalpar
 */
public class SettingsDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private boolean isDateSet;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDayOfMonth;
    private TextView beginDateTextView;
    private EditText sortOrderTextView;
    private Spinner sortOrderSpinner;
    private CheckBox artsCheckBox;
    private CheckBox fashionCheckBox;
    private CheckBox sportsCheckBox;

    public SettingsDialogFragment() {}

    public static SettingsDialogFragment newInstance(String dialogTitle) {
        SettingsDialogFragment frag = new SettingsDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", dialogTitle);
        frag.setArguments(args);
        return frag;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_dialog_layout, container);

        final Context context = view.getContext();
        final DatePickerDialog datePickerDialog = getDatePickerDialog(view.getContext());
        final ImageButton addDateButton = (ImageButton) view.findViewById(R.id.add_date_button);
        beginDateTextView = (TextView) view.findViewById(R.id.begin_date_input_text);
        sortOrderSpinner = (Spinner) view.findViewById(R.id.sort_order_spinner);
        artsCheckBox = (CheckBox) view.findViewById(R.id.arts_news_desk_option);
        fashionCheckBox = (CheckBox) view.findViewById(R.id.fashion_news_desk_option);
        sportsCheckBox = (CheckBox) view.findViewById(R.id.sports_news_desk_option);
        setupSortOrderSpinner(context);

        final Filter savedSettings = loadSettingsFromSharedPreference(context);
        addDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open date picker dialog
                if (isDateSet || savedSettings != null) {
                    datePickerDialog.updateDate(selectedYear, selectedMonth, selectedDayOfMonth);
                }
                datePickerDialog.show();
            }
        });

        final Button saveButton = (Button) view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save to preference
                FilterSettingsStorage.getInstance(context).saveFilter(getSelectedFilterSettings());
                dismissDialog();
            }
        });

        final Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        return view;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        selectedYear = year;
        selectedMonth = month;
        selectedDayOfMonth = dayOfMonth;
        isDateSet = true;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        beginDateTextView.setText(simpleDateFormat.format(new Date(year - 1900, month, dayOfMonth)));
    }

    private String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return simpleDateFormat.format(date);
    }

    private DatePickerDialog getDatePickerDialog(Context context) {
        Date date = new Date();
        return new DatePickerDialog(context, this, date.getYear() + 1900, date.getMonth(), date.getDay());
    }

    private Filter getSelectedFilterSettings() {
        Filter filter = new Filter();
        filter.setDateInMillis(new Date(selectedYear - 1900, selectedMonth, selectedDayOfMonth).getTime());
        filter.setSortOrder(sortOrderSpinner.getSelectedItem().toString());

        HashMap<String, Boolean> newsDeskMap = new HashMap<>();
        newsDeskMap.put(Filter.ARTS_KEY, artsCheckBox.isChecked());
        newsDeskMap.put(Filter.FASHION_KEY, fashionCheckBox.isChecked());
        newsDeskMap.put(Filter.SPORTS_KEY, sportsCheckBox.isChecked());
        filter.setNewsDeskMap(newsDeskMap);
        return filter;
    }

    private Filter loadSettingsFromSharedPreference(Context context) {
        Filter filter = FilterSettingsStorage.getInstance(context).retrieveFilter();

        if (filter.isFilterSet()) {
            Date selectedDate = new Date(filter.getDateInMillis());
            beginDateTextView.setText(getFormattedDate(selectedDate));
            // cos selectedDate.getYear() has - 1990
            selectedYear = selectedDate.getYear() + 1900;
            selectedMonth = selectedDate.getMonth();
            selectedDayOfMonth = selectedDate.getDate();

            // sort order
            setSpinnerSelection(filter.getSortOrder());

            // news desk
            HashMap<String, Boolean> newsDeskMap = filter.getNewsDeskMap();
            artsCheckBox.setChecked(newsDeskMap.get(Filter.ARTS_KEY));
            fashionCheckBox.setChecked(newsDeskMap.get(Filter.FASHION_KEY));
            sportsCheckBox.setChecked(newsDeskMap.get(Filter.SPORTS_KEY));

            return filter;
        }
        return null;
    }

    private void setupSortOrderSpinner(Context context) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.sort_order_options, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortOrderSpinner.setAdapter(adapter);
    }

    private void setSpinnerSelection(String value) {
        int index = 0;
        SpinnerAdapter adapter = sortOrderSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break;
            }
        }
        sortOrderSpinner.setSelection(index);
    }

    private void dismissDialog() {
        dismiss();
    }
}
