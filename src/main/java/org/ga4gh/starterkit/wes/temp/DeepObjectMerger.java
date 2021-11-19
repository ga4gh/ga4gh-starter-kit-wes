package org.ga4gh.starterkit.wes.temp;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import org.ga4gh.starterkit.common.config.LogLevel;

public class DeepObjectMerger {

    private static final Set<Class<?>> DEFAULT_CLASSES_TO_SET = new HashSet<>(Arrays.asList(
        boolean.class,
        byte.class,
        short.class,
        char.class,
        int.class,
        long.class,
        float.class,
        double.class,
        List.class,
        ArrayList.class,
        String.class,
        LocalDateTime.class,
        LogLevel.class
    ));

    private Set<Class<?>> classesToSet;

    public DeepObjectMerger() {
        classesToSet = new HashSet<>();
        classesToSet.addAll(DEFAULT_CLASSES_TO_SET);
    }

    public DeepObjectMerger(boolean useDefaults) {
        classesToSet = new HashSet<>();
        if (useDefaults) {
            classesToSet.addAll(DEFAULT_CLASSES_TO_SET);
        }
    }

    public void merge(Object source, Object target) {
        // Through reflection, get all fields associated with object to be merged
        Class<?> objectClass = source.getClass();
        List<Field> fields = new ArrayList<>(Arrays.asList(objectClass.getDeclaredFields()));

        // Traverse the class's super classes until there are no more
        // super classes in order to merge properties defined in the super
        Class<?> superClass = objectClass.getSuperclass();
        while (superClass != null) {
            List<Field> superClassFields = new ArrayList<>(Arrays.asList(superClass.getDeclaredFields()));
            fields.addAll(superClassFields);
            superClass = superClass.getSuperclass();
        }

        for (Field field : fields) {
            // Get field and set as accessible so it can be written
            Class<?> fieldClass = field.getType();
            field.setAccessible(true);

            try {
                Object sourceProperty = field.get(source);
                Object targetProperty = field.get(target);

                // If the field is a 'primitive' type, then the target field
                // can be set with the source field value (provided source
                // field value is not null)
                if (getClassesToSet().contains(fieldClass)) {
                    if (sourceProperty != null) {
                        field.set(target, sourceProperty);
                    }
                } else {
                    // If the field is a complex class (i.e. non-primitive),
                    // and both source and target are non-null, then
                    // this method is recursively called on the field value
                    if (sourceProperty != null && targetProperty != null) {
                        merge(sourceProperty, targetProperty);
                    }
                    // Null target field can be set with source field value
                    else if (sourceProperty != null) {
                        field.set(target, sourceProperty);
                    }
                }
            } catch (IllegalAccessException e) {
                System.out.println("illegal access exception");
            }
        }
    }

    public Set<Class<?>> getClassesToSet() {
        return classesToSet;
    }

    public void addClassToSet(Class<?> classToSet) {
        classesToSet.add(classToSet);
    }

    public void addClassesToSet(Set<Class<?>> classesToSet) {
        this.classesToSet.addAll(classesToSet);
    }
}
