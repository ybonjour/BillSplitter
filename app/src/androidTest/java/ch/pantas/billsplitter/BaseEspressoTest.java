package ch.pantas.billsplitter;

import android.app.*;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.test.ActivityInstrumentationTestCase2;
import com.google.inject.*;
import org.mockito.Mock;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.google.inject.util.Modules.override;
import static java.lang.Thread.currentThread;
import static org.mockito.MockitoAnnotations.initMocks;
import static roboguice.RoboGuice.*;

public abstract class BaseEspressoTest<U, T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    private static final Module[] EMPTY_MODULES_ARRAY = new Module[0];

    protected Application application;
    protected T activity;

    public BaseEspressoTest(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        currentThread().setContextClassLoader(getClass().getClassLoader());
        initRoboGuice();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        destroyInjector(application);
        setBaseApplicationInjector(application, Stage.DEVELOPMENT,
                override(newDefaultRoboModule(application)).with(EMPTY_MODULES_ARRAY));
        application = null;
        activity = null;
    }

    protected AbstractModule getMockModule() throws IllegalAccessException {
        return new MockModule(getListOfMocks(getInstance()));
    }

    protected abstract U getInstance();

    private List<Object> getListOfMocks(Object test) throws IllegalAccessException {
        List<Object> objects = new ArrayList<Object>();
        List<Field> fields = getFieldsWithMockAnnotation(test.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            objects.add(field.get(test));
        }

        return objects;
    }

    private List<Field> getFieldsWithMockAnnotation(Class<?> clazz) {
        Class<?> currentClass = clazz;
        List<Field> fields = new ArrayList<Field>();
        while (true) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (field.getAnnotation(Mock.class) != null) fields.add(field);
            }

            if (currentClass == BaseEspressoTest.class) break;
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }

    private void initRoboGuice() throws IllegalAccessException {
        application = (Application) getInstrumentation().getTargetContext().getApplicationContext();
        initMocks(getInstance());
        setBaseApplicationInjector(application, Stage.DEVELOPMENT,
                override(newDefaultRoboModule(application)).with(getMockModule()));
        getInjector(application).injectMembers(this);
    }

    class MockModule extends AbstractModule {

        private List<Object> mocksToInject;

        public MockModule(List<Object> mocksToInject) {
            this.mocksToInject = mocksToInject;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void configure() {
            bind(Executor.class).toProvider(ExecutorProvider.class);

            for (final Object mock : mocksToInject) {
                Class clazz = mock.getClass();
                bind(clazz.getSuperclass()).toInstance(mock);
            }
        }
    }
}
