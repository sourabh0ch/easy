package com.easy.easychat.activity;

import android.app.ProgressDialog;
import android.arch.core.executor.TaskExecutor;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.easy.easychat.R;
import com.easy.easychat.adapter.ViewPagerAdapter;
import com.easy.easychat.entity.BottomNavigationBehavior;
import com.easy.easychat.fragment.ContactFragment;
import com.easy.easychat.fragment.ConversationFragment;
import com.easy.easychat.fragment.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {
    private ActionBar toolbar;
    Context context;
    private ImageView ivNext;
    ImageView ivLogOut;
    private ViewPager viewPager;
    BottomNavigationView navigation;
    MenuItem prevMenuItem;
    private FirebaseAuth auth;
    private DatabaseReference mDatabaseReference;
    ConversationFragment conversationFragment;
    ContactFragment contactFragment;
    ProfileFragment profileFragment;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            inflateToolBar();
            initOnclickListener();
            context = HomeActivity.this;
            auth = FirebaseAuth.getInstance();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            progressDialog = new ProgressDialog(this);
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            // attaching bottom sheet behaviour - hide / show on scroll
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
            layoutParams.setBehavior(new BottomNavigationBehavior());

            // load the store fragment by default
            // toolbar.setTitle("Shop");
            loadFragment(new ConversationFragment());


            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (prevMenuItem != null) {
                        prevMenuItem.setChecked(false);
                    } else {
                        navigation.getMenu().getItem(0).setChecked(false);
                    }
                    Log.d("page", "onPageSelected: " + position);
                    navigation.getMenu().getItem(position).setChecked(true);
                    prevMenuItem = navigation.getMenu().getItem(position);

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            setupViewPager(viewPager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        conversationFragment = new ConversationFragment();
        contactFragment = new ContactFragment();
        profileFragment = new ProfileFragment();
        adapter.addFragment(conversationFragment);
        adapter.addFragment(contactFragment);
        adapter.addFragment(profileFragment);
        viewPager.setAdapter(adapter);
    }

    private void inflateToolBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.main__toolbar1);
        ivLogOut = (ImageView) mToolbar.findViewById(R.id.ivLogOut);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_chat:
                    viewPager.setCurrentItem(0);
                    break;

                case R.id.navigation_contact:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.navigation_Profile:

                    viewPager.setCurrentItem(2);
                    break;
            }

            return false;
        }
    };

    private void initOnclickListener() {
        ivLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUp(view);
            }
        });
    }

    private void popUp(View view) {
        PopupMenu menu = new PopupMenu(HomeActivity.this, view);
        menu.getMenuInflater().inflate(R.menu.pop_up_menu, menu.getMenu());
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.logout) {
                    progressDialog.setTitle("Logging out");
                    progressDialog.setMessage("Please wait while you are logging out");
                    progressDialog.show();
                    signOutServiceCall();
                }
                return true;
            }
        });
    }


    /**
     * loading fragment into FrameLayout
     *
     * @param fragment
     */
    public void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
        transaction.addToBackStack(null);
    }


    private void signOutServiceCall() {
        try {
            FirebaseAuth.getInstance().signOut();
            context.startActivity(new Intent(context, LoginWithEmailAndPassword.class));
            finish();
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}