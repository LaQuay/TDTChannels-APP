package laquay.com.canalestdt.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import laquay.com.canalestdt.R;

public class AboutTitleTextView extends AppCompatTextView {

    public AboutTitleTextView(Context context) {
        super(context);

        init(context, null);
    }

    public AboutTitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public AboutTitleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setTextSize(18);
        setTypeface(null, Typeface.BOLD);
        setTextColor(getResources().getColor(R.color.blue_900));
        setPadding(0, 40, 0, 20);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}