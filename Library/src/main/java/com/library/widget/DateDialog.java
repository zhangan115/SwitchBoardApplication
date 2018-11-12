package com.library.widget;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.sito.library.R;

import java.util.Calendar;

public class DateDialog extends Dialog {

    private Context context;
    private int style;

    private NumberPicker np1, np2, np3;
    private static String str1 = "1999";
    private static String str2 = "1";
    private static String str3 = "1";
    private TextView dayTv, monthTv, yearTv;
    private boolean isPickYear, isPickMonth;

    public DateDialog(Context context) {
        super(context);
        this.context = context;
    }

    public DateDialog(Context context, int style) {
        super(context);
        this.context = context;
        this.style = style;
    }

    public DateDialog(Context context, int style, int year, int mouth, int day) {
        super(context);
        this.context = context;
        this.style = style;
        str1 = year + "";
        str2 = mouth + "";
        str3 = day + "";
    }

    public void pickYear() {
        isPickYear = true;
    }

    public void pickMonth() {
        isPickMonth = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.date_dialog);

        np1 = (NumberPicker) findViewById(R.id.np1);
        np2 = (NumberPicker) findViewById(R.id.np2);
        np3 = (NumberPicker) findViewById(R.id.np3);
        yearTv = (TextView) findViewById(R.id.year);
        monthTv = (TextView) findViewById(R.id.month);
        dayTv = (TextView) findViewById(R.id.day);
        if (isPickYear) {
            np2.setVisibility(View.GONE);
            np3.setVisibility(View.GONE);
            dayTv.setVisibility(View.GONE);
            monthTv.setVisibility(View.GONE);
        }
        if (isPickMonth) {
            np3.setVisibility(View.GONE);
            dayTv.setVisibility(View.GONE);
        }
        np1.getChildAt(0).setFocusable(false);
        np1.getChildAt(0).setFocusableInTouchMode(false);
        np2.getChildAt(0).setFocusable(false);
        np2.getChildAt(0).setFocusableInTouchMode(false);
        np3.getChildAt(0).setFocusable(false);
        np3.getChildAt(0).setFocusableInTouchMode(false);

        np1.setMaxValue(2299);
        np1.setMinValue(1970);
        np1.setValue(Integer.parseInt(str1));
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
                str1 = np1.getValue() + "";
                if (Integer.parseInt(str1) % 4 == 0
                        && Integer.parseInt(str1) % 100 != 0
                        || Integer.parseInt(str1) % 400 == 0) {
                    switch (str2) {
                        case "1":
                        case "3":
                        case "5":
                        case "7":
                        case "8":
                        case "10":
                        case "12":
                            np3.setMaxValue(31);
                            np3.setMinValue(1);
                            break;
                        case "4":
                        case "6":
                        case "9":
                        case "11":
                            np3.setMaxValue(30);
                            np3.setMinValue(1);
                            break;
                        default:
                            np3.setMaxValue(29);
                            np3.setMinValue(1);
                            break;
                    }

                } else {
                    if (str2.equals("1") || str2.equals("3") || str2.equals("5") || str2.equals("7") || str2.equals("8") || str2.equals("10") || str2.equals("12")) {
                        np3.setMaxValue(31);
                        np3.setMinValue(1);
                    } else if (str2.equals("4") || str2.equals("6") || str2.equals("9") || str2.equals("11")) {
                        np3.setMaxValue(30);
                        np3.setMinValue(1);
                    } else {
                        np3.setMaxValue(28);
                        np3.setMinValue(1);
                    }
                }

            }
        });

        np2.setMaxValue(12);
        np2.setMinValue(1);
        np2.setValue(Integer.parseInt(str2));
        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
                str2 = np2.getValue() + "";
                switch (str2) {
                    case "1":
                    case "3":
                    case "5":
                    case "7":
                    case "8":
                    case "10":
                    case "12":
                        np3.setMaxValue(31);
                        np3.setMinValue(1);
                        break;
                    case "4":
                    case "6":
                    case "9":
                    case "11":
                        np3.setMaxValue(30);
                        np3.setMinValue(1);
                        break;
                    default:
                        if (Integer.parseInt(str1) % 4 == 0
                                && Integer.parseInt(str1) % 100 != 0
                                || Integer.parseInt(str1) % 400 == 0) {
                            np3.setMaxValue(29);
                            np3.setMinValue(1);
                        } else {
                            np3.setMaxValue(28);
                            np3.setMinValue(1);
                        }
                        break;
                }
            }
        });

        np3.setMaxValue(31);
        np3.setMinValue(1);
        np3.setValue(Integer.parseInt(str3));
        np3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
                str3 = np3.getValue() + "";
            }
        });

        // 设置返回按钮事件和文本
        if (backButtonText != null) {
            Button bckButton = ((Button) findViewById(R.id.dialog_back));
            bckButton.setText(backButtonText);

            if (backButtonClickListener != null) {
                bckButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        backButtonClickListener.onClick(new DateDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
                        dismiss();
                    }
                });
            }
        } else {
            findViewById(R.id.dialog_back).setVisibility(View.GONE);
        }

        // 设置确定按钮事件和文本
        if (confirmButtonText != null) {
            Button cfmButton = ((Button) findViewById(R.id.dialog_confirm));
            cfmButton.setText(confirmButtonText);

            if (confirmButtonClickListener != null) {
                cfmButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        confirmButtonClickListener.onClick(new DateDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
                        dismiss();
                    }
                });
            }
        } else {
            findViewById(R.id.dialog_confirm).setVisibility(View.GONE);
        }
    }

    private String backButtonText; // 对话框返回按钮文本
    private String confirmButtonText; // 对话框确定文本

    // 对话框按钮监听事件
    private OnClickListener backButtonClickListener,
            confirmButtonClickListener;

    public void setBackButton(String backButtonText,
                              OnClickListener listener) {
        this.backButtonText = backButtonText;
        this.backButtonClickListener = listener;
    }

    public void setConfirmButton(String confirmButtonText,
                                 OnClickListener listener) {
        this.confirmButtonText = confirmButtonText;
        this.confirmButtonClickListener = listener;
    }

    public Calendar getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.valueOf(str1), Integer.valueOf(str2) - 1, Integer.valueOf(str3));
        return calendar;
    }

    @Override
    public void show() {
        super.show();
    }
}
