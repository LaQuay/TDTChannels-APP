package laquay.com.open.canalestdt.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SharedPreferencesController {
    public static final String TAG = SharedPreferencesController.class.getSimpleName();
    private static final String DEFAULT_SHARED_PREFERENCE = "MyDefSharedPreference";
    private static HashMap<String, SharedPreferencesController> preferenceControllerHashMap = new HashMap<>();
    private SharedPreferences sharedPreference;
    private Gson gson;

    private SharedPreferencesController(Context context, String name) {
        sharedPreference = context.getApplicationContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    private static SharedPreferencesController getSharedPreferenceController(Context context, String sharedPreferenceName) {
        if (preferenceControllerHashMap == null) preferenceControllerHashMap = new HashMap<>();
        // get or create new sharedPreferenceManager
        String name = TextUtils.isEmpty(sharedPreferenceName) ? DEFAULT_SHARED_PREFERENCE : sharedPreferenceName;
        SharedPreferencesController sharedPreferencesController = preferenceControllerHashMap.get(name);
        if (sharedPreferencesController == null) {
            sharedPreferencesController = new SharedPreferencesController(context, name);
            preferenceControllerHashMap.put(name, sharedPreferencesController);
        }
        return sharedPreferencesController;
    }

    /**
     * You have to initialize all your preference when open app.
     *
     * @param context : {@link Context}
     */
    public static synchronized void init(Context context) {
        getSharedPreferenceController(context, DEFAULT_SHARED_PREFERENCE);
    }

    /**
     * You have to initialize all your preference when open app.
     *
     * @param context : {@link Context}
     * @param names   : list of custom {@link SharedPreferences}
     */
    public static synchronized void init(Context context, String... names) {
        if (names == null || names.length == 0) return;
        for (String name : names) {
            getSharedPreferenceController(context, name);
        }
    }

    /**
     * Get {@link SharedPreferences} with name: name in {@link Context#MODE_PRIVATE}.
     *
     * @param name
     * @return
     */
    public static synchronized SharedPreferencesController getInstance(String name) {
        SharedPreferencesController sharedPreferencesController = preferenceControllerHashMap.get(name);
        if (sharedPreferencesController == null)
            throw new IllegalStateException("The share preference: " + name + " is not initialized before. You have to initialize it first by calling init(Context, boolean, String...) function");
        return sharedPreferencesController;
    }

    /**
     * Get default sharedPreference name: {@link #DEFAULT_SHARED_PREFERENCE}
     *
     * @return
     */
    public static synchronized SharedPreferencesController getInstance() {
        return getInstance(DEFAULT_SHARED_PREFERENCE);
    }

    /**
     * Easy to save your value here.
     *
     * @param key   : the key you want to save the object with
     * @param value : can be any kind of object (int, long, boolean, string, float, YourConvertableObject)
     * @return
     */
    public SharedPreferencesController putValue(String key, Object value) {
        SharedPreferences.Editor editor = sharedPreference.edit();

        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            String json = gson.toJson(value);
            editor.putString(key, json);
        }

        editor.commit();
        return this;
    }

    /**
     * Get value which is saved
     *
     * @param key
     * @param type
     * @param <T>
     * @return
     */
    public <T> T getValue(String key, Class<T> type) {
        if (type == Integer.class) {
            Integer value = sharedPreference.getInt(key, 0);
            return (T) value;
        } else if (type == Boolean.class) {
            Boolean value = sharedPreference.getBoolean(key, false);
            return (T) value;
        } else if (type == Float.class) {
            Float value = sharedPreference.getFloat(key, 0);
            return (T) value;
        } else if (type == String.class) {
            String value = sharedPreference.getString(key, "");
            return (T) value;
        } else if (type == Long.class) {
            Long value = sharedPreference.getLong(key, 0);
            return (T) value;
        } else {
            String json = sharedPreference.getString(key, "");
            if (TextUtils.isEmpty(json)) {
                return null;
            } else {
                T value = gson.fromJson(json, type);
                return value;
            }
        }
    }

    /**
     * Get value which is saved with a default value
     *
     * @param key
     * @param type
     * @param <T>  default value to be return if key is not added before
     * @return
     */
    public <T> T getValue(String key, Class<T> type, T defaultValue) {
        if (sharedPreference.contains(key)) {
            return getValue(key, type);
        } else {
            return defaultValue;
        }
    }

    /**
     * Get List of object which is saved in {@link SharedPreferences}
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> getValues(String key, Class<T[]> clazz) {
        String json = sharedPreference.getString(key, "");
        if (TextUtils.isEmpty(json)) return null;
        T[] values = gson.fromJson(json, clazz);
        List<T> list = new ArrayList<T>();
        list.addAll(Arrays.asList(values));
        return list;
    }


    /**
     * remove a key/value pair from shared preference
     *
     * @param key
     */
    public void remove(String key) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.remove(key).commit();
    }

    /**
     * Clear all the {@link SharedPreferences}
     */
    public void clear() {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.clear().commit();
    }

    /**
     * Get {@link SharedPreferences} of this manager if exists.
     *
     * @return SharedPreferences
     */
    public SharedPreferences getSharedPreference() {
        return this.sharedPreference;
    }
}
