package ch.pantas.billsplitter.framework;

import android.app.Activity;
import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;

import com.google.inject.Stage;

import static ch.pantas.billsplitter.framework.AbstractModuleFactory.EMPTY_MODULES_ARRAY;
import static com.google.inject.util.Modules.override;
import static java.lang.Thread.currentThread;
import static org.mockito.MockitoAnnotations.initMocks;
import static roboguice.RoboGuice.destroyInjector;
import static roboguice.RoboGuice.getInjector;
import static roboguice.RoboGuice.newDefaultRoboModule;
import static roboguice.RoboGuice.setBaseApplicationInjector;

public abstract class BaseEspressoTest<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    protected Application application;

    public BaseEspressoTest(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        currentThread().setContextClassLoader(getClass().getClassLoader());
        application = (Application) getInstrumentation().getTargetContext().getApplicationContext();
        initMocks(this);
        setBaseApplicationInjector(application, Stage.DEVELOPMENT,
                override(newDefaultRoboModule(application)).with(getMockModule()));
        getInjector(application).injectMembers(this);
    }


    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        destroyInjector(application);
        setBaseApplicationInjector(application, Stage.DEVELOPMENT,
                override(newDefaultRoboModule(application)).with(EMPTY_MODULES_ARRAY));
        application = null;
    }

    private com.google.inject.Module getMockModule() {
        return AbstractModuleFactory.getAbstractModuleFactory().createModule(this, BaseEspressoTest.class);
    }
}

