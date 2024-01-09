package com.projectfkklp.saristorepos.validators;

import android.content.Context;

import com.projectfkklp.saristorepos.classes.ValidationStatus;
import com.projectfkklp.saristorepos.interfaces.OnValidation;
import com.projectfkklp.saristorepos.models.UserStoreRelation;
import com.projectfkklp.saristorepos.repositories.UserStoreRelationRepository;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserStoreRelationValidator {
    public static void validate(Context context, UserStoreRelation userStoreRelation, OnValidation onValidation) {
        ValidationStatus validationStatus = new ValidationStatus();

        UserStoreRelationRepository
                .getRelationsByUserId(userStoreRelation.getUserId())
                .addOnSuccessListener(successTask->{
                    List<UserStoreRelation> userStoreRelations = successTask.toObjects(UserStoreRelation.class);
                    userStoreRelations = userStoreRelations.stream()
                        .filter(usr ->
                            Objects.equals(usr.getUserId(), userStoreRelation.getUserId())
                            && Objects.equals(usr.getStoreId(), userStoreRelation.getStoreId())
                        )
                        .collect(Collectors.toList());

                    if (userStoreRelations.size()>0){
                        validationStatus.putError("not_exists", "User Store Relation already exists");
                    }
                })
                .addOnFailureListener(failedTask-> ToastUtils.show(context, failedTask.getMessage()))
                .addOnCompleteListener(completeTask->{
                    if (userStoreRelation.getUserId().isEmpty()){
                        validationStatus.putError("userId", "User ID is required");
                    }

                    if (userStoreRelation.getStoreId().isEmpty()){
                        validationStatus.putError("storeId", "Store ID is required");
                    }

                    if (userStoreRelation.getRole() == null){
                        validationStatus.putError("role", "Role is required");
                    }

                    if (userStoreRelation.getStatus() == null){
                        validationStatus.putError("status", "Status is required");
                    }

                    onValidation.onValidation(validationStatus);
                })
        ;
    }
}
