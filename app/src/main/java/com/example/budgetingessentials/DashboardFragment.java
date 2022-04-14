package com.example.budgetingessentials;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    // Floating action button
    private FloatingActionButton fab_main;
    private FloatingActionButton fab_income;
    private FloatingActionButton fab_expense;

    // Floating action button text
    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    private boolean isOpen = false;

    // Floating action button animation
    private Animation fadeOpen, fadeClose;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Connect floating buttons and texts to layout
        fab_main = myView.findViewById(R.id.fb_main_btn);
        fab_income = myView.findViewById(R.id.income_ft_btn);
        fab_expense = myView.findViewById(R.id.expense_ft_btn);
        fab_income_txt = myView.findViewById(R.id.income_ft_text);
        fab_expense_txt = myView.findViewById(R.id.expense_ft_text);

        // Animations
        fadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        fadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen){
                    fab_income.startAnimation(fadeClose);
                    fab_expense.startAnimation(fadeClose);
                    fab_income.setClickable(false);
                    fab_expense.setClickable(false);

                    fab_income_txt.startAnimation(fadeClose);
                    fab_expense_txt.startAnimation(fadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;

                }else {
                    fab_income.startAnimation(fadeOpen);
                    fab_expense.startAnimation(fadeOpen);
                    fab_income.setClickable(true);
                    fab_expense.setClickable(true);

                    fab_income_txt.startAnimation(fadeOpen);
                    fab_expense_txt.startAnimation(fadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;
                }

            }
        });

        return myView;
    }
}