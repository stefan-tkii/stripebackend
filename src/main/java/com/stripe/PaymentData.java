package com.stripe;

public class PaymentData 
{

    private String id;
    private double amount;
    
    public PaymentData()
    {

    }

    public PaymentData(String id, double amount) 
    {
        this.id = id;
        this.amount = amount;
    }


    public String getId() 
    {
        return this.id;
    }

    public void setId(String id) 
    {
        this.id = id;
    }

    public double getAmount() 
    {
        return this.amount;
    }

    public void setAmount(double amount) 
    {
        this.amount = amount;
    }

}