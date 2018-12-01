package com.board.applicion.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.board.applicion.R;

public class IPEditText extends LinearLayout {

    private EditText mFirstIP;
    private EditText mSecondIP;
    private EditText mThirdIP;
    private EditText mFourthIP;
    private EditText mFiveIP;

    private String mText;
    private String mText1;
    private String mText2;
    private String mText3;
    private String mText4;
    private String mText5;

    private SharedPreferences mPreferences;

    public IPEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(
                R.layout.custom_my_edittext, this);
        mFirstIP = (EditText) findViewById(R.id.ip_first);
        mSecondIP = (EditText) findViewById(R.id.ip_second);
        mThirdIP = (EditText) findViewById(R.id.ip_third);
        mFourthIP = (EditText) findViewById(R.id.ip_fourth);
        mFiveIP = (EditText) findViewById(R.id.ip_five);
        mPreferences = context.getSharedPreferences("config_IP",
                Context.MODE_PRIVATE);
        OperatingEditText(context);
    }

    private void OperatingEditText(final Context context) {
        mFirstIP.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                /**
                 * Whenever the EditText is filled with 3 digit, the cursor will move to next EditText.
                 */
                if (s != null && s.length() > 0) {
                    if (s.length() > 2 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mText1 = s.toString().substring(0, s.length() - 1);
                            mFirstIP.setText(mText1);
                        } else {
                            mText1 = s.toString().trim();
                        }

                        if (Integer.parseInt(mText1) > 255) {
                            Toast.makeText(context, "Please input valid IP address",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putInt("IP_FIRST", mText1.length());
                        editor.commit();

                        mSecondIP.setFocusable(true);
                        mSecondIP.requestFocus();
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSecondIP.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s != null && s.length() > 0) {
                    if (s.length() > 2 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mText2 = s.toString().substring(0, s.length() - 1);
                            mSecondIP.setText(mText2);
                        } else {
                            mText2 = s.toString().trim();
                        }

                        if (Integer.parseInt(mText2) > 255) {
                            Toast.makeText(context, "Please input valid IP address",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putInt("IP_SECOND", mText2.length());
                        editor.commit();

                        mThirdIP.setFocusable(true);
                        mThirdIP.requestFocus();
                    }
                }

                /**
                 *  When user deletes the input, set focus to previous EditText
                 */
                if (start == 0 && s.length() == 0) {
                    mFirstIP.setFocusable(true);
                    mFirstIP.requestFocus();
                    mFirstIP.setSelection(mPreferences.getInt("IP_FIRST", 0));
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mThirdIP.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                /**
                 * Check if the IP address is valid.
                 * set focus to next EditText.
                 */
                if (s != null && s.length() > 0) {
                    if (s.length() > 2 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mText3 = s.toString().substring(0, s.length() - 1);
                            mThirdIP.setText(mText3);
                        } else {
                            mText3 = s.toString().trim();
                        }

                        if (Integer.parseInt(mText3) > 255) {
                            Toast.makeText(context, "Please input valid IP address",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putInt("IP_THIRD", mText3.length());
                        editor.commit();

                        mFourthIP.setFocusable(true);
                        mFourthIP.requestFocus();
                    }
                }

                /**
                 * When user deletes the input, set focus to previous EditText
                 */
                if (start == 0 && s.length() == 0) {
                    mSecondIP.setFocusable(true);
                    mSecondIP.requestFocus();
                    mSecondIP.setSelection(mPreferences.getInt("IP_SECOND", 0));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mFourthIP.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                /**
                 * Check if the IP address is valid.
                 * set focus to next EditText.
                 */
                if (s != null && s.length() > 0) {
                    mText4 = s.toString().trim();

                    if (Integer.parseInt(mText4) > 255) {
                        Toast.makeText(context, "Please input valid IP address", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }

                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putInt("IP_FOURTH", mText4.length());
                    editor.commit();
                }

                /**
                 * When user deletes the input, set focus to previous EditText
                 */
                if (start == 0 && s.length() == 0) {
                    mSecondIP.setFocusable(true);
                    mSecondIP.requestFocus();
                    mSecondIP.setSelection(mPreferences.getInt("IP_THIRD", 0));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public String getText(Context context) {
        if (TextUtils.isEmpty(mText1) || TextUtils.isEmpty(mText2)
                || TextUtils.isEmpty(mText3) || TextUtils.isEmpty(mText4)) {
            Toast.makeText(context, "Please input valid IP address", Toast.LENGTH_LONG).show();
        }
        return mText1 + "." + mText2 + "." + mText3 + "." + mText4;
    }
}
