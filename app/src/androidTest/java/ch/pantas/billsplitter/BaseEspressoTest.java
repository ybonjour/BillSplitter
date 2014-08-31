package ch.pantas.billsplitter;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

public abstract class BaseEspressoTest<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    public BaseEspressoTest(Class<T> activityClass) {
        super(activityClass);
    }
}
