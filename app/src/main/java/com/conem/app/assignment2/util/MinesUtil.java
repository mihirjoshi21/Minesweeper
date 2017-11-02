package com.conem.app.assignment2.util;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.conem.app.assignment2.model.Difficulty;
import com.conem.app.assignment2.model.MinesModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class to generate mines
 * Created by mj on 9/28/2017.
 */

public class MinesUtil {

    public static final int ROW = 9;
    public static final int COLUMN = 9;
    public static final int GRID_SIZE = ROW * COLUMN;
    private static final float TEN = .1f;
    private static final float THIRTY = .3f;
    private static final float FIFTY = .5f;

    public static final int NOT_SHOWN_BLOCK = 0;
    public static final int EMPTY_BLOCK = -2;
    public static final int BOMB_BLOCK = -1;
    public static final int FLAG_BLOCK = -3;
    public static final int WRONG_FLAG_BLOCK = -4;

    /**
     * Generate the Mines field
     *
     * @param minesModel mine model
     * @return generates mines model
     */
    public static MinesModel Mines(MinesModel minesModel) {

        int numberOfMines = numberOfMines(minesModel.minesDifficulty);
        int[][] mMineField = new int[ROW][COLUMN];

        Random random = new Random();
        int row, column;
        for (int i = 0; i < numberOfMines; ) {
            row = random.nextInt(ROW);
            column = random.nextInt(COLUMN);
            if (mMineField[row][column] == NOT_SHOWN_BLOCK) {
                mMineField[row][column] = BOMB_BLOCK;
                i++;
            }
        }

        minesModel.minesCount = numberOfMines;
        minesModel.flagsCount = numberOfMines;
        minesModel.minesArray = mMineField.clone();

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {

                if (mMineField[i][j] != -1) {
                    int mineField = 0;
                    mineField += checkOutOfBounds(i - 1, j - 1, mMineField);
                    mineField += checkOutOfBounds(i - 1, j, mMineField);
                    mineField += checkOutOfBounds(i - 1, j + 1, mMineField);
                    mineField += checkOutOfBounds(i, j - 1, mMineField);
                    mineField += checkOutOfBounds(i, j + 1, mMineField);
                    mineField += checkOutOfBounds(i + 1, j - 1, mMineField);
                    mineField += checkOutOfBounds(i + 1, j, mMineField);
                    mineField += checkOutOfBounds(i + 1, j + 1, mMineField);
                    mMineField[i][j] = mineField;
                }
            }
        }

