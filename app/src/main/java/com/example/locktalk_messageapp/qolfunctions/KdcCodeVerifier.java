package com.example.locktalk_messageapp.qolfunctions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class KdcCodeVerifier {

    public interface VerificationCallback {
        void onVerificationResult(boolean isVerified);
    }

    public static void verify(String code, VerificationCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assuming 'kdcCodes' is the collection where KDC codes are stored
        // Each document in this collection has a 'code' field and a 'valid' field
        db.collection("kdcCodes").document(code).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Check if the code is valid
                        Boolean isValid = document.getBoolean("valid");
                        if (Boolean.TRUE.equals(isValid)) {
                            callback.onVerificationResult(true);
                            // mark the code as invalid after use
//                            document.getReference().update("valid", false);
                        } else {
                            callback.onVerificationResult(false);
                        }
                    } else {
                        // Document doesn't exist
                        callback.onVerificationResult(false);
                    }
                } else {
                    // Task failed with an exception
                    callback.onVerificationResult(false);
                }
            }
        });
    }
}
