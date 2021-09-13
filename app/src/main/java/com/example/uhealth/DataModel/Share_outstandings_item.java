package com.example.uhealth.DataModel;

import java.util.HashMap;

public class Share_outstandings_item {
    private String IdKey;
    private int expire;
    private String from_id;
    private String to_id;
    private String from_email, to_email;
    private String from_username, to_username;
    private String documentId;

    public Share_outstandings_item(HashMap<String,Object> in_map){
        IdKey = in_map.get("IdKey").toString();
        expire = ((Long)in_map.get("expire")).intValue();
        from_id = in_map.get("from_Id").toString();
        to_id = in_map.get("to_Id").toString();
        from_email = in_map.get("from_email").toString();
        from_username = in_map.get("from_username").toString();
        to_email = in_map.get("to_email").toString();
        to_username = in_map.get("to_username").toString();
    }

//    getter and setter
    public String getIdKey() {
        return IdKey;
    }

    public int getExpire() {
        return expire;
    }

    public String getFrom_id() {
        return from_id;
    }

    public String getTo_id() {
        return to_id;
    }

    public String getFrom_email() {
        return from_email;
    }

    public String getFrom_username() {
        return from_username;
    }

    public String getTo_email() {
        return to_email;
    }

    public String getTo_username() {
        return to_username;
    }


    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