        minesModel.hiddenArray = mMineField;
        return minesModel;

    }

    /**
     * Check if area us out of bounds
     *
     * @param row        row to check
     * @param column     column to check
     * @param minesField mines field array
     * @return 1 if mines found 0 otherwise
     */

    private static int checkOutOfBounds(int row, int column, int[][] minesField) {
        try {
            if (minesField[row][column] == BOMB_BLOCK) {
                return 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    /**
     * Get number of mines
     *
     * @param difficulty difficulty level
     * @return number of squares with mines
     */
    private static int numberOfMines(Difficulty difficulty) {
        switch (difficulty) {
            case BEGINNER:
                return (int) (TEN * GRID_SIZE);
            case INTERMEDIATE:
                return (int) (THIRTY * GRID_SIZE);
            case ADVANCED:
                return (int) (FIFTY * GRID_SIZE);
            default:
                return (int) (TEN * GRID_SIZE);
        }
    }

    /**
     * Make a shown array
     *
     * @param minesModel    mines model
     * @param row           row to be checked
     * @param column        column to be checked
     * @param isLongPressed is long pressed
     * @return new mines model
     */
    public static MinesModel checkClickedPosition(MinesModel minesModel, int row, int column, boolean isLongPressed) {

        if (isLongPressed) {
            if (minesModel.shownArray[row][column] == FLAG_BLOCK) {
                minesModel.shownArray[row][column] = NOT_SHOWN_BLOCK;
                minesModel.flagsCount++;
            } else if (minesModel.flagsCount > 0) {
                minesModel.shownArray[row][column] = FLAG_BLOCK;
                minesModel.flagsCount--;
            }
        } else if (minesModel.shownArray[row][column] != FLAG_BLOCK) {
            if (minesModel.hiddenArray[row][column] == NOT_SHOWN_BLOCK) {
                List<Integer> traverseList = generateTraverseList(row, column);
                minesModel.hiddenArray[row][column] = EMPTY_BLOCK;
                minesModel.shownArray[row][column] = EMPTY_BLOCK;
                for (int i = 0; i < traverseList.size(); i += 2) {
                    if (minesModel.hiddenArray[traverseList.get(i)][traverseList.get(i + 1)] > 0) {
                        minesModel.shownArray[traverseList.get(i)][traverseList.get(i + 1)] =
                                minesModel.hiddenArray[traverseList.get(i)][traverseList.get(i + 1)];
                    } else if (minesModel.hiddenArray[traverseList.get(i)][traverseList.get(i + 1)] == 0) {
                        minesModel = checkClickedPosition(minesModel, traverseList.get(i),
                                traverseList.get(i + 1), false);
                    }
                }
            } else {
                if (minesModel.hiddenArray[row][column] == BOMB_BLOCK) {
                    minesModel.minesDiscoveredRow = row;
                    minesModel.getMinesDiscoveredColumn = column;
                    minesModel.minesDiscovered = true;
                }
                minesModel.shownArray[row][column] = minesModel.hiddenArray[row][column] ==
                        NOT_SHOWN_BLOCK ? EMPTY_BLOCK : minesModel.hiddenArray[row][column];
            }
        }

        minesModel.itemsShowing = 0;
        for (int[] i : minesModel.shownArray) {
            for (int j : i) {
                if (j != 0) minesModel.itemsShowing++;
            }
        }
        return minesModel;
    }

    /**
     * Generate List to traverse
     *
     * @param row    starting row
     * @param column starting column
     * @return traverse list
     */
    private static List<Integer> generateTraverseList(int row, int column) {
        List<Integer> traverseList = new ArrayList<>();
        if (row - 1 >= 0 && column - 1 >= 0) {
            traverseList.add(row - 1);
            traverseList.add(column - 1);
        }
        if (row - 1 >= 0 && column >= 0) {
            traverseList.add(row - 1);
            traverseList.add(column);
        }
        if (row - 1 >= 0 && column + 1 < COLUMN) {
            traverseList.add(row - 1);
            traverseList.add(column + 1);
        }
        if (row >= 0 && column - 1 >= 0) {
            traverseList.add(row);
            traverseList.add(column - 1);
        }
        if (row >= 0 && column + 1 < COLUMN) {
            traverseList.add(row);
            traverseList.add(column + 1);
        }
        if (row + 1 < ROW && column - 1 >= 0) {
            traverseList.add(row + 1);
            traverseList.add(column - 1);
        }
        if (row + 1 < ROW && column >= 0) {
            traverseList.add(row + 1);
            traverseList.add(column);
        }
        if (row + 1 < ROW && column + 1 < COLUMN) {
            traverseList.add(row + 1);
            traverseList.add(column + 1);
        }

        return traverseList;
    }

    /**
     * Refresh Mines Model
     *
     * @param difficultyType difficulty level
     * @return newly created mines model
     */
    public static MinesModel refreshGrid(String difficultyType) {
        MinesModel minesModel = new MinesModel();
        minesModel.minesDifficulty = difficultyType.equals("1") ? Difficulty.BEGINNER :
                (difficultyType.equals("2") ? Difficulty.INTERMEDIATE : Difficulty.ADVANCED);
        minesModel = MinesUtil.Mines(minesModel);
        minesModel.shownArray = new int[ROW][COLUMN];
        return minesModel;
    }

    /**
     * Get Display Metrics
     *
     * @param activity Activity reference
     * @return metrics object
     */
    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
}
