package com.thanhduy.ophuot.main.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.thanhduy.ophuot.R;
import com.thanhduy.ophuot.base.BaseActivity;
import com.thanhduy.ophuot.base.DeviceToken;
import com.thanhduy.ophuot.base.GoogleAuthController;
import com.thanhduy.ophuot.base.ImageLoader;
import com.thanhduy.ophuot.base.InternetConnection;
import com.thanhduy.ophuot.chat_list.view.ChatListFragment;
import com.thanhduy.ophuot.config.view.ConfigFragment;
import com.thanhduy.ophuot.database.SqlLiteDbHelper;
import com.thanhduy.ophuot.database.model.SearchResult;
import com.thanhduy.ophuot.favorite.view.FavoriteFragment;
import com.thanhduy.ophuot.featured.view.FeaturedFragment;
import com.thanhduy.ophuot.help.HelpFragment;
import com.thanhduy.ophuot.login_and_register.view.LoginActivity;
import com.thanhduy.ophuot.main.presenter.MainPresenter;
import com.thanhduy.ophuot.model.User;
import com.thanhduy.ophuot.my_homestay.view.MyHomestayFragment;
import com.thanhduy.ophuot.profile.view.ProfileUserActivity;
import com.thanhduy.ophuot.search.view.SearchFragment;
import com.thanhduy.ophuot.support.view.SupportFragment;
import com.thanhduy.ophuot.utils.Constants;
import com.thanhduy.ophuot.utils.SessionManagerUser;
import com.thanhduy.ophuot.utils.ShowAlertDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    //private DatabaseAdapter databaseAdapter;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private View headerView;
    private ImageView imgAvatar;
    private TextView txtName, txtEmail;
    public static Activity mainActivity;
    private LinearLayout linearContain;

    //tag using for fragment
    private static final String TAG_FEATURED = "featured";
    private static final String TAG_SEARCH = "search";
    private static final String TAG_FAVORITE = "favorite";
    private static final String TAG_SHARE = "share";
    private static final String TAG_SUPPORT = "support";
    private static final String TAG_INBOX = "inbox";
    private static final String TAG_MY_HOMESTAY = "myHomestay";
    private static final String TAG_CONFIG = "config";
    private static final String TAG_HELP = "help";

    public static String CURRENT_TAG = TAG_INBOX;
    private String[] activityTitles;
    private Handler mHandler;
    public static int navItemIndex = 0;
    private DatabaseReference mDatabase;
    private boolean isShowIconLogOut = false;
    private boolean isClickLogout = false;
    private boolean exit = false;
    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private SessionManagerUser sessionManagerUser;

    private ProgressBar progressBar;
    private MainPresenter presenter;
    private SqlLiteDbHelper sqlLiteDbHelper;
    private List<SearchResult> searchResults = new ArrayList<>();
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init
        sqlLiteDbHelper = new SqlLiteDbHelper(this);
        mainActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHandler = new Handler();
        sessionManagerUser = new SessionManagerUser(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        presenter = new MainPresenter(this);
        //createData();
        initViews();
        setUpNavigationView();
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_FEATURED;
            loadFragment();
        }
        initData();
    }

    private void initData() {
        if (InternetConnection.getInstance().isOnline(MainActivity.this)) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                hideItemLogOut();
                progressBar.setVisibility(View.GONE);
                linearContain.setVisibility(View.VISIBLE);
            } else {
                String token = FirebaseInstanceId.getInstance().getToken();
                DeviceToken.getInstance().addDeviceToken(mDatabase, getUid(), token);
                showItemLogOut();
                showDataUserIntoHeader();
            }
        } else {
            progressBar.setVisibility(View.GONE);
            linearContain.setVisibility(View.VISIBLE);
            Snackbar snackbar = Snackbar.make(drawerLayout, getResources().getString(R.string.noInternet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initData();
                        }
                    });
            snackbar.show();
        }
    }


    private void hideItemLogOut() {
        //set boolean
        isShowIconLogOut = false;
        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.nav_sign_out).setVisible(false);
    }

    //
    private void showItemSearch() {
        if (mMenu != null) {
            mMenu.findItem(R.id.action_search).setVisible(true);
        }
    }


    private void hideItemSearch() {
        if (mMenu != null) {
            mMenu.findItem(R.id.action_search).setVisible(false);
        }
    }

    //
    private void showItemLogOut() {
        isShowIconLogOut = true;
        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.nav_sign_out).setVisible(true);
    }


    private void showDataUserIntoHeader() {
        progressBar.setVisibility(View.VISIBLE);
        linearContain.setVisibility(View.GONE);
        //showProgessDialog();
        mDatabase.child(Constants.USERS).child(getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    ImageLoader.getInstance().loadImageAvatar(MainActivity.this, user.getAvatar(), imgAvatar);
                    txtName.setText(user.getName());
                    txtEmail.setText(user.getEmail());
                    sessionManagerUser.createLoginSession(user);
                }
                //hide progress dialog
                //  hideProgressDialog();

                progressBar.setVisibility(View.GONE);
                linearContain.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError.getCode() == -3) {
                    ShowAlertDialog.showAlert(getResources().getString(R.string.accountBlocked), MainActivity.this);
                }
                hideProgressDialog();
            }
        });
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //end init
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        imgAvatar = (ImageView) headerView.findViewById(R.id.img_avatar);
        txtName = (TextView) headerView.findViewById(R.id.txt_header_name);
        txtEmail = (TextView) headerView.findViewById(R.id.txt_header_email);
        progressBar = (ProgressBar) headerView.findViewById(R.id.progress_bar);
        linearContain = (LinearLayout) headerView.findViewById(R.id.linear_contain);
        //init title
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        //event click avatar
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    navItemIndex = 0;
                    // loadFragment();
                    startActivity(new Intent(MainActivity.this, ProfileUserActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }

            }
        });
    }

    //set title for toolbar
    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void loadFragment() {
        if (CURRENT_TAG.equals(TAG_SEARCH)) {
            showItemSearch();
        } else {
            hideItemSearch();
        }
        selectNavMenu();
        //set toolbar title
        setToolbarTitle();
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawerLayout.closeDrawers();
            // show or hide the fab button

            return;
        }
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
        //  invalidateOptionsMenu();

    }

    private Fragment getFragment() {
        switch (navItemIndex) {
            case 0: {
                FeaturedFragment featuredFragment = new FeaturedFragment();
                return featuredFragment;
            }
            case 1: {
                SearchFragment searchFragment = new SearchFragment();
                return searchFragment;
            }
            case 2: {
                FavoriteFragment favoriteFragment = new FavoriteFragment();
                return favoriteFragment;
            }
            case 3: {
                SupportFragment supportFragment = new SupportFragment();
                return supportFragment;
            }
            case 4: {
                ChatListFragment chatListFragment = new ChatListFragment();
                return chatListFragment;
            }
            case 5: {
                MyHomestayFragment myHomestayFragment = new MyHomestayFragment();
                return myHomestayFragment;
            }
            case 6: {
                ConfigFragment configFragment = new ConfigFragment();
                return configFragment;
            }
            case 7: {
                HelpFragment helpFragment = new HelpFragment();
                return helpFragment;
            }
            default:
                return new FeaturedFragment();
        }
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_best:
                        isClickLogout = false;
                        CURRENT_TAG = TAG_FEATURED;
                        navItemIndex = 0;
                        break;
                    case R.id.nav_search:
                        isClickLogout = false;
                        CURRENT_TAG = TAG_SEARCH;
                        navItemIndex = 1;
                        break;
                    case R.id.nav_favorite:
                        isClickLogout = false;
                        CURRENT_TAG = TAG_FAVORITE;
                        navItemIndex = 2;
                        break;
                    case R.id.nav_support:
                        isClickLogout = false;
                        CURRENT_TAG = TAG_SUPPORT;
                        navItemIndex = 3;
                        break;
                    case R.id.nav_inbox:
                        isClickLogout = false;
                        CURRENT_TAG = TAG_INBOX;
                        navItemIndex = 4;
                        break;
                    case R.id.nav_my_post:
                        isClickLogout = false;
                        CURRENT_TAG = TAG_MY_HOMESTAY;
                        navItemIndex = 5;
                        break;
                    case R.id.nav_config:
                        isClickLogout = false;
                        CURRENT_TAG = TAG_CONFIG;
                        navItemIndex = 6;
                        break;
                    case R.id.nav_help:
                        isClickLogout = false;
                        CURRENT_TAG = TAG_HELP;
                        navItemIndex = 7;
                        break;
                    case R.id.nav_sign_out:
                        isClickLogout = true;
                        //show alert xác nhận
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(R.string.confirmLogOut)
                                .setCancelable(false)
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        logOut();
                                        sessionManagerUser.logoutUser();
                                        //initInfo();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                        builder.create().show();
                        break;
                    default:
                        //  isClickLogout = false;
                        navItemIndex = 0;
                }
                //checking if the item is in checked or not, if not make it in checked state
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                item.setChecked(true);
                if (!isClickLogout) {
                    loadFragment();
                }


                return true;
            }

        });
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


    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        if (GoogleAuthController.getInstance().getGoogleApiClient() != null) {
            presenter.logOut(GoogleAuthController.getInstance().getGoogleApiClient());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAuthController.install(this, this);
    }

    public void moveToMainActivity() {
        Intent refresh = new Intent(MainActivity.this, MainActivity.class);
        startActivity(refresh);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_FEATURED;
                loadFragment();
                return;
            } else {
                if (exit) {
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.pressBackAgain), Toast.LENGTH_LONG).show();
                    exit = true;
                    //sau 3s thì set exit thành false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = false;
                        }
                    }, 3000);
                }
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_view_item, menu);
        MenuItem menuItemSearch = menu.findItem(R.id.action_search);

        final SearchView searchViewActionBar = (SearchView) MenuItemCompat.getActionView(menuItemSearch);

        searchViewActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    if (SearchFragment.searchFragment != null) {
                        searchResults.clear();
                        SearchFragment.searchFragment.handleSearch(true, searchResults);
                    }

                } else {
                    if (SearchFragment.searchFragment != null) {
                        searchResults = sqlLiteDbHelper.searchProvinceOrDistrict(newText);
                        SearchFragment.searchFragment.handleSearch(false, searchResults);

                    }
                }
                return true;
            }
        });
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_search) {
//            Intent intent = new Intent(MainActivity.this, SearchFillTextActivity.class);
//            startActivity(intent);
//        }

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
