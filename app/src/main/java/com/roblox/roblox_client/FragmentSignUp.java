package com.roblox.roblox_client;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class FragmentSignUp extends Fragment {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$Validation = null;
    private static /* synthetic */ int[] $SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$ValidationOp = null;
    private static final String TAG = "fragmentsignup";
    private static Button mBirthday = null;
    static int mYear = 2000;
    static int mMonth = 0;
    static int mDay = 1;
    static boolean mDateSelected = false;
    private SignUpCallbacks mSignUpCallbacks = null;
    private View mBackgroundView = null;
    private TextView mCancelTextView = null;
    private EditText mUsernameEditText = null;
    private ImageView mUsernameCheckImageView = null;
    private EditText mPasswordEditText = null;
    private ImageView mPasswordCheckImageView = null;
    private EditText mPasswordVerifyEditText = null;
    private ImageView mPasswordVerifyCheckImageView = null;
    private Spinner mGenderSpinner = null;
    private EditText mEmailEditText = null;
    private ImageView mEmailCheckImageView = null;
    private Button mSignUpButton = null;
    private TextView mAgreementTextView = null;
    private String mUsername = null;
    private String mPassword = null;
    private String mPasswordVerify = null;
    private String mEmail = null;
    private int mGender = 0;
    private boolean mIsCanceling = false;
    private Validation mUsernameValidation = Validation.BLANK;
    private Validation mPasswordValidation = Validation.BLANK;
    private Validation mPasswordVerifyValidation = Validation.BLANK;
    private Validation mEmailVerifyValidation = Validation.BLANK;

    interface SignUpCallbacks {
        void onFragmentSignUpCancel();

        void onFragmentSignUpOk(JSONObject jSONObject);
    }

    private enum Validation {
        BLANK,
        VALID,
        INVALID;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Validation[] valuesCustom() {
            Validation[] validationArrValuesCustom = values();
            int length = validationArrValuesCustom.length;
            Validation[] validationArr = new Validation[length];
            System.arraycopy(validationArrValuesCustom, 0, validationArr, 0, length);
            return validationArr;
        }
    }

    private enum ValidationOp {
        USERNAME,
        PASSWORD;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static ValidationOp[] valuesCustom() {
            ValidationOp[] validationOpArrValuesCustom = values();
            int length = validationOpArrValuesCustom.length;
            ValidationOp[] validationOpArr = new ValidationOp[length];
            System.arraycopy(validationOpArrValuesCustom, 0, validationOpArr, 0, length);
            return validationOpArr;
        }
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$Validation() {
        int[] iArr = $SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$Validation;
        if (iArr == null) {
            iArr = new int[Validation.valuesCustom().length];
            try {
                iArr[Validation.BLANK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Validation.INVALID.ordinal()] = 3;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Validation.VALID.ordinal()] = 2;
            } catch (NoSuchFieldError e3) {
            }
            $SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$Validation = iArr;
        }
        return iArr;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$ValidationOp() {
        int[] iArr = $SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$ValidationOp;
        if (iArr == null) {
            iArr = new int[ValidationOp.valuesCustom().length];
            try {
                iArr[ValidationOp.PASSWORD.ordinal()] = 2;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[ValidationOp.USERNAME.ordinal()] = 1;
            } catch (NoSuchFieldError e2) {
            }
            $SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$ValidationOp = iArr;
        }
        return iArr;
    }

    public void setSignUpCallbacks(SignUpCallbacks callbacks) {
        this.mSignUpCallbacks = callbacks;
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        this.mBackgroundView = view.findViewById(R.id.fragment_sign_up_background);
        this.mCancelTextView = (TextView) view.findViewById(R.id.fragment_sign_up_cancel);
        this.mUsernameEditText = (EditText) view.findViewById(R.id.fragment_sign_up_username);
        this.mUsernameCheckImageView = (ImageView) view.findViewById(R.id.fragment_sign_up_username_checked);
        this.mPasswordEditText = (EditText) view.findViewById(R.id.fragment_sign_up_password);
        this.mPasswordCheckImageView = (ImageView) view.findViewById(R.id.fragment_sign_up_password_checked);
        this.mPasswordVerifyEditText = (EditText) view.findViewById(R.id.fragment_sign_up_password_verify);
        this.mPasswordVerifyCheckImageView = (ImageView) view.findViewById(R.id.fragment_sign_up_password_verify_checked);
        mBirthday = (Button) view.findViewById(R.id.fragment_sign_up_birthday);
        this.mGenderSpinner = (Spinner) view.findViewById(R.id.fragment_sign_up_gender_spinner);
        this.mEmailEditText = (EditText) view.findViewById(R.id.fragment_sign_up_email);
        this.mEmailCheckImageView = (ImageView) view.findViewById(R.id.fragment_sign_up_email_checked);
        this.mSignUpButton = (Button) view.findViewById(R.id.fragment_sign_up_button);
        this.mAgreementTextView = (TextView) view.findViewById(R.id.fragment_sign_up_agreement);
        setNextButton(this.mUsernameEditText, this.mPasswordEditText);
        setNextButton(this.mPasswordEditText, this.mPasswordVerifyEditText);
        this.mBackgroundView.setOnClickListener(new View.OnClickListener() { // from class: com.roblox.roblox_client.FragmentSignUp.1
            @Override // android.view.View.OnClickListener
            public void onClick(View x) {
            }
        });
        this.mCancelTextView.setOnClickListener(new View.OnClickListener() { // from class: com.roblox.roblox_client.FragmentSignUp.2
            @Override // android.view.View.OnClickListener
            public void onClick(View x) {
                if (FragmentSignUp.this.mSignUpCallbacks != null) {
                    FragmentSignUp.this.mIsCanceling = true;
                    InputMethodManager inputManager = (InputMethodManager) FragmentSignUp.this.getActivity().getSystemService("input_method");
                    inputManager.hideSoftInputFromWindow(FragmentSignUp.this.getActivity().getCurrentFocus().getWindowToken(), 2);
                    FragmentSignUp.this.mSignUpCallbacks.onFragmentSignUpCancel();
                }
            }
        });
        this.mUsernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.roblox.roblox_client.FragmentSignUp.3
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                if (!FragmentSignUp.this.mIsCanceling && !hasFocus) {
                    FragmentSignUp.this.doValidationTask(ValidationOp.USERNAME);
                }
            }
        });
        this.mPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.roblox.roblox_client.FragmentSignUp.4
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                if (!FragmentSignUp.this.mIsCanceling && !hasFocus) {
                    FragmentSignUp.this.doValidationTask(ValidationOp.PASSWORD);
                }
            }
        });
        this.mPasswordVerifyEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.roblox.roblox_client.FragmentSignUp.5
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                if (!FragmentSignUp.this.mIsCanceling && !hasFocus) {
                    FragmentSignUp.this.doPasswordVerifyValidation(true);
                }
            }
        });
        this.mPasswordVerifyEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.roblox.roblox_client.FragmentSignUp.6
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 6) {
                    return false;
                }
                FragmentSignUp.this.mPasswordVerifyEditText.clearFocus();
                FragmentSignUp.this.doPasswordVerifyValidation(true);
                InputMethodManager inputManager = (InputMethodManager) FragmentSignUp.this.getActivity().getSystemService("input_method");
                inputManager.hideSoftInputFromWindow(FragmentSignUp.this.getActivity().getCurrentFocus().getWindowToken(), 2);
                return true;
            }
        });
        mBirthday.setOnClickListener(new View.OnClickListener() { // from class: com.roblox.roblox_client.FragmentSignUp.7
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(FragmentSignUp.this.getFragmentManager(), (String) null);
                if (FragmentSignUp.this.getActivity().getCurrentFocus() != null) {
                    FragmentSignUp.this.getActivity().getCurrentFocus().clearFocus();
                }
            }
        });
        this.mEmailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.roblox.roblox_client.FragmentSignUp.8
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                if (!FragmentSignUp.this.mIsCanceling && !hasFocus) {
                    FragmentSignUp.this.doEmailValidation();
                }
            }
        });
        this.mEmailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.roblox.roblox_client.FragmentSignUp.9
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 6) {
                    return false;
                }
                FragmentSignUp.this.mEmailEditText.clearFocus();
                FragmentSignUp.this.doEmailValidation();
                InputMethodManager inputManager = (InputMethodManager) FragmentSignUp.this.getActivity().getSystemService("input_method");
                inputManager.hideSoftInputFromWindow(FragmentSignUp.this.getActivity().getCurrentFocus().getWindowToken(), 2);
                return true;
            }
        });
        this.mSignUpButton.setOnClickListener(new View.OnClickListener() { // from class: com.roblox.roblox_client.FragmentSignUp.10
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                FragmentSignUp.this.onSignUpClicked();
            }
        });
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.GenderArray, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mGenderSpinner.setAdapter((SpinnerAdapter) genderAdapter);
        this.mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.roblox.roblox_client.FragmentSignUp.11
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view2, int position, long id) {
                FragmentSignUp.this.getActivity().getCurrentFocus().clearFocus();
                FragmentSignUp.this.mGender = position;
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
                FragmentSignUp.this.getActivity().getCurrentFocus().clearFocus();
                FragmentSignUp.this.mGender = 0;
            }
        });
        this.mAgreementTextView.setText(Html.fromHtml(getString(R.string.SignupFinePrintAndroid)));
        this.mAgreementTextView.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        mYear = 2000;
        mMonth = 0;
        mDay = 1;
        mDateSelected = false;
    }

    public static void onDateSet() {
        Calendar cal = new GregorianCalendar(mYear, mMonth, mDay);
        String date = DateUtils.formatDateTime(null, cal.getTimeInMillis(), 524304);
        mBirthday.setText(date);
        mDateSelected = true;
    }

    private void setImageViewValidation(ImageView v, Validation s) {
        switch ($SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$Validation()[s.ordinal()]) {
            case 1:
                v.setImageDrawable(null);
                break;
            case 2:
                v.setImageResource(R.drawable.sign_up_checkmark);
                break;
            case 3:
                v.setImageResource(R.drawable.sign_up_xmark);
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setUsernameValidation(Validation s) {
        setImageViewValidation(this.mUsernameCheckImageView, s);
        this.mUsernameValidation = s;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPasswordValidation(Validation s) {
        setImageViewValidation(this.mPasswordCheckImageView, s);
        this.mPasswordValidation = s;
    }

    private void setPasswordVerifyValidation(Validation s) {
        setImageViewValidation(this.mPasswordVerifyCheckImageView, s);
        this.mPasswordVerifyValidation = s;
    }

    private void setEmailValidation(Validation s) {
        setImageViewValidation(this.mEmailCheckImageView, s);
        this.mEmailVerifyValidation = s;
    }

    public void onSignUpClicked() {
        getStringsFromUi();
        if (this.mUsername.equals("")) {
            Utils.alertExclusively(getActivity(), R.string.NoSignupUsernameError);
            return;
        }
        if (this.mUsername.equals("")) {
            Utils.alertExclusively(getActivity(), R.string.NoSignupPasswordError);
            return;
        }
        if (this.mGender != 1 && this.mGender != 2) {
            Utils.alertExclusively(getActivity(), R.string.NoSignupGenderError);
            return;
        }
        if (!mDateSelected) {
            Utils.alertExclusively(getActivity(), R.string.NoSignupBirthdayError);
            return;
        }
        doPasswordVerifyValidation(true);
        doEmailValidation();
        if (this.mUsernameValidation != Validation.VALID || this.mPasswordValidation != Validation.VALID || this.mPasswordVerifyValidation != Validation.VALID || this.mEmailVerifyValidation == Validation.INVALID) {
            Utils.alertExclusively(getActivity(), R.string.EntryIncorrectBody);
        } else {
            new SignUpAsyncTask().execute(new Void[0]);
        }
    }

    public void setNextButton(TextView view, final View nextView) {
        view.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.roblox.roblox_client.FragmentSignUp.12
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 5) {
                    return false;
                }
                nextView.requestFocus();
                return true;
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), this, FragmentSignUp.mYear, FragmentSignUp.mMonth, FragmentSignUp.mDay);
        }

        @Override // android.app.DatePickerDialog.OnDateSetListener
        public void onDateSet(DatePicker view, int year, int month, int day) {
            FragmentSignUp.mYear = year;
            FragmentSignUp.mMonth = month;
            FragmentSignUp.mDay = day;
            FragmentSignUp.onDateSet();
        }
    }

    public void getStringsFromUi() {
        this.mUsername = this.mUsernameEditText.getText().toString();
        this.mPassword = this.mPasswordEditText.getText().toString();
        this.mPasswordVerify = this.mPasswordVerifyEditText.getText().toString();
        this.mEmail = this.mEmailEditText.getText().toString();
    }

    public void doValidationTask(ValidationOp op) {
        getStringsFromUi();
        switch ($SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$ValidationOp()[op.ordinal()]) {
            case 1:
                if (this.mUsername.isEmpty()) {
                    setUsernameValidation(Validation.BLANK);
                    break;
                } else if (this.mUsername.length() < 3) {
                    Utils.alertExclusively(getActivity(), R.string.UsernameTooShort);
                    setUsernameValidation(Validation.INVALID);
                    break;
                } else if (this.mUsername.length() > 20) {
                    Utils.alertExclusively(getActivity(), R.string.UsernameTooLong);
                    setUsernameValidation(Validation.INVALID);
                    break;
                } else {
                    new ValidationAsyncTask(op).execute(new Void[0]);
                    break;
                }
            case 2:
                if (!this.mPassword.isEmpty()) {
                    new ValidationAsyncTask(op).execute(new Void[0]);
                    break;
                } else {
                    setPasswordValidation(Validation.BLANK);
                    break;
                }
        }
    }

    public void doPasswordVerifyValidation(boolean warn) {
        getStringsFromUi();
        if (this.mPassword.isEmpty() || this.mPasswordVerify.isEmpty()) {
            setPasswordVerifyValidation(Validation.BLANK);
            return;
        }
        if (this.mPassword.equals(this.mPasswordVerify)) {
            setPasswordVerifyValidation(Validation.VALID);
            return;
        }
        setPasswordVerifyValidation(Validation.INVALID);
        if (warn) {
            Utils.alertExclusively(getActivity(), R.string.VerifyPasswordWrong);
        }
    }

    public void doEmailValidation() {
        if (this.mEmail.isEmpty()) {
            setEmailValidation(Validation.BLANK);
            return;
        }
        Pattern p = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher m = p.matcher(this.mEmail);
        boolean matches = m.matches();
        setEmailValidation(matches ? Validation.VALID : Validation.INVALID);
        if (!matches) {
            Utils.alertExclusively(getActivity(), R.string.EmailInvalid);
        }
    }

    class ValidationAsyncTask extends AsyncTask<Void, Void, Void> {
        private /* synthetic */ int[] $SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$ValidationOp;
        ValidationOp mOp;
        String mPasswordLocal;
        String mUsernameLocal;
        String mResponse = null;
        JSONObject mJson = null;

        /* synthetic */ int[] $SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$ValidationOp() {
            int[] iArr = $SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$ValidationOp;
            if (iArr == null) {
                iArr = new int[ValidationOp.valuesCustom().length];
                try {
                    iArr[ValidationOp.PASSWORD.ordinal()] = 2;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[ValidationOp.USERNAME.ordinal()] = 1;
                } catch (NoSuchFieldError e2) {
                }
                $SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$ValidationOp = iArr;
            }
            return iArr;
        }

        ValidationAsyncTask(ValidationOp op) {
            this.mUsernameLocal = null;
            this.mPasswordLocal = null;
            this.mOp = op;
            this.mUsernameLocal = FragmentSignUp.this.mUsername;
            this.mPasswordLocal = FragmentSignUp.this.mPassword;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... arg0)  {
            String encodedUsername = "";
            String encodedPassword = "";
            try {
                encodedUsername = URLEncoder.encode(this.mUsernameLocal, "UTF-8");
                encodedPassword = URLEncoder.encode(this.mPasswordLocal, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Utils.alertUnexpectedError((RobloxActivity) FragmentSignUp.this.getActivity(), "Username or Password not UTF-8 #1");
            }
            switch ($SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$ValidationOp()[this.mOp.ordinal()]) {
                case 1:
                    this.mResponse = HttpAgent.readUrlToString(RobloxSettings.usernameCheckUrl(encodedUsername), null);
                    break;
                case 2:
                    this.mResponse = HttpAgent.readUrlToString(RobloxSettings.passwordCheckUrl(), RobloxSettings.passwordCheckUrlArgs(encodedUsername, encodedPassword));
                    break;
            }
            if (this.mResponse != null) {
                try {
                    this.mJson = new JSONObject(this.mResponse);
                } catch (JSONException e2) {
                    Utils.alertUnexpectedError((RobloxActivity) FragmentSignUp.this.getActivity(), "ValidationAsyncTask cannot parse JSON #1");
                }
            }
            Log.i(FragmentSignUp.TAG, "JSON: " + this.mResponse);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (FragmentSignUp.this.getActivity() != null && this.mJson != null) {
                try {
                    switch ($SWITCH_TABLE$com$roblox$roblox_client$FragmentSignUp$ValidationOp()[this.mOp.ordinal()]) {
                        case 1:
                            if (this.mJson.getInt("data") == 0) {
                                FragmentSignUp.this.setUsernameValidation(Validation.VALID);
                                break;
                            } else {
                                FragmentSignUp.this.new UsernameSuggestionAsyncTask().execute(new Void[0]);
                                FragmentSignUp.this.setUsernameValidation(Validation.INVALID);
                                break;
                            }
                        case 2:
                            if (this.mJson.getBoolean("success")) {
                                FragmentSignUp.this.setPasswordValidation(Validation.VALID);
                                FragmentSignUp.this.doPasswordVerifyValidation(false);
                                break;
                            } else {
                                Utils.alertExclusively(FragmentSignUp.this.getActivity(), R.string.PasswordWrongAndroid);
                                FragmentSignUp.this.setPasswordValidation(Validation.INVALID);
                                break;
                            }
                    }
                } catch (JSONException e) {
                    Utils.alertUnexpectedError((RobloxActivity) FragmentSignUp.this.getActivity(), "ValidationAsyncTask cannot parse JSON #2");
                }
            }
        }
    }

    class UsernameSuggestionAsyncTask extends AsyncTask<Void, Void, Void> {
        String mResponse = null;
        String mUsernameLocal;

        UsernameSuggestionAsyncTask() {
            this.mUsernameLocal = null;
            this.mUsernameLocal = FragmentSignUp.this.mUsername;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... arg0)  {
            String encodedUsername = "";
            try {
                encodedUsername = URLEncoder.encode(this.mUsernameLocal, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Utils.alertUnexpectedError((RobloxActivity) FragmentSignUp.this.getActivity(), "Username not UTF-8");
            }
            this.mResponse = HttpAgent.readUrlToString(RobloxSettings.recommendUsernameUrl(encodedUsername), null);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) throws Resources.NotFoundException {
            super.onPostExecute(result);
            if (FragmentSignUp.this.getActivity() != null) {
                if (this.mUsernameLocal.equals(this.mResponse)) {
                    Utils.alertExclusively(FragmentSignUp.this.getActivity(), R.string.UsernameExplicit);
                    return;
                }
                String format = FragmentSignUp.this.getResources().getString(R.string.UsernameTaken);
                String s = Utils.format(format, this.mResponse);
                Utils.alertExclusively(FragmentSignUp.this.getActivity(), s);
            }
        }
    }

    class SignUpAsyncTask extends AsyncTask<Void, Void, Void> {
        AlertDialog mAlertDialog;
        int mDayLocal;
        String mEmailLocal;
        int mGenderLocal;
        int mMonthLocal;
        String mPasswordLocal;
        String mStatus;
        String mUsernameLocal;
        int mYearLocal;
        String mResponse = null;
        JSONObject mJson = null;

        SignUpAsyncTask() {
            this.mUsernameLocal = null;
            this.mPasswordLocal = null;
            this.mGenderLocal = 0;
            this.mYearLocal = 2000;
            this.mMonthLocal = 0;
            this.mDayLocal = 1;
            this.mEmailLocal = null;
            this.mAlertDialog = null;
            this.mUsernameLocal = FragmentSignUp.this.mUsername;
            this.mPasswordLocal = FragmentSignUp.this.mPassword;
            this.mGenderLocal = FragmentSignUp.this.mGender;
            this.mYearLocal = FragmentSignUp.mYear;
            this.mMonthLocal = FragmentSignUp.mMonth;
            this.mDayLocal = FragmentSignUp.mDay;
            this.mEmailLocal = FragmentSignUp.this.mEmail;
            this.mAlertDialog = Utils.alert((RobloxActivity) FragmentSignUp.this.getActivity(), R.string.SigningUp);
            this.mAlertDialog.setCanceledOnTouchOutside(false);
            this.mAlertDialog.setCancelable(false);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... arg0)  {
            String encodedUsername = "";
            String encodedPassword = "";
            try {
                encodedUsername = URLEncoder.encode(this.mUsernameLocal, "UTF-8");
                encodedPassword = URLEncoder.encode(this.mPasswordLocal, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Utils.alertUnexpectedError((RobloxActivity) FragmentSignUp.this.getActivity(), "Username or Password not UTF-8 #1");
            }
            String gender = this.mGenderLocal == 1 ? "Male" : "Female";
            String dateOfBirth = Utils.format("%d/%d/%d", Integer.valueOf(this.mMonthLocal + 1), Integer.valueOf(this.mDayLocal), Integer.valueOf(this.mYearLocal));
            this.mResponse = HttpAgent.readUrlToString(RobloxSettings.signUpUrl(), RobloxSettings.signUpUrlArgs(encodedUsername, encodedPassword, gender, dateOfBirth));
            if (this.mResponse != null) {
                try {
                    this.mJson = new JSONObject(this.mResponse);
                    this.mStatus = this.mJson.getString("Status");
                } catch (JSONException e2) {
                }
            }
            Log.i(FragmentSignUp.TAG, "JSON: " + this.mResponse);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (FragmentSignUp.this.getActivity() != null) {
                this.mAlertDialog.cancel();
                this.mAlertDialog = null;
                if (this.mJson == null || this.mStatus == null) {
                    Utils.alertUnexpectedError((RobloxActivity) FragmentSignUp.this.getActivity(), "SignUpAsyncTask cannot parse JSON.");
                    return;
                }
                if (this.mStatus.equals("OK")) {
                    if (FragmentSignUp.this.mSignUpCallbacks != null) {
                        FragmentSignUp.this.mSignUpCallbacks.onFragmentSignUpOk(this.mJson);
                    }
                } else if (this.mStatus.equals("Already Taken")) {
                    Utils.alertExclusively((RobloxActivity) FragmentSignUp.this.getActivity(), R.string.UsernameTaken);
                } else if (this.mStatus.equals("AccountCreationFloodcheck")) {
                    Utils.alertExclusively((RobloxActivity) FragmentSignUp.this.getActivity(), R.string.AccountCreationFloodcheck);
                } else {
                    Utils.alertUnexpectedError((RobloxActivity) FragmentSignUp.this.getActivity(), this.mStatus);
                }
            }
        }
    }
}

