package ch.pantas.billsplitter.framework;

import android.app.Application;
import android.test.InstrumentationTestCase;

import com.google.inject.Module;
import com.google.inject.Stage;

import ch.pantas.billsplitter.dataaccess.GenericStore;
import ch.pantas.billsplitter.model.Attendee;

import static ch.pantas.billsplitter.framework.AbstractModuleFactory.EMPTY_MODULES_ARRAY;
import static ch.pantas.billsplitter.framework.AbstractModuleFactory.getAbstractModuleFactory;
import static com.google.inject.util.Modules.override;
import static java.lang.Thread.currentThread;
import static org.mockito.MockitoAnnotations.initMocks;
import static roboguice.RoboGuice.destroyInjector;
import static roboguice.RoboGuice.getInjector;
import static roboguice.RoboGuice.newDefaultRoboModule;
import static roboguice.RoboGuice.setBaseApplicationInjector;

public abstract class BaseMockitoInstrumentationTest extends InstrumentationTestCase {

    protected Application application;

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

    private Module getMockModule() {
        return getAbstractModuleFactory().createModule(this, BaseMockitoInstrumentationTest.class, getDefaultModule());
    }

    protected Module getDefaultModule(){
        return null;
    }
}
