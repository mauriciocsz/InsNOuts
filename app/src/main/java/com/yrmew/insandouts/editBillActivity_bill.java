package com.yrmew.insandouts;

public class editBillActivity_bill {

    int idBill;
    String nameBill;
    String typeBill;

    public int getIdBill() {
        return idBill;
    }

    public editBillActivity_bill(String nameBill, String typeBill,int idBill) {
        this.nameBill = nameBill;
        this.idBill = idBill;
        this.typeBill = typeBill;
    }

    public String getNameBill() {
        return nameBill;
    }

    public String getTypeBill() {
        return typeBill;
    }

}
