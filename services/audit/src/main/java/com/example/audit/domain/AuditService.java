package com.example.audit.domain;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuditService {

  private final FirestoreOptions firestoreOptions;
  private final Firestore firestore;

  public AuditService() {
    this.firestoreOptions = FirestoreOptions.getDefaultInstance();
    this.firestore = firestoreOptions.getService();
  }
  public AuditService(FirestoreOptions firestoreOptions, Firestore firestore) {
    this.firestoreOptions = firestoreOptions;
    this.firestore = firestore;
  }

  public AuditService(Firestore firestore){
    this.firestoreOptions = null;
    this.firestore = firestore;
  }

  public ApiFuture<WriteResult> auditQuote(String quote, String author, String book, String randomID) {
    DocumentReference doc = firestore.collection("books").document(author);

    Map<String, Object> data = new HashMap<>();
    data.put("created", new Date());
    data.put("quote",quote);
    data.put("author",author);
    data.put("book",book);
    data.put("randomID",randomID);

    return doc.set(data, SetOptions.merge());
  }

}
