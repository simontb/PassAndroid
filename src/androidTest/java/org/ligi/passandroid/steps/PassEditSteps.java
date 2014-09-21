package org.ligi.passandroid.steps;

import org.ligi.passandroid.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.swipeLeft;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

public class PassEditSteps {
    public static void goToMetaData() {
        onView(withId(R.id.passEditPager)).perform(swipeLeft());
    }

    public static void goToImages() {
        goToMetaData();
        onView(withId(R.id.passEditPager)).perform(swipeLeft());

    }

    public static void goToColor() {
        goToImages();
        onView(withId(R.id.passEditPager)).perform(swipeLeft());
    }


    public static void goToBarCode() {
        goToColor();
        onView(withId(R.id.passEditPager)).perform(swipeLeft());
    }
}
