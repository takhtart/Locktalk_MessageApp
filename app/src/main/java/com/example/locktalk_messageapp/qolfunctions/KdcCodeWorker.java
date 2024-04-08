package com.example.locktalk_messageapp.qolfunctions;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Random;

public class KdcCodeWorker extends Worker {

    public KdcCodeWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Generate a new secure KDC key
        String newKdcKey = generateSecureKdcKey();

        // Fetch all employee documents and update their kdcKey field
        db.collection("employees").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                WriteBatch batch = db.batch();
                task.getResult().forEach(employeeDocument -> {
                    batch.update(employeeDocument.getReference(), "kdcKey", newKdcKey);
                });

                batch.commit().addOnCompleteListener(commitTask -> {
                    if (!commitTask.isSuccessful()) {
                        Log.e("KdcCodeWorker", "Error updating KDC keys for employees.", commitTask.getException());
                        // Handle the error (e.g. retry the task or log the failure)
                    }
                });
            } else {
                Log.e("KdcCodeWorker", "Error fetching employees for KDC key update.", task.getException());
                // Handle the error (e.g. retry the task or log the failure)
            }
        });

        // Return success or retry depending on the result of the Firestore operation
        // For simplicity, we're assuming success here
        return Result.success();
    }
    private String generateSecureKdcKey() {
        // Implement key generation logic here. The method will depend on the encryption scheme you're using.
        return String.format("%06d", new Random().nextInt(999999));
    }

//    @NonNull
//    @Override
//    public Result doWork() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        final CountDownLatch latch = new CountDownLatch(1);
//
//        // First, invalidate old KDC codes
//        invalidateOldCodes(db, () -> {
//            // Then, add new KDC codes
//            addNewCodes(db, () -> {
//                // Decrement the latch after all operations are complete
//                latch.countDown();
//            });
//        });
//
//        try {
//            // Wait for the operations to complete
//            latch.await();
//            return Result.success();
//        } catch (InterruptedException e) {
//            Log.e("KdcCodeWorker", "Operation was interrupted.", e);
//            return Result.retry();
//        }
//    }

//    private void invalidateOldCodes(FirebaseFirestore db, Runnable onComplete) {
//        db.collection("kdcCodes").whereEqualTo("valid", true).get()
//                .addOnSuccessListener(snapshot -> {
//                    WriteBatch batch = db.batch();
//                    snapshot.forEach(document -> {
//                        batch.delete(document.getReference());
//                    });
//                    batch.commit().addOnCompleteListener(task -> onComplete.run());
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("KdcCodeWorker", "Failed to invalidate old codes.", e);
//                    onComplete.run();
//                });
//    }

//    private void addNewCodes(FirebaseFirestore db, Runnable onComplete) {
//        WriteBatch batch = db.batch();
//        for (int i = 0; i < 5; i++) {
//            Integer newCode = generateRandomCode();
//            Map<String, Object> data = new HashMap<>();
//            data.put("valid", true);
//            batch.set(db.collection("kdcCodes").document(String.valueOf(newCode)), data);
//        }
//        batch.commit().addOnCompleteListener(task -> onComplete.run());
//    }


//    private int generateRandomCode() {
//        return 100000 + new Random().nextInt(900000);
//    }
}
