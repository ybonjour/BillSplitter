/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Adjusted code from https://github.com/astuetz/PagerSlidingTabStrip
 */


package ch.pantas.billsplitter.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class FixedTabsView extends FrameLayout {

    private ViewPager viewPager;

    private PageListener pageListener;

    private OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;

    private int tabCount = 0;
    private int currentTabPosition = 0;
    private float currentPositionOffset = 0.0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    private int indicatorColor = 0xFF33B5E5;
    private int dividerColor = 0xAAFFFFFF;

    private boolean shouldExpand = false;
    private boolean textAllCaps = true;

    private int indicatorHeight = 10;
    private int dividerPadding = 25;
    private int dividerWidth = 1;

    private int tabTextSize = 14;
    private int tabTextColor = 0xFFFFFFFF;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.BOLD;

    public FixedTabsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pageListener = new PageListener();

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);
    }

    public void setViewPager(ViewPager viewPager) {
        checkNotNull(viewPager);

        this.viewPager = viewPager;
        viewPager.setOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        tabsContainer.removeAllViews();

        tabCount = viewPager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {
            addTab(i, viewPager.getAdapter().getPageTitle(i).toString());
        }

        updateTabStyles();
    }

    private void addTab(final int position, String title) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        tab.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f));

        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(position);
            }
        });
        tabsContainer.addView(tab, position);
    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {
            TextView v = (TextView) tabsContainer.getChildAt(i);

            TextView tab = (TextView) v;
            tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, tabTextSize);
            tab.setTypeface(tabTypeface, tabTypefaceStyle);
            tab.setTextColor(tabTextColor);

            tab.setAllCaps(textAllCaps);
        }

    }


    public void setOnPageChangeListener(OnPageChangeListener listener) {
        checkNotNull(listener);

        this.delegatePageListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int height = getHeight();

        rectPaint.setColor(indicatorColor);

        View currentTab = tabsContainer.getChildAt(currentTabPosition);
        if (currentTab != null) {
            float lineLeft = currentTab.getLeft();
            float lineRight = currentTab.getRight();

            if (currentPositionOffset > 0f && currentTabPosition < tabCount - 1) {

                View nextTab = tabsContainer.getChildAt(currentTabPosition + 1);
                final float nextTabLeft = nextTab.getLeft();
                final float nextTabRight = nextTab.getRight();

                lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
                lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
            }

            canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);

            dividerPaint.setColor(dividerColor);
            for (int i = 0; i < tabCount - 1; i++) {
                View tab = tabsContainer.getChildAt(i);
                canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
            }
        }

    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Position: page selected
            // positionOffset: 0.0-1.0 transition between pages
            // positionOffsetPixels: pixel offset from page

            currentTabPosition = position;
            currentPositionOffset = positionOffset;

            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {

            }
            else if (state == ViewPager.SCROLL_STATE_IDLE) {

            }
            else if (state == ViewPager.SCROLL_STATE_SETTLING) {

            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }

    }
}
