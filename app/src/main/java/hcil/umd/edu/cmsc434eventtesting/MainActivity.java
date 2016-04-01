package hcil.umd.edu.cmsc434eventtesting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Six color palette: https://color.adobe.com/Campfire-color-theme-2528696/
 */
public class MainActivity extends AppCompatActivity {

    private TouchEventHandler _touchEventHandler = new TouchEventHandler();
    private ClickHandler _clickHandler = new ClickHandler();

    private static final Map<Integer, String> _mapActionCodeToName;
    static
    {
        _mapActionCodeToName = new HashMap<Integer, String>();
        _mapActionCodeToName.put(MotionEvent.ACTION_DOWN, "ACTION_DOWN");
        _mapActionCodeToName.put(MotionEvent.ACTION_UP, "ACTION_UP");
        _mapActionCodeToName.put(MotionEvent.ACTION_MOVE, "ACTION_MOVE");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ViewGroup viewGroupRoot = (ViewGroup) findViewById(R.id.viewGroupRoot);

        // Simple check to see if we are in edit mode. If yes, don't do any of the listening stuff
        // (It was messing up the IDE for some reason and throwing an exception)
        if(!viewGroupRoot.isInEditMode()) {
            hookUpAllViewsToListeners(viewGroupRoot);
        }
    }

    /**
     * Recursively walks through all of the ViewGroup children and hooks up event listeners
     * @param parent
     */
    public void hookUpAllViewsToListeners(ViewGroup parent){
        int numChildren = parent.getChildCount();

        for(int i=0; i<numChildren; i++){
            View childView = parent.getChildAt(i);
            if(childView instanceof ViewGroup){
                hookUpAllViewsToListeners((ViewGroup) childView);
            }else if(!(childView instanceof ToggleButton)){ // we want to ignore toggle buttons
                childView.setOnTouchListener(_touchEventHandler);
                //childView.setOnClickListener(_clickHandler);
            }
        }
        parent.setOnTouchListener(_touchEventHandler);
       // parent.setOnClickListener(_clickHandler);
    }

    /**
     * Our TouchEventHandler class
     */
    private class TouchEventHandler implements View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            String idName = getResources().getResourceEntryName(v.getId());

            // See http://developer.android.com/reference/android/view/MotionEvent.html
            // for MotionEvent details
            float touchXPos = event.getX();
            float touchYPos = event.getY();

            // getRawX() and getRawY() return the original raw X coordinate of this event
            // (these coordinates are not local to current view but rather the screen)
            float rawXPos = event.getRawX();
            float rawYPos = event.getRawY();

            // Convert the action code to a string name
            String actionCodeName = "Unknown Action Code" + event.getAction();
            if(_mapActionCodeToName.containsKey(event.getAction())){
                actionCodeName = _mapActionCodeToName.get(event.getAction());
            }

            // Create the toast message
            String touchMessage = MessageFormat.format("{0}.onTouch: {1} [X,Y]=[{2}, {3}] Raw [X,Y]=[{4}, {5}]",
                    idName, actionCodeName, (int)touchXPos, (int)touchYPos, (int)rawXPos, (int)rawYPos);
            Toast.makeText(getApplicationContext(), touchMessage, Toast.LENGTH_SHORT).show();

            // Figure out if this event occurred in a ViewGroup and, if so, whether the handled toggle
            // button is on or off. If the toggle button is 'on', then we return 'true' for the event
            // being handled (and the event will not propagate up the Window Tree). If the toggle button
            // is 'off', then we return 'false' for the event being handled and the event will
            // propagate up the Window Tree.
            boolean eventHandled = false;
            if(v instanceof ViewGroup){
                ViewGroup vg = ((ViewGroup) v);

                // Get the children of this View Group and look for a toggle button
                int numChildren = vg.getChildCount();
                for(int i=0; i<numChildren; i++) {
                    View childView = vg.getChildAt(i);
                    if (childView instanceof ToggleButton) {
                        ToggleButton toggleButton = (ToggleButton) childView;
                        eventHandled = toggleButton.isChecked();
                    }
                }
            }

            return eventHandled;
        }
    }

    /**
     * Not current hooked up
     */
    private class ClickHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String idName = getResources().getResourceEntryName(v.getId());
            Toast.makeText(getApplicationContext(), idName + ".onClick", Toast.LENGTH_SHORT).show();
        }
    }
}
