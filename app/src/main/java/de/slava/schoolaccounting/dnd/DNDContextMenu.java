package de.slava.schoolaccounting.dnd;

import android.graphics.Point;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import de.slava.schoolaccounting.Main;

/**
 * @author by V.Sysoltsev
 */
public class DNDContextMenu<T> implements DNDStateListener<T> {
    private final View node;
    private final RelativeLayout layoutToAttachContextMenu;

    public DNDContextMenu(View node, MotionEvent event, RelativeLayout layoutToAttachContextMenu, DNDObject<T> dndObject) {
        this.node = node;
        this.layoutToAttachContextMenu = layoutToAttachContextMenu;
        Point p = getRelativePosition(layoutToAttachContextMenu, event);
        dndObject.addDragStateListener(this);
        open(p);
    }

    private void open(Point position) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = position.x;
        params.topMargin = position.y;
        layoutToAttachContextMenu.addView(node, params);
        Log.d(Main.getTag(), String.format("open at %d, %d", params.leftMargin, params.topMargin));
    }

    private void close() {
        layoutToAttachContextMenu.removeView(node);
        Log.d(Main.getTag(), "Closing");
    }

    protected Point getRelativePosition(View v, MotionEvent event) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        float screenX = event.getRawX();
        float screenY = event.getRawY();
        float viewX = screenX - location[0];
        float viewY = screenY - location[1];
        return new Point((int) viewX, (int) viewY);
    }

    @Override
    public void onStateChanges(DNDObject<T> object, int oldState, int newState) {
        switch (newState) {
            case DragEvent.ACTION_DRAG_ENDED:
                close();
                break;
        }
    }
}
