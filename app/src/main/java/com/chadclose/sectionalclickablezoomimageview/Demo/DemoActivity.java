package com.chadclose.sectionalclickablezoomimageview.Demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.chadclose.sectionalclickablezoomimageview.R;
import com.chadclose.sectionalclickablezoomimageview.SectionalClickableZoomImageView;


public class DemoActivity extends Activity implements SectionalClickableZoomImageView.SectionClickListener {


    private SectionalClickableZoomImageView scziv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        scziv = (SectionalClickableZoomImageView) findViewById(R.id.scziv);

        scziv.init(R.drawable.image_default, R.drawable.image_areas,
                new int[] {Color.argb(255, 255, 0, 0), Color.argb(255,0,0,255), Color.argb(255,0,255,0)},
                new int[] {R.drawable.image_pressed_1, R.drawable.image_pressed_2, R.drawable.image_pressed_3},
                this);

    }


    @Override
    public void onSectionClick(int section) {
        Log.d("Section", "Pressed: " + (section + 1));
    }

}