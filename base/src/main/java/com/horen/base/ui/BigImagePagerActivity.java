package com.horen.base.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.horen.base.R;
import com.horen.base.app.GlideApp;
import com.horen.base.base.BaseActivity;
import com.horen.base.util.GlideFileHelper;
import com.horen.base.widget.SheetDialog;
import com.horen.base.widget.ViewPagerFixed;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @author :ChenYangYi
 * @date :2018/08/13/10:43
 * @description :查看大图，支持本地资源和网络图片
 * @github :https://github.com/chenyy0708
 */
public class BigImagePagerActivity extends BaseActivity {
    public static final String INTENT_IMGURLS = "imgurls";
    public static final String INTENT_LIST = "imageResouceId";
    public static final String INTENT_POSITION = "position";
    private List<View> guideViewList = new ArrayList<View>();
    private LinearLayout guideGroup;
    private GlideFileHelper glideFileHelper;
    private ViewPager viewPager;

    public static void startImagePagerActivity(Activity activity, List<String> imgUrls, int position) {
        Intent intent = new Intent(activity, BigImagePagerActivity.class);
        intent.putStringArrayListExtra(INTENT_IMGURLS, new ArrayList<String>(imgUrls));
        intent.putExtra(INTENT_POSITION, position);
        activity.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void startImageForResouceId(Activity activity, List<Integer> imageResouceIds, int position, ImageView imageView) {
        Intent intent = new Intent(activity, BigImagePagerActivity.class);
        Bundle options = ActivityOptions.makeSceneTransitionAnimation(
                activity, imageView, "shareImage").toBundle();
        intent.putIntegerArrayListExtra(INTENT_LIST, new ArrayList<Integer>(imageResouceIds));
        intent.putExtra(INTENT_POSITION, position);
        activity.startActivity(intent, options);
    }

    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BigImagePagerActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_pager;
    }

    @Override
    protected void setFitsSystemWindows() {
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // 隐藏系统状态栏
        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        // 保存图片工具类
        glideFileHelper = new GlideFileHelper(mContext);
        viewPager = (ViewPagerFixed) findViewById(R.id.pager);
        guideGroup = findViewById(R.id.guideGroup);
        int startPos = getIntent().getIntExtra(INTENT_POSITION, 0);
        ArrayList<String> imgUrls = getIntent().getStringArrayListExtra(INTENT_IMGURLS);
        ArrayList<Integer> imageResouceIds = getIntent().getIntegerArrayListExtra(INTENT_LIST);

        ImageAdapter mAdapter = new ImageAdapter(this);
        mAdapter.setDatas(imgUrls, imageResouceIds);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < guideViewList.size(); i++) {
                    guideViewList.get(i).setSelected(i == position ? true : false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(startPos);

        addGuideView(guideGroup, startPos, imgUrls, imageResouceIds);
    }

    @Override
    public void initPresenter() {

    }

    private void addGuideView(LinearLayout guideGroup, int startPos, ArrayList<String> imgUrls, ArrayList<Integer> imageResouceIds) {
        // 大于一张图片添加引导点
        if (imgUrls != null && imgUrls.size() > 1) {
            guideViewList.clear();
            for (int i = 0; i < imgUrls.size(); i++) {
                View view = new View(this);
                view.setBackgroundResource(R.drawable.selector_guide_bg);
                view.setSelected(i == startPos ? true : false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.gudieview_width),
                        getResources().getDimensionPixelSize(R.dimen.gudieview_heigh));
                layoutParams.setMargins(10, 0, 0, 0);
                guideGroup.addView(view, layoutParams);
                guideViewList.add(view);
            }
        }
        // 大于一张图片添加引导点
        if (imageResouceIds != null && imageResouceIds.size() > 1) {
            guideViewList.clear();
            for (int i = 0; i < imageResouceIds.size(); i++) {
                View view = new View(this);
                view.setBackgroundResource(R.drawable.selector_guide_bg);
                view.setSelected(i == startPos ? true : false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.gudieview_width),
                        getResources().getDimensionPixelSize(R.dimen.gudieview_heigh));
                layoutParams.setMargins(10, 0, 0, 0);
                guideGroup.addView(view, layoutParams);
                guideViewList.add(view);
            }
        }
    }

    private class ImageAdapter extends PagerAdapter {

        private List<String> datas;
        private List<Integer> imgRIds;
        private LayoutInflater inflater;
        private Context context;

        public void setDatas(List<String> datas, List<Integer> iamgeRIds) {
            if (datas != null && datas.size() > 0) {
                this.datas = datas;
            }
            if (iamgeRIds != null && iamgeRIds.size() > 0) {
                this.imgRIds = iamgeRIds;
            }
        }

        public ImageAdapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            if (datas != null && datas.size() > 0) {
                return datas.size();
            }
            if (imgRIds != null && imgRIds.size() > 0) {
                return imgRIds.size();
            }
            return 0;
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = inflater.inflate(R.layout.item_pager_image, container, false);
            if (view != null) {
                final PhotoView imageView = (PhotoView) view.findViewById(R.id.image);
                // 点击图片
                imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        BigImagePagerActivity.this.finish();
                    }
                });

                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final SheetDialog dialog = new SheetDialog(mContext, new String[]{"保存至相册"}, null);
                        dialog.isTitleShow(false)
                                .cancelText(getString(R.string.disagree));
//                                .show();

                        dialog.setOnOperItemClickL(new OnOperItemClickL() {
                            @Override
                            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (datas != null && datas.size() > 0) {
                                    glideFileHelper.savePicture(System.currentTimeMillis() + ".png", datas.get(viewPager.getCurrentItem()));
                                }
                                if (imgRIds != null && imgRIds.size() > 0) {
                                    glideFileHelper.savePicture(System.currentTimeMillis() + ".png", imgRIds.get(viewPager.getCurrentItem()));
                                }
                                dialog.dismiss();
                            }
                        });

                        return true;
                    }
                });


                //loading
                final ProgressBar loading = new ProgressBar(context);
                FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                loadingLayoutParams.gravity = Gravity.CENTER;
                loading.setLayoutParams(loadingLayoutParams);
                ((FrameLayout) view).addView(loading);

                String imgurl = null;
                Integer imgRId = -1;
                if (datas != null && datas.size() > 0) {
                    imgurl = datas.get(position);
                }
                if (imgRIds != null && imgRIds.size() > 0) {
                    imgRId = imgRIds.get(position);
                }
                loading.setVisibility(View.VISIBLE);
                GlideApp.with(context)
                        .load(TextUtils.isEmpty(imgurl) ? imgRId : imgurl)
                        .thumbnail(0.1f)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                loading.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                loading.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(imageView);
                container.addView(view, 0);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }
}
