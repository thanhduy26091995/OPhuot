package com.thanhduy.ophuot.main.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.database.DatabaseAdapter;
import com.thanhduy.ophuot.profile.view.ProfileUserActivity;
import com.thanhduy.ophuot.profile.view.ProfileUserFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseAdapter databaseAdapter;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private View headerView;
    private ImageView imgAvatar;

    //tag using for fragment
    private static final String TAG_PROFILE = "profile";
    public static String CURRENT_TAG = TAG_PROFILE;
    private String[] activityTitles;
    private Handler mHandler;
    public static int navItemIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHandler = new Handler();

        databaseAdapter = new DatabaseAdapter(this);
        //  databaseAdapter.copyDatabase();
        databaseAdapter.copyDatabase();
        //createData();

//        if (FirebaseAuth.getInstance().getCurrentUser() == null){
//            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
//
//        }
        initViews();
        setUpNavigationView();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //end init
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        imgAvatar = (ImageView) headerView.findViewById(R.id.img_avatar);
        //init title
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        //event click avatar
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navItemIndex = 0;
               // loadFragment();
                startActivity(new Intent(MainActivity.this, ProfileUserActivity.class));
            }
        });
    }

    //set title for toolbar
    private void setToolbarTitle() {
        // txtTitle.setText(activityTitles[navItemIndex]);
    }

    private void loadFragment() {
        //set toolbar title
        setToolbarTitle();
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
//        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
//            drawer.closeDrawers();
//            // show or hide the fab button
//
//            return;
//        }
        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                //overridePendingTransition(R.anim.comming_in_right, R.anim.comming_out_right);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawerLayout.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getFragment() {
        switch (navItemIndex) {
            case 0: {
                ProfileUserFragment profileUserFragment = new ProfileUserFragment();
                return profileUserFragment;
            }
            default:
                return new ProfileUserFragment();
        }
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.nav_home_page:
//                        isClickLogout = false;
//                        linearAddFriend.setVisibility(View.GONE);
//                        CURRENT_TAG = TAG_HOME;
//                        navItemIndex = 0;
//                        break;
//                    case R.id.nav_contacts:
//                        isClickLogout = false;
//                        linearAddFriend.setVisibility(View.VISIBLE);
//                        CURRENT_TAG = TAG_CONTACTS_REQUESTS;
//                        navItemIndex = 1;
//                        break;
//                    case R.id.nav_messages:
//                        isClickLogout = false;
//                        linearAddFriend.setVisibility(View.GONE);
//                        CURRENT_TAG = TAG_MESSAGE;
//                        navItemIndex = 2;
//                        break;
//                    case R.id.nav_room:
//                        isClickLogout = false;
//                        linearAddFriend.setVisibility(View.GONE);
//                        CURRENT_TAG = TAG_CHAT_ROOM;
//                        navItemIndex = 3;
//                        break;
//                    case R.id.nav_member_around:
//                        isClickLogout = false;
//                        linearAddFriend.setVisibility(View.GONE);
//                        CURRENT_TAG = TAG_MEMBER_AROUND;
//                        navItemIndex = 4;
//                        break;
////                    case R.id.nav_discount:
////                        isClickLogout = false;
////                        linearAddFriend.setVisibility(View.GONE);
////                        CURRENT_TAG = TAG_DISCOUNT;
////                        navItemIndex = 5;
////                        break;
//                    case R.id.nav_news:
//                        isClickLogout = false;
//                        linearAddFriend.setVisibility(View.GONE);
//                        startActivity(new Intent(MainActivity.this, BrowserActivity.class));
//                        drawer.closeDrawers();
//                        return true;
//                    case R.id.nav_events:
//                        isClickLogout = false;
//                        linearAddFriend.setVisibility(View.GONE);
//                        CURRENT_TAG = TAG_EVENTS;
//                        navItemIndex = 7;
//                        break;
//                    case R.id.nav_settings:
//                        isClickLogout = false;
//                        linearAddFriend.setVisibility(View.GONE);
//                        CURRENT_TAG = TAG_SETTING;
//                        navItemIndex = 8;
//                        break;
//                    case R.id.nav_logout:
//                        isClickLogout = true;
//                        //show alert xác nhận
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                        builder.setMessage(R.string.confirmLogOut)
//                                .setCancelable(false)
//                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        logOut();
//                                        //initInfo();
//                                    }
//                                })
//                                .setNegativeButton(R.string.huy, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.cancel();
//                                    }
//                                });
//                        builder.create().show();
//                        break;
//                    default:
//                        isClickLogout = false;
//                        navItemIndex = 0;
//                }
//                //checking if the item is in checked or not, if not make it in checked state
//                if (item.isChecked()) {
//                    item.setChecked(false);
//                } else {
//                    item.setChecked(true);
//                }
//                item.setChecked(true);
//                if (!isClickLogout) {
//                    loadHomeFragment();
//                }
//
//
//                return true;
//            }
//
//        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
