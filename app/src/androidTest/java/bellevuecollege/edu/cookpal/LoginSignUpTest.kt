package bellevuecollege.edu.cookpal

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
//import androidx.test.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import bellevuecollege.edu.cookpal.profile.LoginFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.After
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {
    private lateinit var scenario: FragmentScenario<LoginFragment>
    //mock NavController
    private val mockNavController = mock(NavController::class.java)


    @Before
    fun setUp() {
        scenario = launchFragmentInContainer<LoginFragment>()
        //set the NavController property on the fragment
        scenario.onFragment {fragment -> Navigation.setViewNavController(fragment.requireView(), mockNavController)}
    }

    @After
    fun tearDown() {
        scenario.close()
        Thread.sleep(1000L)
    }

    @Test
    @Throws(Exception::class)
    fun signup_user_success() {
        onView(withId(R.id.emailAddress))
            .perform(ViewActions.typeText("test_abc_123@hotmail.com"))
            .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.password))
            .perform(ViewActions.typeText("Asdf123$"))
            .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.signUpButton))
            .perform(click())
    }

    @Test
    @Throws(Exception::class)
    fun login_user_success() {
        onView(withId(R.id.emailAddress))
            .perform(ViewActions.typeText("test_abc_123@hotmail.com"))
            .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.password))
            .perform(ViewActions.typeText("Asdf123$"))
            .perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.loginButton))
            .perform(click())
    }


}