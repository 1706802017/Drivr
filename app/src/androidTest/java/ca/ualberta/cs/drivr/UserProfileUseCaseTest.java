/*
 * Copyright 2016 CMPUT301F16T10
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package ca.ualberta.cs.drivr;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.robotium.solo.Solo;

/**
 * Created by Daniel on 2016-11-27.
 */

public class UserProfileUseCaseTest extends ActivityInstrumentationTestCase2<MainActivity> {
   /*
    * Testing for user stories related to user profiles
    *
    * For:
    * 03.01.01:
    *   "As a user, I want a profile with a unique username and my contact information."
    * 03.02.01:
    *   "As a user, I want to edit the contact information in my profile."
    * 03.03.01:
    *   "As a user, I want to, when a username is presented for a thing, retrieve and show its contact information."
    * 03.04.01:
    *  "As a driver, in my profile I can provide details about the vehicle I drive."
    *
    */

    private Solo solo;

    public UserProfileUseCaseTest() {
        super(ca.ualberta.cs.drivr.MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

   /*
    * 03.01.01:
    *   "As a user, I want a profile with a unique username and my contact information."
    */
//    Test works but didn't want to create a "new user" every time. So it's just commented out
//    public void testAddingNewUser() {
//        solo.assertCurrentActivity("Expected MainActivity", MainActivity.class);
//
//        int[] location = new int[2];
//        View view = solo.getView(R.id.forTesting);
//        view.getLocationOnScreen(location);
//        int x= location[0];
//        int y= location[1];
//        solo.clickOnScreen(x,y);
//        View login = getActivity().findViewById(R.id.main_fab_login);
//        solo.clickOnView(login);
//        solo.assertCurrentActivity("Expected LoginActivity", LoginActivity.class);
//
//        TextView signup = (TextView)solo.getView(R.id.login_sign_up_text);
//        solo.clickOnView(signup);
//
//        solo.enterText((EditText) solo.getView(R.id.login_username), "aaaaa");
//        solo.enterText((EditText) solo.getView (R.id.login_name), "aaaaa");
//        solo.enterText((EditText) solo.getView (R.id.login_email), "aaaa@");
//        solo.enterText((EditText) solo.getView (R.id.login_phone), "1112223333");
//        solo.clickOnButton ("Sign Up");
//
//    }

    /*
    * 03.02.01:
    *   "As a user, I want to edit the contact information in my profile."
    */
    public void testEditUser() {
        solo.assertCurrentActivity("Expected MainActivity", MainActivity.class);
        int[] location = new int[2];
        View view = solo.getView(R.id.forTesting);
        view.getLocationOnScreen(location);
        int x= location[0];
        int y= location[1];
        solo.clickOnScreen(x,y);
        View login = getActivity().findViewById(R.id.main_fab_login);
        solo.clickOnView(login);
        solo.assertCurrentActivity("Expected LoginActivity", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.login_username), "aaaaa");
        solo.clickOnText("Sign In");

        solo.assertCurrentActivity("Expected MainActivity", MainActivity.class);
        solo.clickOnScreen(x,y);
        View profile = getActivity().findViewById(R.id.main_fab_profile);
        solo.clickOnView(profile);

        View editProfile = solo.getView(R.id.profile_edit_icon);
        solo.clickOnView(editProfile);
        solo.clearEditText((EditText) solo.getView(R.id.profile_username_edit_text));
        solo.clearEditText((EditText) solo.getView (R.id.profile_name_edit_text));
        solo.clearEditText((EditText) solo.getView (R.id.profile_email_edit_text));
        solo.clearEditText((EditText) solo.getView (R.id.profile_phone_number_edit_text));
        solo.enterText((EditText) solo.getView(R.id.profile_username_edit_text), "aaaab");
        solo.enterText((EditText) solo.getView (R.id.profile_name_edit_text), "aaaab");
        solo.enterText((EditText) solo.getView (R.id.profile_email_edit_text), "aaaab@");
        solo.enterText((EditText) solo.getView (R.id.profile_phone_number_edit_text), "1112233333");
        solo.clickOnView(editProfile);
    }


   /*
    * 03.04.01:
    *   "As a driver, in my profile I can provide details about the vehicle I drive."
    */
    public void testSwitchDriver() {
        solo.assertCurrentActivity("Expected MainActivity", MainActivity.class);
        int[] location = new int[2];
        View view = solo.getView(R.id.forTesting);
        view.getLocationOnScreen(location);
        int x= location[0];
        int y= location[1];
        solo.clickOnScreen(x,y);
        View driverMode = getActivity().findViewById(R.id.main_driver_mode);
        solo.clickOnView(driverMode);
        solo.enterText((EditText) solo.getView(R.id.main_keyword_edit_text), "aaaaa");
        solo.assertCurrentActivity("Expected MainActivity", MainActivity.class);
    }
}

