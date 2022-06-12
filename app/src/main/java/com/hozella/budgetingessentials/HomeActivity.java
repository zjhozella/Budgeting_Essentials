package com.hozella.budgetingessentials;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;



import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    // Fragments
    private DashboardFragment dashboardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;

    // Global Total Expense and Income
    public static int expenseTotalSum = 0;
    public static int incomeTotalSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.app_toolbar);
        toolbar.setTitle("Expense Manager");
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        frameLayout = findViewById(R.id.main_frame);

        dashboardFragment = new DashboardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new ExpenseFragment();

        setFragment(dashboardFragment);

        // Controls where each navigation button takes you
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

    @Override
    public void onBackPressed(){

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }else {
            super.onBackPressed();
        }
    }

    public void displaySelectedListener(int itemId){
        Fragment fragment = null;

        switch (itemId){
            case R.id.nav_dashboard:
                setFragment(dashboardFragment);
                DashboardFragment.isOpen = false;
                break;

            case R.id.nav_income:
                setFragment(incomeFragment);
                DashboardFragment.isOpen = false;
                break;

            case R.id.nav_expense:
                setFragment(expenseFragment);
                DashboardFragment.isOpen = false;
                break;

            case R.id.nav_logout:

                break;
        }

        if (fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());

        return true;
    }
}