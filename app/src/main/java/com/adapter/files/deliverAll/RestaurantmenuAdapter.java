package com.adapter.files.deliverAll;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import com.utils.Utilities;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.transition.TransitionManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alaadcin.user.R;
import com.alaadcin.user.deliverAll.RestaurantAllDetailsNewActivity;
import com.general.files.AppFunctions;
import com.general.files.GeneralFunctions;
import com.squareup.picasso.Picasso;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class RestaurantmenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_HEADER = 0;
    private final int TYPE_GRID = 1;
    private final int TYPE_LIST = 2;

    ArrayList<HashMap<String, String>> list;
    Context mContext;
    GeneralFunctions generalFunc;
    private OnItemClickListener mItemClickListener;
    int cnt = 0;
    int grayColor = -1;
    Drawable noIcon = null;

    RestaurantAllDetailsNewActivity restAllDetailsNewAct;

    int imageBackColor;
    boolean isRTLmode = false;
    //ArrayList<Integer> expandedItemPositions = new ArrayList();
    RecyclerView.LayoutManager layoutManager;
    public RestaurantmenuAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc, RecyclerView.LayoutManager layoutManager) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.layoutManager = layoutManager;

        if (mContext instanceof RestaurantAllDetailsNewActivity) {
            restAllDetailsNewAct = (RestaurantAllDetailsNewActivity) mContext;
        }

        grayColor = mContext.getResources().getColor(R.color.gray);
        imageBackColor = mContext.getResources().getColor(R.color.appThemeColor_1);
        noIcon = mContext.getResources().getDrawable(R.mipmap.ic_no_icon);
        isRTLmode = generalFunc.isRTLmode();




    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_headerview, parent, false);
            ViewHolder headerViewGolder = new ViewHolder(view);
            return headerViewGolder;
        } else if (viewType == TYPE_GRID) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resmenu_gridview, parent, false);
            GridViewHolder gridViewGolder = new GridViewHolder(view);
            return gridViewGolder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_list, parent, false);
            ListViewHolder listViewGolder = new ListViewHolder(view);
            return listViewGolder;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        HashMap<String, String> mapData = list.get(position);


        if (holder instanceof ViewHolder) {
            ViewHolder headerholder = (ViewHolder) holder;
            headerholder.menuHeaderTxt.setText(mapData.get("menuName"));
            cnt = 1;
            Logger.d("GGGGGGGG",""+position+","+((GridLayoutManager) layoutManager).findFirstVisibleItemPosition());
       /* if (layoutManager instanceof LinearLayoutManager && position== ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition()){

            if (mContext instanceof RestaurantAllDetailsNewActivity )
                ((RestaurantAllDetailsNewActivity)mContext).scrollMenu(mapData.get("menuName"));

        }else  if (layoutManager instanceof GridLayoutManager && position== ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition()){
            if (mContext instanceof RestaurantAllDetailsNewActivity )
                ((RestaurantAllDetailsNewActivity)mContext).scrollMenu(mapData.get("menuName"));
        }*/

        } else if (holder instanceof GridViewHolder) {
            GridViewHolder grideholder = (GridViewHolder) holder;
            grideholder.title.setText(mapData.get("vItemType"));
            String eFoodType = mapData.get("eFoodType");
            if (eFoodType.equals("NonVeg")) {
                grideholder.nonVegImage.setVisibility(View.VISIBLE);
                grideholder.vegImage.setVisibility(View.GONE);
            } else if (eFoodType.equals("Veg")) {
                grideholder.nonVegImage.setVisibility(View.GONE);
                grideholder.vegImage.setVisibility(View.VISIBLE);
            }
            if (mapData.get("prescription_required").equalsIgnoreCase("Yes")) {
                grideholder.presImage.setVisibility(View.VISIBLE);
            } else {
                grideholder.presImage.setVisibility(View.GONE);
            }
            String StrikeoutPriceConverted=mapData.get("StrikeoutPriceConverted");
            if (mapData.get("fOfferAmtNotZero").equalsIgnoreCase("Yes")) {
                grideholder.price.setText(StrikeoutPriceConverted);
                grideholder.price.setTextColor(grayColor);
            } else {
                grideholder.price.setText(StrikeoutPriceConverted);
                grideholder.price.setPaintFlags(0);
            }
            String vImage = mapData.get("vImageResized");
            boolean isBlank = (vImage != null && !TextUtils.isEmpty(vImage));
            if (!isBlank) {
                grideholder.menuImage.setVisibility(View.VISIBLE);
            }

//            String imageURL = Utilities.getResizeImgURL(mContext, vImage,width , heightOfImage);

            Picasso.get()
                    .load(isBlank ? vImage : "https://www.test.com/ghg").placeholder(R.mipmap.ic_no_icon).error(noIcon)
                    .into(grideholder.menuImage);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) grideholder.menuImage.getLayoutParams();
            layoutParams.height = GeneralFunctions.parseIntegerValue(0, mapData.get("heightOfImage"));
            grideholder.menuImage.setLayoutParams(layoutParams);
            grideholder.addBtn.setText(mapData.get("LBL_ADD"));
            new CreateRoundedView(imageBackColor, 5, 0, 0, grideholder.addBtn);
            grideholder.addBtn.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(grideholder.addBtn, position);
                }
            });
            if (isRTLmode) {
                grideholder.tagImage.setRotation(180);
                grideholder.tagTxt.setPadding(10, 15, 0, 0);
            }
            String vHighlightName = mapData.get("vHighlightName");
            if (vHighlightName != null && !vHighlightName.equals("")) {
                grideholder.tagImage.setVisibility(View.VISIBLE);
                grideholder.tagTxt.setVisibility(View.VISIBLE);
                grideholder.tagTxt.setText(vHighlightName);
            } else {
                grideholder.tagImage.setVisibility(View.GONE);
                grideholder.tagTxt.setVisibility(View.GONE);
            }
            grideholder.vCategoryNameTxt.setText(mapData.get("vCategoryName"));
            if (generalFunc.isRTLmode()) {
                grideholder.addlayout.setBackground(mContext.getResources().getDrawable(R.drawable.login_border_rtl));
            }
        } else {
            ListViewHolder listholder = (ListViewHolder) holder;
            String isFromSearch =mapData.get("isFromSearch");
            boolean isFromSearchAvail=Utils.checkText(isFromSearch);
            if (Utils.checkText(isFromSearch)) {

                if (android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP)
                {
                    listholder.storeTitle.setTransformationMethod(null);
                    listholder.storeTitleEx.setTransformationMethod(null);
                }


                Drawable image = ContextCompat.getDrawable(mContext, R.drawable.ic_star_color1_24dp);
                image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
// Replace blank spaces with image icon
                String myText = mapData.get("vCompany")+"(";
                int textLength = myText.length();
                SpannableString sb = new SpannableString(myText + "  " + mapData.get("vAvgRatingConverted")+ ")");
                ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BASELINE);
                sb.setSpan(imageSpan, textLength, textLength + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                listholder.storeTitle.setText(sb);
                listholder.storeTitleEx.setText(sb);

                listholder.storeTitle.setSelected(true);
                listholder.storeTitleEx.setSelected(true);
//                listholder.restaurantRateTxt.setText(mapData.get("vAvgRatingConverted"));
//                listholder.restaurantRateTxt.setSelected(true);
                listholder.title.setTextColor(imageBackColor);
                listholder.titleEx.setTextColor(imageBackColor);
            }
            listholder.title.setText(mapData.get("vItemType"));
            listholder.title.setSelected(true);
            listholder.titleEx.setText(mapData.get("vItemType"));
            listholder.titleEx.setSelected(true);
            listholder.desc.setText(mapData.get("vItemDesc"));
            listholder.descEx.setText(mapData.get("vItemDesc"));
            //new CreateRoundedView(Color.parseColor("#ffffff"), 30, 2, Color.parseColor("#cfcfcf"), listholder.top_area);

            if (mapData.get("vItemDesc").equalsIgnoreCase("")) {
                listholder.desc.setVisibility(View.GONE);
                listholder.descEx.setVisibility(View.GONE);
            } else {
                listholder.desc.setVisibility(View.VISIBLE);
                listholder.descEx.setVisibility(View.VISIBLE);
            }

            String vImage = mapData.get("vImageResized");
            boolean isBlank = !Utils.checkText(vImage);

            int padding = (int) mContext.getResources().getDimension(R.dimen._5sdp);
            int wdt = new AppFunctions(mContext).getViewHeightWidth(listholder.parent, false);
            int _hw = (int) mContext.getResources().getDimension(isFromSearchAvail?R.dimen._100sdp:R.dimen._60sdp);

            //String imgUrl = Utilities.getResizeImgURL(mContext, mapData.get("vImage"), _hw, _hw, 0);//fixed width-height 60sdp image url
            ImageView imageView= isFromSearchAvail?listholder.searchExpandImg:listholder.expandImg;
            ImageView imageView1= isFromSearchAvail?listholder.searchMenuImg:listholder.menuImg;

            if (isFromSearchAvail)
            {
                listholder.expandImg.setVisibility(View.GONE);
                listholder.searchMenuImg.setVisibility(View.GONE);
                listholder.menuImg.setVisibility(View.GONE);
                listholder.searchMenuImg.setVisibility(View.INVISIBLE);
            }else
            {
                listholder.expandImg.setVisibility(View.GONE);
                listholder.searchMenuImg.setVisibility(View.GONE);
                listholder.menuImg.setVisibility(View.INVISIBLE);
                listholder.searchMenuImg.setVisibility(View.GONE);
            }
            if (mapData.get("vImage").isEmpty()){

                imageView.setVisibility(View.INVISIBLE);

            }else {
                String imgUrl = Utilities.getResizeImgURL(mContext, mapData.get("vImage"), wdt, 0, 0);

                imageView.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(imgUrl)
                        .placeholder(R.mipmap.ic_no_icon)
                        .error(noIcon)
                        .into(imageView);

            }
            /*new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            listholder.expandImg.setImageBitmap(bitmap);
                            Logger.d("onBitmapLoaded_", "Default - Height: " + bitmap.getHeight() + " | Width: " + bitmap.getWidth());
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            listholder.expandImg.setImageResource(R.mipmap.ic_no_icon);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            listholder.expandImg.setImageResource(R.mipmap.ic_no_icon);
                        }
                    });*/


//            for (int i = 0; i < expandedItemPositions.size(); i++) {
//                if (expandedItemPositions.get(i) != position) {//Manage item expanded or collapsed while scrolling.
//                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) listholder.exTmpImgLayout.getLayoutParams();
//                    layoutParams.width = 0;
//                    layoutParams.height = 0;
//                    listholder.exTmpImgLayout.requestLayout();
//                    listholder.exTmpImgLayout.setVisibility(View.VISIBLE);
//
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listholder.expandImg.getLayoutParams();
//                    params.width = (int) mContext.getResources().getDimension(R.dimen._60sdp);
//                    params.height = (int) mContext.getResources().getDimension(R.dimen._60sdp);
//                    listholder.expandImg.requestLayout();
//
//                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listholder.expandDetailArea.getLayoutParams();
//                    lp.width = 0;
//                    lp.height = 0;
//                    listholder.expandDetailArea.setVisibility(View.GONE);
//                    listholder.expandDetailArea.requestLayout();
//
//                    listholder.mainDetailArea.setVisibility(View.VISIBLE);
//                    listholder.addBtnArea.setVisibility(View.VISIBLE);
//                    listholder.menuImg.setVisibility(View.INVISIBLE);
//                } else {
//                    int ParentImageHeight = new AppFunctions(mContext).getViewHeightWidth(listholder.expandImg, true);
//                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) listholder.exTmpImgLayout.getLayoutParams();
//                    layoutParams.width = listholder.parent.getWidth();
//                    layoutParams.height = ParentImageHeight;
//                    listholder.exTmpImgLayout.requestLayout();
//                    listholder.exTmpImgLayout.setVisibility(View.INVISIBLE);
//
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listholder.expandImg.getLayoutParams();
//                    params.width = listholder.parent.getWidth();
//                    params.height = ParentImageHeight;
//                    listholder.expandImg.requestLayout();
//
//                    TransitionManager.beginDelayedTransition(listholder.expandDetailArea);
//                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listholder.expandDetailArea.getLayoutParams();
//                    lp.width = listholder.parent.getWidth() - (int) mContext.getResources().getDimension(R.dimen._30sdp);
//                    lp.height = listholder.parent.getHeight() - (int) mContext.getResources().getDimension(R.dimen._30sdp);
//                    listholder.expandDetailArea.requestLayout();
//
//                    listholder.expandDetailArea.setVisibility(View.VISIBLE);
//                    listholder.mainDetailArea.setVisibility(View.GONE);
//                    listholder.addBtnArea.setVisibility(View.GONE);
//                    listholder.menuImg.setVisibility(View.GONE);
//
//
//                }
//            }
            final long[] mLastClickTime = {0};

            imageView.setOnClickListener(v -> {

                if (SystemClock.elapsedRealtime() - mLastClickTime[0] < 800) {
                    return;
                }
                mLastClickTime[0] = SystemClock.elapsedRealtime();

                try {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        return;
                    }
                    TransitionManager.beginDelayedTransition(listholder.parent);
                    int height = listholder.parent.getHeight();
                    int width = listholder.parent.getWidth();
                    int ht = imageView.getMeasuredHeight();

                    WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    int s1 = display.getHeight();

                    if (ht >= height || ht > s1 / 4 || ht > Utils.dpToPx(120, mContext)) {
                        new Handler().postDelayed(() -> {
                            setMargins(imageView, padding, padding, padding, padding);
                        }, 600);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                        params.width = (int) mContext.getResources().getDimension(R.dimen._60sdp);
                        params.height = (int) mContext.getResources().getDimension(isFromSearchAvail?R.dimen._100sdp:R.dimen._60sdp);
                        imageView.requestLayout();

                        new AppFunctions(mContext).slideAnimView(listholder.exTmpImgLayout, listholder.expandDetailArea, listholder.exTmpImgLayout.getHeight(), 0, 400);

                        listholder.expandDetailArea.setVisibility(View.GONE);
                        imageView1.setVisibility(View.INVISIBLE);
                        listholder.mainDetailArea.setVisibility(View.VISIBLE);
                        listholder.addBtnArea.setVisibility(View.VISIBLE);
                        HashMap<String, String> hashMap = list.get(position);
                        hashMap.put("isExpand", "No");

                        list.set(position, hashMap);
                        // setViewAdapterPosition(position, true);
                    } else { //expanded


                        new Handler().postDelayed(() -> {
                            int pd = (int) mContext.getResources().getDimension(R.dimen._minus10sdp);
                            setMargins(imageView, 0, pd, 0, 0);
                        }, 500);


                        int ParentImageHeight = new AppFunctions(mContext).getViewHeightWidth(imageView, true);
                        //  setViewAdapterPosition(position, false);

                        HashMap<String, String> hashMap = list.get(position);
                        hashMap.put("isExpand", "Yes");
                        hashMap.put("tempheightImg", ParentImageHeight + "");

                        list.set(position, hashMap);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) listholder.exTmpImgLayout.getLayoutParams();
                        layoutParams.width = width;
                        layoutParams.height = ParentImageHeight;//height;
                        listholder.exTmpImgLayout.requestLayout();
                        listholder.exTmpImgLayout.setVisibility(View.INVISIBLE);

                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                        params.width = width;
                        params.height = ParentImageHeight;
                        imageView.requestLayout();

                        listholder.expandDetailArea.setVisibility(View.VISIBLE);
                        listholder.mainDetailArea.setVisibility(View.GONE);
                        listholder.addBtnArea.setVisibility(View.GONE);
                        imageView1.setVisibility(View.GONE);

                        TransitionManager.beginDelayedTransition(listholder.expandDetailArea);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listholder.expandDetailArea.getLayoutParams();

//                        lp.width = width - (int) mContext.getResources().getDimension(R.dimen._30sdp);
                        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
//                        lp.height = height - (int) mContext.getResources().getDimension(R.dimen._30sdp);
                        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        Logger.d("ParentImageHeight1", "::" + height + "::" + lp.height);
                        listholder.expandDetailArea.requestLayout();

                    }
                } catch (Exception e) {
                    Logger.d("ScrollException", "::" + e.toString());
                    e.printStackTrace();
                }
            });


            if (!list.get(position).get("isExpand").equalsIgnoreCase("Yes")) {

                setMargins(imageView, padding, padding, padding, padding);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                params.width = (int) mContext.getResources().getDimension(R.dimen._60sdp);
                params.height = (int) mContext.getResources().getDimension(isFromSearchAvail?R.dimen._100sdp:R.dimen._60sdp);
                imageView.requestLayout();
                new AppFunctions(mContext).slideAnimView(listholder.exTmpImgLayout, listholder.expandDetailArea, listholder.exTmpImgLayout.getHeight(), 0, 400);

                listholder.expandDetailArea.setVisibility(View.GONE);
                imageView1.setVisibility(View.INVISIBLE);
                listholder.mainDetailArea.setVisibility(View.VISIBLE);
                listholder.addBtnArea.setVisibility(View.VISIBLE);

            } else {

                TransitionManager.beginDelayedTransition(listholder.parent);
                int height = listholder.parent.getHeight();
                int width = listholder.parent.getWidth();

                int pd = (int) mContext.getResources().getDimension(R.dimen._minus10sdp);
                setMargins(imageView, 0, pd, 0, 0);

                int ParentImageHeight = Integer.parseInt(list.get(position).get("tempheightImg"));


                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) listholder.exTmpImgLayout.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = ParentImageHeight;//height;
                listholder.exTmpImgLayout.requestLayout();
                listholder.exTmpImgLayout.setVisibility(View.INVISIBLE);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                params.width = width;
                params.height = ParentImageHeight;
                imageView.requestLayout();

                listholder.expandDetailArea.setVisibility(View.VISIBLE);
                listholder.mainDetailArea.setVisibility(View.GONE);
                listholder.addBtnArea.setVisibility(View.GONE);
                imageView1.setVisibility(View.GONE);


                TransitionManager.beginDelayedTransition(listholder.expandDetailArea);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listholder.expandDetailArea.getLayoutParams();
