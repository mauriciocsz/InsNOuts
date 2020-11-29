package com.yrmew.insandouts;

public class editBillActivity_bill {


    int idBill;
    String nameBill;
    String typeBill;

    public int getIdBill() {
        return idBill;
    }

    public void setIdBill(int idBill) {
        this.idBill = idBill;
    }

    public editBillActivity_bill(String nameBill, String typeBill,int idBill) {
        this.nameBill = nameBill;
        this.idBill = idBill;
        this.typeBill = typeBill;
    }

    public String getNameBill() {
        return nameBill;
    }

    public void setNameBill(String nameBill) {
        this.nameBill = nameBill;
    }

    public String getTypeBill() {
        return typeBill;
    }

    public void setTypeBill(String typeBill) {
        this.typeBill = typeBill;
    }
}
