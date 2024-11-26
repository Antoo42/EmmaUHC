package fr.anto42.emma.utils.data;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class FirebaseUtils {

    /*private static Firestore db;

    public void initializeFirebase() {
        String FIREBASE_KEY = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"uhc-saves\",\n" +
                "  \"private_key_id\": \"8eec94865db8f919d1a59f2065bbbf9cba3c08b1\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDlHyznCgIXX6be\\nbu1lhsREPSXskQ5kEntjecSpz/4zNnDKcXB2PcPfdHXeeLjDA1cpSzkQhMeDR+Lk\\nigh3h6+D82n0jdDSHSNu+K+73E4rd+idAdhMFCKYoMM174pCpddLbvtIC4mYizjb\\nvJE0Snf5vKojmJB7Cbg9yX88ZG2PWnKS1eeV4vlZgRhZv4e+l+3/dDrozXBNXRvd\\n89sCQdkoPDvm1ldsZwGINOtfI3AkkqwLprCSpQ3NrNDlrEZ1WUGey2v2I6WvHrsD\\nFUCWxwNvxom1pYg4oPEaDK02LJq/bidBmMK8/uTrVtpdH0+1iySvsD9O38P9ulih\\nRvrdxPQLAgMBAAECggEAG1mIU5/nNgEN+zlEjzzyCq896qo6DIoEPpCXgCm6z0Qt\\nXI0D/qlKy06sjzzb2TN54Q0Ap88hvbMEC4HKlvIUamzQ9gdDqQEfbKy01Xjrv9aV\\nZsCEoDg7FPsDslmAqIoBVBjQV/5Ak3vy8o/kyVgmUCJMMOmSPY2gd4eo732Rcm4A\\nnc7uSVqkXFXBwNgrpAdyKnGQtQ9KWxvsvVBAWQ9x3EeGHWJg4/nivvJJEb3aQs15\\nLNezJ2XQ763MVfuKZevk75cXnlBN7gGHuCMHx/W5VUCjPy0OoF2dmaDrQI1quNHX\\nLVf7A9/8mCc9lFj6OYKdZeLA6lbSES1J8y8lPI9bAQKBgQD/m4d7/WuWkEpG+2rT\\nCVkfviJqsGvtzQNcIWGShhA//+cqm/SW9QRQ+RHIzPHPXJ/mUIqJNcEd/CTcQ1fa\\nlGmnRvMUUNbq5jS4vPyK/CZkxaxKiXUxfJDxavPe2HgV3o06UTqU1SMFo2AuYe1O\\nFZI6NLCAJu5geJ/s6JcFioc4gQKBgQDleTxJv+awifzLluFnPmOMYqQ1HuOS+y/3\\nIW1ZS4o3roVa9RA1/9ob1X4sOUkGkGRCkAycJtTOr4G80GjDZnkdKG1T96LJLFjr\\naR6R1kRLtbhzeMDKAeucW8wa2/97FhHIWPLdkq2eJ93gMIRrX3qBjQZlbHnE6B1L\\n3ZBuaeZGiwKBgCtJ0MEr4vulxh6NV8MT0zcwAS4mYLDuRoJk2Nxyve/IzAKIVPz3\\n09pOxDdIli8zFZVw0HV6WBFW8XsoIInRFq+QsXwgULWJdSZQQ0jaeToZzBr3epR9\\n7yIjpdd4u6WggjR9Kzmnev3qdFCGnyTZPGGFKNsbPMWzyAncPi9fDZWBAoGBALfx\\nxOgBUjS1h9TS74BJPJcuAx8p0Zbij4J0YykvKm6UEQXmZ8ZBf17MdkWQ7f67BANv\\nYQxS/Edp3H+0OCZaID/FRk4GtZC5YdXi/8Q7k6d01ZSKeU/01h8SXrwxGcxa1opY\\n8uW2TEH5BxxNB/jiIlty5muq1rmGruU/iCVZ8mMdAoGAG2bZ0NnKIZKAhbNz3vT5\\nDSDDezdx0qFgAJ4bGLpQ4fVMxk3+TpfofI8PQKsP/WQJQzZS5tg7iojv7d1VYMdY\\npnaEAkV6j3jaflfE6vsJDS4PBtrDnMYOSQoMPC+b+xpQkA+9Nls2cpIvsWPDiM82\\nUwnHiAtFlgaV5RncOFuNi+o=\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"firebase-adminsdk-ug5gv@uhc-saves.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"111195994242101248821\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-ug5gv%40uhc-saves.iam.gserviceaccount.com\"\n" +
                "}";
        ByteArrayInputStream serviceAccount = new ByteArrayInputStream(FIREBASE_KEY.getBytes());

        //GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
                //.setCredentials(credentials)
                .setDatabaseUrl("https://your-project-id.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        db = FirestoreClient.getFirestore();

        System.out.println("Firebase initialisé avec succès");

    }

    public Firestore getFirestore() {
        return db;
    }*/
}