//                lp.width = width - (int) mContext.getResources().getDimension(R.dimen._30sdp);
//                lp.height = height - (int) mContext.getResources().getDimension(R.dimen._30sdp);
                lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
                lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;

                listholder.expandDetailArea.requestLayout();


            }

            String eFoodType = mapData.get("eFoodType");

            if (eFoodType.equals("NonVeg")) {
                listholder.nonVegImage.setVisibility(View.VISIBLE);
                listholder.nonVegImageEx.setVisibility(View.VISIBLE);
                listholder.vegImage.setVisibility(View.GONE);
                listholder.vegImageEx.setVisibility(View.GONE);
            } else if (eFoodType.equals("Veg")) {
                listholder.nonVegImage.setVisibility(View.GONE);
                listholder.nonVegImageEx.setVisibility(View.GONE);
                listholder.vegImage.setVisibility(View.VISIBLE);
                listholder.vegImageEx.setVisibility(View.VISIBLE);
            } else {
               // listholder.vegImage.setVisibility(View.INVISIBLE);
               // listholder.vegImageEx.setVisibility(View.INVISIBLE);
            }

            if (mapData.get("prescription_required").equalsIgnoreCase("Yes")) {
                listholder.presImage.setVisibility(View.VISIBLE);
                listholder.presImageEx.setVisibility(View.VISIBLE);
            } else {
                listholder.presImage.setVisibility(View.GONE);
                listholder.presImageEx.setVisibility(View.GONE);
            }
           String StrikeoutPriceConverted= mapData.get("StrikeoutPriceConverted");
            if (mapData.get("fOfferAmtNotZero").equalsIgnoreCase("Yes")) {
                listholder.price.setText(StrikeoutPriceConverted);
                listholder.price.setPaintFlags(listholder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                listholder.price.setTextColor(grayColor);
                listholder.priceEx.setText(StrikeoutPriceConverted);
                String fDiscountPricewithsymbolConverted= mapData.get("fDiscountPricewithsymbolConverted");
                listholder.priceEx.setPaintFlags(listholder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                listholder.priceEx.setTextColor(grayColor);
                listholder.offerPrice.setText(fDiscountPricewithsymbolConverted);
                listholder.offerPrice.setVisibility(View.VISIBLE);
                listholder.offerPriceEx.setText(fDiscountPricewithsymbolConverted);
                listholder.offerPriceEx.setVisibility(View.VISIBLE);
            } else {
                listholder.price.setTextColor(imageBackColor);
                listholder.price.setText(StrikeoutPriceConverted);
                listholder.price.setPaintFlags(0);
                listholder.priceEx.setTextColor(imageBackColor);
                listholder.priceEx.setText(StrikeoutPriceConverted);
                listholder.priceEx.setPaintFlags(0);
                listholder.offerPrice.setVisibility(View.GONE);
                listholder.offerPriceEx.setVisibility(View.GONE);
            }
            String LBL_ADD=mapData.get("LBL_ADD");
            listholder.addBtn.setText(LBL_ADD);
            listholder.addBtnEx.setText(LBL_ADD);
            new CreateRoundedView(imageBackColor, 5, 0, 0, listholder.addBtn);
            new CreateRoundedView(imageBackColor, 5, 0, 0, listholder.addBtnEx);

            listholder.addBtn.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(listholder.addBtn, position);
                }
            });

            listholder.addBtnEx.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(listholder.addBtnEx, position);
                }
            });

            String vHighlightName = mapData.get("vHighlightName");
            if (vHighlightName != null && !vHighlightName.equals("") && !isFromSearchAvail) {
                listholder.tagArea.setVisibility(View.VISIBLE);
                listholder.tagTxt.setText(vHighlightName);
            } else {
                listholder.tagArea.setVisibility(View.GONE);
            }

            if (isFromSearchAvail){
                listholder.storeTitle.setVisibility(View.VISIBLE);
//                listholder.restaurantRateView.setVisibility(View.VISIBLE);
                listholder.storeTitleEx.setVisibility(View.VISIBLE);
            }else
            {
//                listholder.restaurantRateView.setVisibility(View.GONE);
                listholder.storeTitle.setVisibility(View.GONE);
                listholder.storeTitleEx.setVisibility(View.GONE);
            }

            String isLastLine = mapData.get("isLastLine");
            if (isLastLine != null && isLastLine.equals("Yes")) {
                listholder.bottomLine.setVisibility(View.GONE);
            } else {
                listholder.bottomLine.setVisibility(View.GONE);
            }

            if (generalFunc.isRTLmode()) {
                listholder.addlayout.setBackground(mContext.getResources().getDrawable(R.drawable.login_border_rtl));
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MTextView menuHeaderTxt;

        public ViewHolder(View view) {
            super(view);
            menuHeaderTxt = (MTextView) view.findViewById(R.id.menuHeaderTxt);
        }
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView vegImage, nonVegImage, presImage;
        MTextView title;
        MTextView price;
        MTextView addBtn;
        ImageView tagImage;
        MTextView tagTxt;
        SelectableRoundedImageView menuImage;
        MTextView vCategoryNameTxt;
        LinearLayout addlayout;
        public GridViewHolder(View view) {
            super(view);
            menuImage = (SelectableRoundedImageView) view.findViewById(R.id.menuImage);
            vegImage = (ImageView) view.findViewById(R.id.vegImage);
            nonVegImage = (ImageView) view.findViewById(R.id.nonVegImage);
            presImage = (ImageView) view.findViewById(R.id.presImage);
            title = (MTextView) view.findViewById(R.id.title);
            price = (MTextView) view.findViewById(R.id.price);
            addBtn = (MTextView) view.findViewById(R.id.addBtn);
            tagImage = (ImageView) view.findViewById(R.id.tagImage);
            tagTxt = (MTextView) view.findViewById(R.id.tagTxt);
            vCategoryNameTxt = (MTextView) view.findViewById(R.id.vCategoryNameTxt);
            addlayout = view.findViewById(R.id.addlayout);
        }
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        ImageView vegImage, nonVegImage, presImage, menuImg, expandImg,searchMenuImg, searchExpandImg;
        ImageView presImageEx, vegImageEx, nonVegImageEx;
        MTextView title,storeTitle,restaurantRateTxt, price, priceEx, offerPrice, offerPriceEx, desc, descEx, addBtn, addBtnEx, tagTxt, titleEx,storeTitleEx;
        View bottomLine;
        LinearLayout tagArea, restaurantRateView,mainDetailArea, addBtnArea, expandDetailArea, exTmpImgLayout;
        RelativeLayout top_area;
        RelativeLayout parent;
        LinearLayout addlayout;
        ListViewHolder(View view) {
            super(view);

            parent = view.findViewById(R.id.parent);
            vegImage = (ImageView) view.findViewById(R.id.vegImage);
            vegImageEx = (ImageView) view.findViewById(R.id.vegImageEx);
            menuImg = (ImageView) view.findViewById(R.id.menuImg);
            searchMenuImg = (ImageView) view.findViewById(R.id.searchMenuImg);
            expandImg = (ImageView) view.findViewById(R.id.expandImg);
            searchExpandImg = (ImageView) view.findViewById(R.id.searchExpandImg);
            exTmpImgLayout = view.findViewById(R.id.expandTempImg);
            nonVegImage = (ImageView) view.findViewById(R.id.nonVegImage);
            nonVegImageEx = (ImageView) view.findViewById(R.id.nonVegImageEx);
            presImage = (ImageView) view.findViewById(R.id.presImage);
            presImageEx = (ImageView) view.findViewById(R.id.presImageEx);
            title = (MTextView) view.findViewById(R.id.title);
            titleEx = (MTextView) view.findViewById(R.id.titleEx);
            storeTitle = (MTextView) view.findViewById(R.id.storeTitle);
            storeTitleEx = (MTextView) view.findViewById(R.id.storeTitleEx);
            restaurantRateTxt = view.findViewById(R.id.restaurantRateTxt);
            restaurantRateView = view.findViewById(R.id.restaurantRateView);
            mainDetailArea = view.findViewById(R.id.mainDetailArea);
            addBtnArea = view.findViewById(R.id.addBtnArea);
            expandDetailArea = view.findViewById(R.id.expandDetailArea);
            price = (MTextView) view.findViewById(R.id.price);
            priceEx = (MTextView) view.findViewById(R.id.priceEx);
            offerPrice = (MTextView) view.findViewById(R.id.offerPrice);
            offerPriceEx = (MTextView) view.findViewById(R.id.offerPriceEx);
            desc = (MTextView) view.findViewById(R.id.desc);
            descEx = (MTextView) view.findViewById(R.id.descEx);
            addBtn = (MTextView) view.findViewById(R.id.addBtn);
            addBtnEx = (MTextView) view.findViewById(R.id.addBtnEx);
            tagArea = (LinearLayout) view.findViewById(R.id.tagArea);
            tagTxt = (MTextView) view.findViewById(R.id.tagTxt);
            bottomLine = (View) view.findViewById(R.id.bottomLine);
            top_area = view.findViewById(R.id.top_area);
            addlayout = view.findViewById(R.id.addlayout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        String rowType = list.get(position).get("Type");
        if (rowType.equals("HEADER")) {
            return TYPE_HEADER;
        } else if (rowType.equals("GRID")) {
            return TYPE_GRID;
        } else {
            return TYPE_LIST;
        }

    }

    public interface OnItemClickListener {
        void onItemClickList(View v, int position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

//    private void setViewAdapterPosition(int pos, boolean isRemove) {
//        if (isRemove) {
//            expandedItemPositions.remove(pos);
//        } else {
//            expandedItemPositions.add(pos);
//        }
//    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

}
