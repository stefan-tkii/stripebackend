package com.stripe;

public class RefundData 
{

    private String paymentId;

    public RefundData()
    {

    }

    public RefundData(String paymentId)
    {
        this.paymentId = paymentId;
    }

    public String getPaymentId() 
    {
        return this.paymentId;
    }

    public void setPaymentId(String paymentId) 
    {
        this.paymentId = paymentId;
    }
 
}