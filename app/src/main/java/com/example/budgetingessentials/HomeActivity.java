package com.example.budgetingessentials;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity /*implements NavigationView.OnNavigationItemSelectedListener*/ {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    // Fragments
    private DashboardFragment dashboardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        frameLayout = findViewById(R.id.main_frame);

        dashboardFragment = new DashboardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new ExpenseFragment();

        setFragment(dashboardFragment);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dashboard:
                        setFragment(dashboardFragment);
                        DashboardFragment.isOpen = false;
                        //bottomNavigationView.setItemBackgroundResource(R.color.dashboard_color);
                        return true;
                    case R.id.income:
                        setFragment(incomeFragment);
                        DashboardFragment.isOpen = false;
                        //bottomNavigationView.setItemBackgroundResource(R.color.income_color);
                        return true;
                    case R.id.expense:
                        setFragment(expenseFragment);
                        DashboardFragment.isOpen = false;
                        //bottomNavigationView.setItemBackgroundResource(R.color.expense_color);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();

    }


}