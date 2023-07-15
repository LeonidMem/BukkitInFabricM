package ru.leonidm.bukkitinfabric.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class OpenUnsafe {

    private static final Unsafe UNSAFE;

    static {
        try {
            Class<Unsafe> unsafeClass = Unsafe.class;
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);

            UNSAFE = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private OpenUnsafe() {

    }

    public static Unsafe get() {
        return UNSAFE;
    }
}
