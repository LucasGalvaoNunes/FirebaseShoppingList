package dev.lucasgnunes.firebaseshoppinglist;

import java.io.Serializable;

public class Item implements Serializable {
    private String documentID;

    private String name;
    private Integer amount;
    private Boolean checked;

    public Item() {
    }

    public Item(String name, Integer amount, Boolean checked) {
        this.name = name;
        this.amount = amount;
        this.checked = checked;
    }

    public Item(String documentID, String name, Integer amount, Boolean checked) {
        this.documentID  = documentID;
        this.name = name;
        this.amount = amount;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
