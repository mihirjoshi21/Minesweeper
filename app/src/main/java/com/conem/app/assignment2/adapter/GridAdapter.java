package com.conem.app.assignment2.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.conem.app.assignment2.R;
import com.conem.app.assignment2.model.MinesModel;
import com.conem.app.assignment2.util.MinesUtil;
import com.conem.app.assignment2.util.ProjectUtil;

import static com.conem.app.assignment2.util.MinesUtil.BOMB_BLOCK;
import static com.conem.app.assignment2.util.MinesUtil.COLUMN;
import static com.conem.app.assignment2.util.MinesUtil.EMPTY_BLOCK;
import static com.conem.app.assignment2.util.MinesUtil.FLAG_BLOCK;
import static com.conem.app.assignment2.util.MinesUtil.GRID_SIZE;
import static com.conem.app.assignment2.util.MinesUtil.NOT_SHOWN_BLOCK;
import static com.conem.app.assignment2.util.MinesUtil.ROW;
import static com.conem.app.assignment2.util.MinesUtil.WRONG_FLAG_BLOCK;

/**
 * Grid Adapter
 * Created by mj on 9/29/2017.
 */

public class GridAdapter extends BaseAdapter {

    private final Context mContext;
    private int[] mMines;
    private MinesModel mMinesModel;
    private int[] colors = {android.R.color.holo_blue_light, R.color.blue,
            R.color.green, R.color.red, R.color.purple,
            R.color.magenta, R.color.pink, R.color.yellow, R.color.dark_gray};

    private static double ITEM_HEIGHT = .06;

    public GridAdapter(Context context, MinesModel minesModel) {
        mContext = context;
        mMinesModel = minesModel;
        mMines = new int[GRID_SIZE];

        if (minesModel.shownArray != null) {
            create1D(minesModel);
        }
    }

    @Override
    public int getCount() {
        return mMines.length;
    }

    @Override
    public Object getItem(int i) {
        return mMines[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_grid, null);
            holder.text = convertView.findViewById(R.id.text1);
            holder.image = convertView.findViewById(R.id.image1);
            convertView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    (int) (MinesUtil.getDisplayMetrics((Activity) mContext).heightPixels * ITEM_HEIGHT)));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setTypeface(ProjectUtil.getTypeface(mContext, ProjectUtil.EIGHT_BIT_FONT));

        holder.text.setHapticFeedbackEnabled(true);
        holder.image.setHapticFeedbackEnabled(true);

        if (mMines[position] == BOMB_BLOCK || mMines[position] == FLAG_BLOCK ||
                mMines[position] == WRONG_FLAG_BLOCK) {
            setViewVisible(true, holder.image, holder.text);
            holder.image.setBackgroundDrawable((mMines[position] == FLAG_BLOCK || mMines[position] == WRONG_FLAG_BLOCK) ?
                    ContextCompat.getDrawable(mContext, R.drawable.block)
                    : new ColorDrawable(ContextCompat.getColor(mContext, R.color.gray)));
            holder.image.setImageDrawable(ContextCompat.getDrawable(mContext,
                    mMines[position] == BOMB_BLOCK ? R.drawable.mine : (
                            mMines[position] == FLAG_BLOCK ? R.drawable.flag : R.drawable.flag_cross)));
            if (mMinesModel.minesDiscovered &&
                    mMinesModel.minesDiscoveredRow * MinesUtil.ROW + mMinesModel.getMinesDiscoveredColumn == position) {
                holder.image.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.red)));
            }
        } else {
            setViewVisible(false, holder.image, holder.text);
            if (mMines[position] == EMPTY_BLOCK) {
                holder.text.setText("");
            } else {
                holder.text.setText(mMines[position] == BOMB_BLOCK ? "*" : (mMines[position] == NOT_SHOWN_BLOCK ?
                        " " : String.valueOf(mMines[position])));
            }

            //Text Color
            holder.text.setTextColor(ContextCompat.getColor(mContext, mMines[position] > 0 ?
                    colors[mMines[position]] : android.R.color.white));

            //BackGround color if star shown

            if (mMines[position] == 0) {
                holder.text.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.block));
            } else {
                holder.text.setBackgroundDrawable(ContextCompat.getDrawable(mContext,
                        R.color.gray));
            }
        }
        return convertView;
    }

    public void notifyDataSetChanged(MinesModel minesModel) {
        mMinesModel = minesModel;
        create1D(minesModel);
        notifyDataSetChanged();
    }

    /**
     * Create 1D from 2D array
     *
     * @param minesModel mines model
     */
    private void create1D(MinesModel minesModel) {
        for (int i = 0, k = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                if (minesModel.minesDiscovered) {
                    if (minesModel.shownArray[i][j] == FLAG_BLOCK) {
                        if (minesModel.hiddenArray[i][j] != BOMB_BLOCK) {
                            minesModel.shownArray[i][j] = WRONG_FLAG_BLOCK;
                        }
                    } else {
                        minesModel.shownArray[i][j] = minesModel.hiddenArray[i][j] == NOT_SHOWN_BLOCK ? EMPTY_BLOCK :
                                minesModel.hiddenArray[i][j];
                    }
                } else if (minesModel.isWon) {
                    if (minesModel.hiddenArray[i][j] == BOMB_BLOCK) {
                        minesModel.shownArray[i][j] = FLAG_BLOCK;
                    }
                }
                mMines[k] = minesModel.shownArray[i][j];
                k++;
            }
        }
    }


    private static class ViewHolder {
        TextView text;
        ImageView image;
    }

    /**
     * Make view visible
     *
     * @param isImageVisible is image visible
     * @param imageView      image view reference
     * @param textView       text view reference
     */
    private void setViewVisible(boolean isImageVisible, ImageView imageView, TextView textView) {
        imageView.setVisibility(isImageVisible ? View.VISIBLE : View.GONE);
        textView.setVisibility(!isImageVisible ? View.VISIBLE : View.GONE);
    }
}
