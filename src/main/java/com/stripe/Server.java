package com.stripe;

import java.nio.file.Paths;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;
import static spark.Spark.port;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.StripeResponse;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;

public class Server 
{

    private static Gson gson = new Gson();

    static class CreatePayment 
    {
        @SerializedName("items")
        PaymentData[] items;

        public PaymentData[] getItems() 
        {
            return items;
        }
    }

    static class CreatePaymentResponse 
    {
        private String clientSecret;

        public CreatePaymentResponse(String clientSecret) 
        {
            this.clientSecret = clientSecret;
        }
    }

    static int calculateOrderAmount(PaymentData[] items) 
    {
        double amount = 0.0;
        for (PaymentData o : items) 
        {
            amount = o.getAmount();
            break;
        }
        return (int) amount * 100;
    }

    static String getOrderId(PaymentData[] items) 
    {
        String id = "";
        for (PaymentData o : items) 
        {
            id = o.getId();
            break;
        }
        return id;
    }

    static class CreateRefund 
    {
        @SerializedName("items")
        RefundData[] items;

        public RefundData[] getItems() 
        {
            return items;
        }
    }

    static class RefundResponse
    {
        private int code;
        private String body;

        public RefundResponse(int code, String body)
        {
            this.code = code;
            this.body = body;
        }
    }

    static String getPaymentDataId(RefundData[] items)
    {
        String id = "";
        for (RefundData o : items) 
        {
            id = o.getPaymentId();
            break;
        }
        return id;
    }

    public static void main(String[] args) 
    {
        port(4242);
        staticFiles.externalLocation(Paths.get("public").toAbsolutePath().toString());

        Stripe.apiKey = "sk_test_51JVv5lGfuf9wZawUZGzHMSXh9ydMkOIkf0VwQUjGN9SJh9pglfwr5i28amHUybZiBqaY17zgfdiv0BH7stLRpLKD00k3OFoPco";

        post("/create-payment-intent", (request, response) -> 
        {
            response.type("application/json");

            CreatePayment postBody = gson.fromJson(request.body(), CreatePayment.class);
            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                    .setDescription(new String(getOrderId(postBody.getItems()))).setCurrency("usd")
                    .setAmount(new Long(calculateOrderAmount(postBody.getItems()))).build();

            PaymentIntent intent = PaymentIntent.create(createParams);
            CreatePaymentResponse paymentResponse = new CreatePaymentResponse(intent.getClientSecret());
            return gson.toJson(paymentResponse);
        });

        post("/refund-payment", (request, response) ->
        {
            response.type("application/json");

            CreateRefund postBody = gson.fromJson(request.body(), CreateRefund.class);
            String id = new String(getPaymentDataId(postBody.getItems()));

            Refund refund = Refund.create(RefundCreateParams.builder()
            .setPaymentIntent(id)
            .build());
            
            StripeResponse resp = refund.getLastResponse();
            if(resp != null)
            {
                RefundResponse refundResponse = new RefundResponse(resp.code(), resp.body());
                return gson.toJson(refundResponse);
            }
            else
            {
                return gson.toJson("Error, no stripe response was found.");
            }
        });

    }

}