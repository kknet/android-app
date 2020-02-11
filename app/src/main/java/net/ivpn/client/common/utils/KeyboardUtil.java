package net.ivpn.client.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import java.util.HashMap;

public class KeyboardUtil implements ViewTreeObserver.OnGlobalLayoutListener {
    private final static int MAGIC_NUMBER = 200;

    private SoftKeyboardToggleListener callback;
    private View rootView;
    private Boolean prevValue = null;
    private float screenDensity;
    private static HashMap<SoftKeyboardToggleListener, KeyboardUtil> listeners = new HashMap<>();

    public interface SoftKeyboardToggleListener {
        void onToggleSoftKeyboard(boolean isVisible);
    }

    @Override
    public void onGlobalLayout() {
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);

        int heightDiff = rootView.getRootView().getHeight() - (rect.bottom - rect.top);
        float dp = heightDiff / screenDensity;
        boolean isVisible = dp > MAGIC_NUMBER;

        if (callback != null && (prevValue == null || isVisible != prevValue)) {
            prevValue = isVisible;
            callback.onToggleSoftKeyboard(isVisible);
        }
    }

    /**
     * Add a new keyboard listener
     *
     * @param activity calling activity
     * @param listener callback
     */
    public static void addKeyboardToggleListener(Activity activity, SoftKeyboardToggleListener listener) {
        removeKeyboardToggleListener(listener);

        listeners.put(listener, new KeyboardUtil(activity, listener));
    }

    /**
     * Remove a registered listener
     *
     * @param listener {@link SoftKeyboardToggleListener}
     */
    public static void removeKeyboardToggleListener(SoftKeyboardToggleListener listener) {
        if (listeners.containsKey(listener)) {
            KeyboardUtil util = listeners.get(listener);
            util.removeListener();

            listeners.remove(listener);
        }
    }

    /**
     * Remove all registered keyboard listeners
     */
    public static void removeAllKeyboardToggleListeners() {
        for (SoftKeyboardToggleListener l : listeners.keySet())
            listeners.get(l).removeListener();

        listeners.clear();
    }

    /**
     * Manually toggle soft keyboard visibility
     *
     * @param context calling context
     */
    public static void toggleKeyboardVisibility(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * Force closes the soft keyboard
     *
     * @param activeView the view with the keyboard focus
     */
    public static void forceCloseKeyboard(View activeView) {
        InputMethodManager inputMethodManager = (InputMethodManager) activeView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null)
            inputMethodManager.hideSoftInputFromWindow(activeView.getWindowToken(), 0);
    }

    private void removeListener() {
        callback = null;

        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private KeyboardUtil(Activity act, SoftKeyboardToggleListener listener) {
        callback = listener;

        rootView = ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        screenDensity = act.getResources().getDisplayMetrics().density;
    }

}