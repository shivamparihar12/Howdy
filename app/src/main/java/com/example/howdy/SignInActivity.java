package com.example.howdy;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.howdy.databinding.ActivitySignInBinding;
import com.example.howdy.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private String otpID;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private String storedVerificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        otpInputs();

        binding.otpLayout.setVisibility(View.GONE);
        binding.sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG + "click", "clicked");
                if (binding.phoneNo.getText().toString().isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG + "click", "clicked worked");
                    binding.progressCircular.setVisibility(View.VISIBLE);
                    Log.d(TAG + "phoneno", "+" + binding.ccp.getSelectedCountryCode().toString() + binding.phoneNo.getText().toString());
                    startPhoneNoVerification("+" + binding.ccp.getSelectedCountryCode().toString() + binding.phoneNo.getText().toString());
                    //PhoneAuthCredential credential=PhoneAuthProvider.getCredential(otpID,binding.ccp.getSelectedCountryCode().toString() + binding.phoneNo.getText().toString());
                    //signInWithPhoneAuthCredential(credential);
                }
            }
        });

        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.otp1.getText().toString().trim().isEmpty() ||
                        binding.otp2.getText().toString().trim().isEmpty() ||
                        binding.otp3.getText().toString().trim().isEmpty() ||
                        binding.otp4.getText().toString().trim().isEmpty() ||
                        binding.otp5.getText().toString().trim().isEmpty() ||
                        binding.otp6.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Please Enter Valid Otp", Toast.LENGTH_SHORT).show();
                } else {
                    String otp = binding.otp1.getText().toString().trim() +
                            binding.otp2.getText().toString().trim() +
                            binding.otp3.getText().toString().trim() +
                            binding.otp4.getText().toString().trim() +
                            binding.otp5.getText().toString().trim() +
                            binding.otp6.getText().toString().trim();
                    if (storedVerificationId != null) {
                        //verifyPhoneNumberWithCode(storedVerificationId, otp);
                        binding.progressCircular.setVisibility(View.VISIBLE);
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(storedVerificationId, otp);
                        Log.d(TAG + "credential", phoneAuthCredential.toString());
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    // Invalid request
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    // The SMS quota for the project has been exceeded
//                }

                // Show a message and update the UI
                Toast.makeText(SignInActivity.this, "Verification failed,Try Again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId;
                resendToken = token;
                binding.phoneNoLayout.setVisibility(View.GONE);
                binding.otpLayout.setVisibility(View.VISIBLE);
            }
        };
    }


    private void startPhoneNoVerification(String phoneNumber) {
//        PhoneAuthOptions options =
//                PhoneAuthOptions.newBuilder(mAuth)
//                        .setPhoneNumber(phoneNumber)       // Phone number to verify
//                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//                        .setActivity(this)                 // Activity (for callback binding)
//                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
//                        .build();
//        PhoneAuthProvider.verifyPhoneNumber(options);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                SignInActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        binding.progressCircular.setVisibility(View.GONE);
                        Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        binding.progressCircular.setVisibility(View.GONE);
                        binding.phoneNoLayout.setVisibility(View.GONE);
                        binding.otpLayout.setVisibility(View.VISIBLE);
                        storedVerificationId = s;
                    }
                }
        );
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
        // [END verify_with_code]
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            binding.progressCircular.setVisibility(View.GONE);
                            Log.d(TAG, "signInWithCredential:success");
//                            String id=mAuth
                            String userID = task.getResult().getUser().getUid();
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            Users users = new Users("+" + binding.ccp.getSelectedCountryCode() + binding.phoneNo.getText().toString(), userID);
                            firebaseDatabase.getReference().child("Users").child(userID).setValue(users);

                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish();


                            // Update UI
                            //startActivity(new Intent(MainActivity.this,BuildProfileActivity.class));
                            Toast.makeText(SignInActivity.this, "Succeeded", Toast.LENGTH_SHORT).show();
                        } else {
                            // Sign in failed, display a message and update the UI
                            binding.progressCircular.setVisibility(View.GONE);
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void otpInputs() {
        binding.otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!charSequence.toString().isEmpty()) {
                    binding.otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!charSequence.toString().isEmpty()) {
                    binding.otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!charSequence.toString().isEmpty()) {
                    binding.otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!charSequence.toString().isEmpty()) {
                    binding.otp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!charSequence.toString().isEmpty()) {
                    binding.otp6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}

