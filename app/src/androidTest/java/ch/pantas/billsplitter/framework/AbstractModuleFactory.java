package ch.pantas.billsplitter.framework;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import org.mockito.Mock;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static com.google.inject.util.Modules.override;

public class AbstractModuleFactory {

    public static final Module[] EMPTY_MODULES_ARRAY = new Module[0];

    private static AbstractModuleFactory instance;

    private AbstractModuleFactory() {
    }

    public static AbstractModuleFactory getAbstractModuleFactory() {
        if (instance == null) instance = new AbstractModuleFactory();

        return instance;
    }

    public Module createModule(Object test, Class baseClazz) {
        return createModule(test, baseClazz, null);
    }

    public Module createModule(Object test, Class baseClazz, Module defaultModule) {
        Module module = new MockModule(getListOfMocks(test, baseClazz));
        if(defaultModule == null) return module;

        return override(module).with(defaultModule);
    }

    private static List<Object> getListOfMocks(Object test, Class baseClazz) {
        List<Object> objects = new ArrayList<Object>();
        List<Field> fields = getFieldsWithMockAnnotation(test.getClass(), baseClazz);
        for (Field field : fields) {
            Object value = getFieldValueFromObject(field, test);
            if (value != null) objects.add(value);
        }

        return objects;
    }

    private static Object getFieldValueFromObject(Field field, Object object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private static List<Field> getFieldsWithMockAnnotation(Class<?> fromClazz, Class<?> toClazz) {
        Class<?> currentClass = fromClazz;
        List<Field> fields = new ArrayList<Field>();
        while (true) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (field.getAnnotation(Mock.class) != null) fields.add(field);
            }

            if (currentClass == toClazz) break;
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }


    private class MockModule extends AbstractModule {

        private List<Object> mocksToInject;

        public MockModule(List<Object> mocksToInject) {
            this.mocksToInject = mocksToInject;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void configure() {
            for (final Object mock : mocksToInject) {
                Class clazz = mock.getClass();
                Class mockingClazz = clazz.getSuperclass().equals(Proxy.class) ? clazz : clazz.getSuperclass();
                bind(mockingClazz).toInstance(mock);
            }
        }
    }
}
