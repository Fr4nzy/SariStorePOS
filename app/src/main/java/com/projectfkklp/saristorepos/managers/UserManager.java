package com.projectfkklp.saristorepos.managers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfkklp.saristorepos.classes.ValidationStatus;
import com.projectfkklp.saristorepos.interfaces.OnUserSave;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.repositories.AuthenticationRepository;
import com.projectfkklp.saristorepos.repositories.UserRepository;
import com.projectfkklp.saristorepos.utils.DateUtils;
import com.projectfkklp.saristorepos.validators.UserValidator;

import java.util.Date;
import java.util.List;


public class UserManager {
    public static CollectionReference getCollectionReference() {
        return  FirebaseFirestore.getInstance().collection("users");
    }
    public static Task<Void> saveUser(User user) {
        DocumentReference document = getCollectionReference().document(user.getId());

        return document.set(user);
    }

    public static Task<Void> deleteUser(){
        DocumentReference document = getCollectionReference().document(AuthenticationRepository.getCurrentAuthenticationUid());

        return document.delete();
    }

    public static void updateUserDailySales(double newTransactionAmount){
        UserRepository.getUserByCurrentAuthentication(user->{
            List<Double> dailySales = user.getDailySales();
            Date updatedAt = user.getDailySalesUpdatedAt();
            Date currentDate = new Date();
            //If no daily sales, adds a new transaction amount
            if (dailySales.size()==0) {
                dailySales.add(newTransactionAmount);
            }
            //
            else if(DateUtils.isSameDay(updatedAt, currentDate)){
                int lastItemIndex = dailySales.size()-1;
                Double updatedDailySale = dailySales.get(lastItemIndex)+newTransactionAmount;
                dailySales.set(dailySales.size()-1, updatedDailySale);
            }
            else {
                // updatedAt and currentDate is not equal

                // get gaps for difference of two or more, and fill it with zeros
                long gaps = DateUtils.calculateDaysDifference(updatedAt, currentDate) - 1;
                for (int i=0; i<gaps;i++){
                    dailySales.add(0.);
                }

                // Append the newTransactionAmount at the end of List
                dailySales.add(newTransactionAmount);
            }

            user.setDailySalesUpdatedAt(currentDate);
            UserManager.saveUser(user);
        });
    }

    public static void saveUser(User user, OnUserSave onUserSave){
        ValidationStatus validationStatus = UserValidator.validate(user);
        Task<Void> task = null;

        if (validationStatus.isValid()) {
            task = getCollectionReference().document(user.getId()).set(user);
        }

        onUserSave.onUserSave(user, validationStatus, task);
    }

}
