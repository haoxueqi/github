package com.ebupt.yuebox.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ebupt.yuebox.R;
import com.ebupt.yuebox.adapter.SectionOrderAdapter;
import com.ebupt.yuebox.adapter.SectionUniversalAdapter;
import com.ebupt.yuebox.adapter.SectionUniversalAdapter.HasMorePagesListener;


 

public class SectionOrderListView extends ListView implements HasMorePagesListener {
	public static final String TAG = SectionOrderListView.class.getSimpleName();
	
	View listFooter;
	boolean footerViewAttached = false;

    private View mHeaderView;
    private boolean mHeaderViewVisible;

    private int mHeaderViewWidth;
    private int mHeaderViewHeight;

    private SectionUniversalAdapter adapter;
    
    public void setPinnedHeaderView(View view) {
        mHeaderView = view;
        mHeaderView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.v(TAG, "cliked!");
				TextView tv_header = (TextView) mHeaderView.findViewById(R.id.tv_header);
				((SectionOrderAdapter) adapter).hideOrShow(tv_header.getText().toString());
			}
		});
        // Disable vertical fading when the pinned header is present
        // TODO change ListView to allow separate measures for top and bottom fading edge;
        // in this particular case we would like to disable the top, but not the bottom edge.
        if (mHeaderView != null) {
            setFadingEdgeLength(0);
        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mHeaderView != null) {
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
            configureHeaderView(getFirstVisiblePosition());
        }
    }

    public void configureHeaderView(int position) {
        if (mHeaderView == null) {
            return;
        }

        int state = adapter.getPinnedHeaderState(position);
        switch (state) {
            case SectionUniversalAdapter.PINNED_HEADER_GONE: {
                mHeaderViewVisible = false;
                break;
            }

            case SectionUniversalAdapter.PINNED_HEADER_VISIBLE: {
            	adapter.configurePinnedHeader(mHeaderView, position, 255);
                if (mHeaderView.getTop() != 0) {
                    mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                }
                mHeaderViewVisible = true;
                break;
            }

            case SectionUniversalAdapter.PINNED_HEADER_PUSHED_UP: {
                View firstView = getChildAt(0);
                if (firstView != null) {
	                int bottom = firstView.getBottom();
	                int headerHeight = mHeaderView.getHeight();
	                int y;
	                int alpha;
	                if (bottom < headerHeight) {
	                    y = (bottom - headerHeight);
	                    alpha = 255 * (headerHeight + y) / headerHeight;
	                } else {
	                    y = 0;
	                    alpha = 255;
	                }
	                adapter.configurePinnedHeader(mHeaderView, position, alpha);
	                if (mHeaderView.getTop() != y) {
	                    mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
	                }
	                mHeaderViewVisible = true;
                }
                break;
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderViewVisible) {
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
    	// TODO Auto-generated method stub
    	if(ev.getAction() == MotionEvent.ACTION_UP && ev.getY() < mHeaderViewHeight)
    	{
    		TextView tv_header = (TextView) mHeaderView.findViewById(R.id.tv_header);
    		((SectionOrderAdapter) adapter).hideOrShow(tv_header.getText().toString());
    		return true;
    	}
    	return super.dispatchTouchEvent(ev);
    }
    
    public SectionOrderListView(Context context) {
        super(context);
    }

    public SectionOrderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SectionOrderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void setLoadingView(View listFooter) {
		this.listFooter = listFooter;
	}
    
    public View getLoadingView() {
		return listFooter;
	}
    
    @Override
    public void setAdapter(ListAdapter adapter) {
    	if (!(adapter instanceof SectionUniversalAdapter)) {
    		throw new IllegalArgumentException(SectionOrderListView.class.getSimpleName() + " must use adapter of type " + SectionUniversalAdapter.class.getSimpleName());
    	}
    	
    	// previous adapter
    	if (this.adapter != null) {
    		this.adapter.setHasMorePagesListener(null);
    		this.setOnScrollListener(null);
    	}
    	
    	this.adapter = (SectionUniversalAdapter) adapter;
    	((SectionUniversalAdapter)adapter).setHasMorePagesListener(this);
		this.setOnScrollListener((SectionUniversalAdapter) adapter);
    	
		View dummy = new View(getContext());
    	super.addFooterView(dummy);
    	super.setAdapter(adapter);
    	super.removeFooterView(dummy);
    }
    
    @Override
    public SectionUniversalAdapter getAdapter() {
    	return adapter;
    }

	@Override
	public void noMorePages() {
		if (listFooter != null) {
			this.removeFooterView(listFooter);
		}
		footerViewAttached = false;
	}

	@Override
	public void mayHaveMorePages() {
		if (! footerViewAttached && listFooter != null) {
			this.addFooterView(listFooter);
			footerViewAttached = true;
		}
	}
	
	public boolean isLoadingViewVisible() {
		return footerViewAttached;
	}
}
