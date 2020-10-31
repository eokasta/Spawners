package com.github.eokasta.spawners.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

    public static final String serverVersion = null;
    private static Field FIELD_DOT_MODIFIERS;

    static {
        try {
            Class.forName("org.bukkit.Bukkit");
            FIELD_DOT_MODIFIERS = Field.class.getDeclaredField("modifiers");
            FIELD_DOT_MODIFIERS.setAccessible(true);
            setObject(Reflection.class, null, "serverVersion", Bukkit.getServer().getClass().getPackage().getName()
                    .substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1));
        } catch (Exception exception) {
        }
    }

    public static void makeAccessible(Field field) {
        try {
            field.setAccessible(true);


            FIELD_DOT_MODIFIERS.setInt(field, field.getModifiers() & 0xFFFFFFEF);
        } catch (Exception e) {

            throw asRuntimeException(e);
        }
    }

    public static void makeAccessible(Method method) {
        try {
            method.setAccessible(true);
        } catch (Exception e) {

            throw asRuntimeException(e);
        }
    }

    public static void makeAccessible(Constructor<?> constructor) {
        try {
            constructor.setAccessible(true);
        } catch (Exception e) {
            throw asRuntimeException(e);
        }
    }

    public static RuntimeException asRuntimeException(Throwable t) {
        if (t instanceof RuntimeException) return (RuntimeException) t;

        if (t instanceof InvocationTargetException)
            return asRuntimeException(((InvocationTargetException) t).getCause());


        return new IllegalStateException(t.getClass().getSimpleName() + ": " + t.getMessage());
    }

    public static Class<?> getCraftPlayer() throws Exception {
        return getBukkitClass("entity.CraftPlayer");
    }

    public static Class<?> getPacket() throws Exception {
        return getNMSClass("Packet");
    }

    public static Object getHandle(Player p) throws Exception {
        return getMethod(getCraftPlayer(), "getHandle").invoke(p, new Object[0]);
    }

    public static Object getConnection(Player p) throws Exception {
        Object handle = getHandle(p);
        return handle.getClass().getField("playerConnection").get(handle);
    }

    public static void sendPacket(Player p, Object packet) throws Exception {
        Object connection = getConnection(p);
        connection.getClass().getMethod("sendPacket", new Class[]{getPacket()}).invoke(connection, new Object[]{packet});
    }

    public static Class<?> getBukkitClass(String clazz) throws Exception {
        return Class.forName("org.bukkit.craftbukkit." + serverVersion + "." + clazz);
    }

    public static Class<?> getBungeeClass(String path, String clazz) throws Exception {
        return Class.forName("net.md_5.bungee." + path + "." + clazz);
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... args) throws Exception {
        Constructor<?> c = clazz.getConstructor(args);
        c.setAccessible(true);
        return c;
    }

    public static Enum<?> getEnum(Class<?> clazz, String constant) throws Exception {
        Class<?> c = Class.forName(clazz.getName());
        @SuppressWarnings("rawtypes")
        Enum[] arrayOfEnum = (Enum[]) c.getEnumConstants();
        for (Enum<?> e : arrayOfEnum) {
            if (e.name().equalsIgnoreCase(constant))
                return e;
        }
        throw new Exception("Enum constant not found " + constant);
    }

    public static Enum<?> getEnum(Class<?> clazz, String enumname, String constant) throws Exception {
        Class<?> c = Class.forName(clazz.getName() + "$" + enumname);
        @SuppressWarnings("rawtypes")
        Enum[] arrayOfEnum = (Enum[]) c.getEnumConstants();
        for (Enum<?> e : arrayOfEnum) {
            if (e.name().equalsIgnoreCase(constant))
                return e;
        }
        throw new Exception("Enum constant not found " + constant);
    }

    public static Field getField(Class<?> clazz, String fname) throws Exception {
        Field f = null;
        try {
            f = clazz.getDeclaredField(fname);
        } catch (Exception e) {
            f = clazz.getField(fname);
        }
        setFieldAccessible(f);
        return f;
    }

    public static Object getFirstObject(Class<?> clazz, Class<?> objclass, Object instance) throws Exception {
        Field f = null;
        for (Field fi : clazz.getDeclaredFields()) {
            if (fi.getType().equals(objclass)) {
                f = fi;
                break;
            }
        }
        if (f == null)
            for (Field fi : clazz.getFields()) {
                if (fi.getType().equals(objclass)) {
                    f = fi;
                    break;
                }
            }
        setFieldAccessible(f);
        return f.get(instance);
    }

    public static Method getMethod(Class<?> clazz, String mname) throws Exception {
        Method m = null;
        try {
            m = clazz.getDeclaredMethod(mname, new Class[0]);
        } catch (Exception e) {
            try {
                m = clazz.getMethod(mname, new Class[0]);
            } catch (Exception ex) {
                return m;
            }
        }
        m.setAccessible(true);
        return m;
    }

    public static <T> Field getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);
                return field;
            }
        }

        if (target.getSuperclass() != null)
            return getField(target.getSuperclass(), name, fieldType, index);
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }

    public static Method getMethod(Class<?> clazz, String mname, Class<?>... args) throws Exception {
        Method m = null;
        try {
            m = clazz.getDeclaredMethod(mname, args);
        } catch (Exception e) {
            try {
                m = clazz.getMethod(mname, args);
            } catch (Exception ex) {
                return m;
            }
        }
        m.setAccessible(true);
        return m;
    }

    public static String getServerVersion() {
        return serverVersion;
    }

    public static Class<?> getNMSClass(String clazz) throws Exception {
        return Class.forName("net.minecraft.server." + serverVersion + "." + clazz);
    }

    public static Object getObject(Class<?> clazz, Object obj, String fname) throws Exception {
        return getField(clazz, fname).get(obj);
    }

    public static Object getObject(Object obj, String fname) throws Exception {
        return getField(obj.getClass(), fname).get(obj);
    }

    public static Object invokeConstructor(Class<?> clazz, Class<?>[] args, Object... initargs) throws Exception {
        return getConstructor(clazz, args).newInstance(initargs);
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method) throws Exception {
        return getMethod(clazz, method).invoke(obj, new Object[0]);
    }


    public static Object invokeMethod(Class<?> clazz, Object obj, String method, Class<?>[] args, Object... initargs) throws Exception {
        return getMethod(clazz, method, args).invoke(obj, initargs);
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method, Object... initargs) throws Exception {
        return getMethod(clazz, method).invoke(obj, initargs);
    }

    public static Object invokeMethod(Object obj, String method) throws Exception {
        return getMethod(obj.getClass(), method).invoke(obj, new Object[0]);
    }

    public static Object invokeMethod(Object obj, String method, Object[] initargs) throws Exception {
        return getMethod(obj.getClass(), method).invoke(obj, initargs);
    }

    public static void setFieldAccessible(Field f) throws Exception {
        f.setAccessible(true);
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(f, f.getModifiers() & 0xFFFFFFEF);
    }

    public static void setObject(Class<?> clazz, Object obj, String fname, Object value) throws Exception {
        getField(clazz, fname).set(obj, value);
    }

    public static void setObject(Object obj, String fname, Object value) throws Exception {
        getField(obj.getClass(), fname).set(obj, value);
    }
}